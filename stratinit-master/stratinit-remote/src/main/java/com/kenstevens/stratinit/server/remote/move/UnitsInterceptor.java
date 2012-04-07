package com.kenstevens.stratinit.server.remote.move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
import com.kenstevens.stratinit.type.SectorCoords;

@Scope("prototype")
@Component
public class UnitsInterceptor {
	@Autowired
	private UnitCommandFactory unitCommandFactory;
	@Autowired
	private UnitDaoService unitDaoService;
	@Autowired
	private SectorDaoService sectorDaoService;
	@Autowired
	private UnitDao unitDao;

	private final WorldView worldView;
	private final Collection<Unit> units;
	private final Unit targetUnit;
	private final Nation nation;
	private final SectorCoords excludeCoords;

	public UnitsInterceptor(Nation nation, WorldView worldView, Unit targetUnit, SectorCoords excludeCoords, Collection<Unit> units) {
		this.nation = nation;
		this.worldView = worldView;
		this.targetUnit = targetUnit;
		this.excludeCoords = excludeCoords;
		this.units = units;
	}

	public void unitsIntercept(Result<None> retval) {
		WorldSector targetSector = worldView.getWorldSector(targetUnit);
		Interceptors interceptors = new Interceptors();
		for (Unit unit : units) {
			targetSector = sectorDaoService.refreshWorldSector(nation,
					worldView, targetSector);
			Attack attack = new Attack(targetSector);
			if (!attack.isAttackable(AttackType.INTERDICTION)) {
				break;
			}
			if (!canIntercept(unit, attack, targetSector)) {
				continue;
			}

			UnitsToMove unitsToIntercept = getUnitsToMove(interceptors, unit);
			WorldSector oldSector = worldView.getWorldSector(unit);
			UnitsMove unitsMove = unitCommandFactory.getUnitsMove(
					unitsToIntercept, worldView);
			Result<None> interdictResult = new Result<None>(unitsMove.move());
			worldView.setWorldSector(sectorDaoService.refreshWorldSector(
					nation, worldView, oldSector));
			worldView.setWorldSector(sectorDaoService.refreshWorldSector(
					nation, worldView, worldView.getWorldSector(unit)));
			interdictResult.setSuccess(!interdictResult.isSuccess());
			retval.and(interdictResult);
			interceptors.flyBack(unitDaoService, worldView);
		}
	}

	private UnitsToMove getUnitsToMove(Interceptors interceptors, Unit unit) {
		List<Unit> unitList = new ArrayList<Unit>();

		unitList.add(unit);
		if (unit.isAir()) {
			interceptors.add(unit);
		}
		UnitsToMove unitsToInterdict = new UnitsToMove(
				targetUnit.getNation(), AttackType.INTERDICTION, nation,
				unitList, targetUnit.getCoords());
		return unitsToInterdict;
	}

	private boolean canIntercept(Unit unit, Attack attack, WorldSector targetSector) {
		if (!attack.canAttack(worldView, AttackType.INTERDICTION, unit)) {
			return false;
		}
		
		if (!canAttack(unit)) {
			return false;
		}
		
		return canIntercept(unit, targetSector);
	}
	
	private boolean canAttack(Unit unit) {
		if (unit.isHurt()) {
			return false;
		}
		if (unit.getAmmo() <= 0) {
			return false;
		}
		// TODO REF magic number
		// For patrol boats
		if (unit.getAttack() == 1) {
			if (targetUnit.getAttack() > 1 && targetUnit.getHp() > 1) {
				return false;
			}
		}
		return true;
	}

	private boolean canIntercept(Unit unit, WorldSector targetSector) {
		// TODO REF excludeCoords is overloaded to mean a different
		// attack type
		if (unit.isNavy() && excludeCoords == null) {
			Supply supply = new Supply(worldView);
			if (!supply.inSupply(unit)) {
				return false;
			}
			if (isEscort(worldView, unit)) {
				return false;
			}
		} else if (unit.isAir()) {
			if (targetSector.getFlak() >= unit.getHp()) {
				return false;
			}
		}
		return true;
	}

	private boolean isEscort(WorldView worldView, Unit unit) {
		if (!unit.isEscort()) {
			return false;
		}
		if (isAnEscort(worldView, unit)) {
			if (targetUnit.isSubmarine()) {
				return false; // escorts can break formation to chase subs
			} else if (worldView.distance(unit.getCoords(),
					targetUnit.getCoords()) > 1) {
				return true;
			}
		}
		return false;
	}

	private boolean isAnEscort(WorldView worldView, Unit unit) {
		return unitDao.isAnEscort(worldView, unit);
	}
}
