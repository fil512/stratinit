package com.kenstevens.stratinit.server.remote.move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.dto.SIBattleLog;
import com.kenstevens.stratinit.model.AttackType;
import com.kenstevens.stratinit.model.BattleLog;
import com.kenstevens.stratinit.model.CityCapturedBattleLog;
import com.kenstevens.stratinit.model.FlakBattleLog;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitBase;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.LogDaoService;
import com.kenstevens.stratinit.server.daoservice.MoveSeen;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.UnitType;
import com.kenstevens.stratinit.util.AttackHelper;

@Scope("prototype")
@Component
public class UnitAttacksSector {
	@Autowired
	private UnitDaoService unitDaoService;
	@Autowired
	private UnitDao unitDao;
	@Autowired
	private SectorDaoService sectorDaoService;
	@Autowired
	private LogDaoService logDaoService;
	@Autowired
	private UnitCommandFactory unitCommandFactory;

	private final AttackType attackType;
	private final Unit attackingUnit;
	private final WorldSector targetSector;
	private final WorldView worldView;
	private final MoveSeen moveSeen;
	private final Nation actor;

	public UnitAttacksSector(Nation actor, AttackType attackType, Unit attackingUnit, WorldSector targetSector, WorldView worldView, MoveSeen moveSeen) {
		this.actor = actor;
		this.attackType = attackType;
		this.attackingUnit = attackingUnit;
		this.targetSector = targetSector;
		this.worldView = worldView;
		this.moveSeen = moveSeen;
	}

	public Result<None> attack() {
		AttackReadiness attackReadiness = new AttackReadiness(attackType, attackingUnit, targetSector, worldView);
		Result<None> result = attackReadiness.canAttack(null);
		if (!result.isSuccess()) {
			return result;
		}
		int flakDamage = BattleLog.NO_DAMAGE;
		if (targetSector.getCityDefense(attackingUnit) > 0) {
			flakDamage = AttackHelper.randomFlakDamage(targetSector.getCityDefense(attackingUnit));
			unitDaoService.damage(attackingUnit, flakDamage);
			if (!attackingUnit.isAlive()) {
				FlakBattleLog flakBattleLog = new FlakBattleLog(AttackType.FLAK,
						attackingUnit, targetSector.getNation(), targetSector.getCoords(), flakDamage);
				logDaoService.persist(flakBattleLog);
				return new Result<None>(new SIBattleLog(actor, flakBattleLog), false);
			}
		}
		Collection<Unit> units = Lists.newArrayList(unitDao.getUnits(targetSector));
		if (!targetSector.isWater() && attackingUnit.isBomber()) {
			return bombTarget(units, attackReadiness, flakDamage);
		}
		Unit enemyUnit = getUnitToAttack(units);
		if (enemyUnit != null) {
			return attack(enemyUnit, attackReadiness, flakDamage);
		} else if (targetSector.isEmptyCity()) {
			return takeCity(attackReadiness);
		} else {
			return new Result<None>("Nothing to attack.", false);
		}
	}

	private Result<None> bombTarget(Collection<Unit> units, AttackReadiness attackReadiness, int flakDamage) {
		UnitBombsSector unitBombsSector = unitCommandFactory.getUnitBombsSector(actor, attackingUnit, targetSector, units, attackReadiness);
		return unitBombsSector.bomb(flakDamage);
	}

	private Result<None> attack(Unit enemyUnit, AttackReadiness attackReadiness, int flakDamage) {
		UnitAttacksUnit unitsAttackUnit = unitCommandFactory.getUnitAttacksUnit(worldView, actor, attackType, attackingUnit, enemyUnit, targetSector, moveSeen);
		return unitsAttackUnit.attack(attackReadiness, null, flakDamage);
	}

	private Unit getUnitToAttack(Collection<Unit> units) {
		if (attackType == AttackType.INTERCEPTION) {
			removeIntercepted(units);
		}
		if (units.isEmpty()) {
			return null;
		}

		if (targetSector.isWater()) {
			// we're at sea, pick the ship if there is one
			for (Unit unit : units) {
				if (unit.isNavy()) {
					return unit;
				}
			}
		}
		if (attackType == AttackType.INTERCEPTION) {
			// when intercepting, prefer fighters then air units.  e.g. heli carrying inf
			for (Unit unit : units) {
				if (unit.getType() == UnitType.FIGHTER) {
					return unit;
				}
			}
			for (Unit unit : units) {
				if (unit.isAir()) {
					return unit;
				}
			}
		}

		return getUnitWithHighestMultiplier(units);
	}

	private void removeIntercepted(Collection<Unit> units) {
		List<Unit> unitsToRemove = new ArrayList<Unit>();
		for (Unit unit : units) {
			if (unit.isIntercepted()) {
				unitsToRemove.add(unit);
			}
		}
		units.removeAll(unitsToRemove);
	}

	private Unit getUnitWithHighestMultiplier(Collection<Unit> units) {
		UnitBase attackerBase = attackingUnit.getUnitBase();
		int maxMultiplier = -1;
		Unit maxUnit = null;

		for (Unit defender : units) {
			if (!AttackHelper.canAttack(attackType, worldView.getMyRelation(defender.getNation()))) {
				continue;
			}
			int multiplier = UnitBase.getMultiplier(attackerBase, defender.getUnitBase());
			if (multiplier >= maxMultiplier) {
				maxMultiplier = multiplier;
				maxUnit = defender;
			}
		}
		return maxUnit;
	}

	private Result<None> takeCity(AttackReadiness attackReadiness) {
		Nation oldOwner = sectorDaoService.captureCity(attackingUnit.getNation(),
				targetSector);
		unitDaoService.damage(attackingUnit, Constants.CITY_CAPTURE_DAMAGE);
		CityCapturedBattleLog battleLog = new CityCapturedBattleLog(attackType, attackingUnit,
				oldOwner, targetSector.getCoords());
		
		if (attackingUnit.isAlive()) {
			attackingUnit.setCoords(targetSector.getCoords());
			attackingUnit.decreaseMobility(attackReadiness.costToAttack());
			unitDaoService.merge(attackingUnit);
		} else {
			battleLog.setAttackerDied(true);
		}
		logDaoService.persist(battleLog);
		return new Result<None>(new SIBattleLog(actor, battleLog));
	}
}
