package com.kenstevens.stratinit.server.remote.move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.model.AttackType;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.move.Attack;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.supply.Supply;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.util.AttackHelper;

@Scope("prototype")
@Component
public class Interdiction {
	@Autowired
	private UnitDao unitDao;
	@Autowired
	private GameDao gameDao;
	@Autowired
	private SectorDaoService sectorDaoService;
	@Autowired
	private UnitCommandFactory unitCommandFactory;
	@Autowired
	private UnitDaoService unitDaoService;

	private final Unit targetUnit;
	private final SectorCoords excludeCoords;

	public Interdiction(Unit targetUnit, SectorCoords excludeCoords) {
		this.targetUnit = targetUnit;
		this.excludeCoords = excludeCoords;
	}

	/**
	 * @return success means no interdiction
	 */
	public Result<None> interdict() {
		Result<None> retval = Result.trueInstance();
		Collection<Nation> nations = unitDao
				.getOtherNationsThatSeeThisUnit(targetUnit);
		Map<Nation, RelationType> theirRelations = gameDao
				.getTheirRelationTypesAsMap(targetUnit.getNation());
		for (Nation nation : nations) {
			if (!AttackHelper.canAttack(AttackType.INTERDICTION,
					theirRelations.get(nation))) {
				continue;
			}
			WorldView worldView = sectorDaoService.getInterdictionWorldView(
					targetUnit, nation);
			Collection<Unit> units;
			if (excludeCoords != null) {
				units = Lists.newArrayList(unitDao
						.getUnitsThatCanCounterFireThisUnit(worldView, nation,
								targetUnit, excludeCoords));
			} else {
				units = Lists.newArrayList(unitDao
						.getUnitsThatCanInterdictThisUnit(worldView, nation,
								targetUnit));
			}
			WorldSector targetSector = worldView.getWorldSector(targetUnit);
			Interceptors interceptors = new Interceptors();
			for (Unit unit : units) {
				targetSector = sectorDaoService.refreshWorldSector(nation,
						worldView, targetSector);
				Attack attack = new Attack(targetSector);
				if (!attack.isAttackable(AttackType.INTERDICTION)) {
					break;
				}
				if (!attack.canAttack(worldView, AttackType.INTERDICTION, unit)) {
					continue;
				}
				if (unit.isHurt()) {
					continue;
				}
				if (unit.getAmmo() <= 0) {
					continue;
				}
				// TODO REF magic number
				// For patrol boats
				if (unit.getAttack() == 1) {
					if (targetUnit.getAttack() > 1 && targetUnit.getHp() > 1) {
						continue;
					}
				}
// TODO REF excludeCoords is overloaded to mean a different attack type
				if (unit.isNavy() && excludeCoords == null) {
					Supply supply = new Supply(worldView);
					if (!supply.inSupply(unit)) {
						continue;
					}
					if (isEscort(worldView, unit)) {
						continue;
					}
				} else if (unit.isAir()) {
					if (targetSector.getFlak() >= unit.getHp()) {
						continue;
					}
				}
				List<Unit> unitList = new ArrayList<Unit>();
				unitList.add(unit);
				if (unit.isAir()) {
					interceptors.add(unit);
				}
				WorldSector oldSector = worldView.getWorldSector(unit);
				UnitsToMove unitsToInterdict = new UnitsToMove(
						targetUnit.getNation(), AttackType.INTERDICTION,
						nation, unitList, targetUnit.getCoords());
				UnitsMove unitsMove = unitCommandFactory.getUnitsMove(
						unitsToInterdict, worldView);
				Result<None> interdictResult = new Result<None>(
						unitsMove.move());
				worldView.setWorldSector(sectorDaoService.refreshWorldSector(
						nation, worldView, oldSector));
				worldView.setWorldSector(sectorDaoService.refreshWorldSector(
						nation, worldView, worldView.getWorldSector(unit)));
				interdictResult.setSuccess(!interdictResult.isSuccess());
				retval.and(interdictResult);
				interceptors.flyBack(unitDaoService, worldView);
			}
		}
		return retval;
	}

	private boolean isEscort(WorldView worldView, Unit unit) {
		if (!unit.isEscort()) {
			return false;
		}
		if (isAnEscort(worldView, unit)) {
			if (targetUnit.isSubmarine()) {
				return false; // escorts can break formation to chase subs
			} else if (worldView.distance(unit.getCoords(), targetUnit.getCoords()) > 1) {
				return true;
			}
		}
		return false;
	}

	private boolean isAnEscort(WorldView worldView, Unit unit) {
		return unitDao.isAnEscort(worldView, unit);
	}
}
