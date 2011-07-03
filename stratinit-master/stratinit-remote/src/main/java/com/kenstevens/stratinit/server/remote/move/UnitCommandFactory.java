package com.kenstevens.stratinit.server.remote.move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.main.Spring;
import com.kenstevens.stratinit.model.AttackType;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.server.daoservice.MoveSeen;
import com.kenstevens.stratinit.type.CoordMeasure;
import com.kenstevens.stratinit.type.SectorCoords;

@Service
public class UnitCommandFactory {
	@Autowired
	private Spring spring;
	@Autowired
	private UnitDao unitDao;

	public UnitsMove getSIUnitsMove(Nation nation, List<SIUnit> siunits,
			SectorCoords targetCoords, WorldView worldView) {
		List<Unit> units = getUnits(nation, siunits, targetCoords);
		UnitsToMove unitsToMove = new UnitsToMove(nation, AttackType.INITIAL_ATTACK, nation, units, targetCoords);
		return getUnitsMove(unitsToMove, worldView);
	}

	public UnitsMove getUnitsMove(UnitsToMove unitsToMove, WorldView worldView) {
		return spring.autowire(new UnitsMove( unitsToMove, worldView ));
	}

	private List<Unit> getUnits(Nation nation, List<SIUnit> siunits,
			SectorCoords targetCoords) {
		// Put bombers first to make attacks more effective
		List<Unit> bombers = new ArrayList<Unit>();
		List<Unit> others = new ArrayList<Unit>();
		for (SIUnit siunit : siunits) {
			Unit unit = unitDao.findUnit(siunit.id);
			if (unit == null) {
				continue;
			}
			if (!unit.isAlive()) {
				continue;
			}
			if (unit.getCoords().equals(targetCoords)) {
				// already there
				continue;
			}
			if (!unit.getNation().equals(nation)) {
				throw new IllegalStateException("You don't own " + unit);
			}
			if (unit.isBomber()) {
				bombers.add(unit);
			} else {
				others.add(unit);
			}
		}
		bombers.addAll(others);
		return bombers;
	}

	public UnitAttacksSector getUnitAttacksSector(Nation actor, AttackType attackType, Unit unit,
			WorldSector targetSector, WorldView worldView, MoveSeen moveSeen) {
		return spring.autowire(new UnitAttacksSector( actor, attackType, unit,
				targetSector, worldView , moveSeen));
	}

	public UnitAttacksUnit getUnitAttacksUnit(WorldView worldView, Nation actor, AttackType attackType,
			Unit unit, Unit enemyUnit, WorldSector targetSector, MoveSeen moveSeen) {

		return spring.autowire(new UnitAttacksUnit( worldView, actor, attackType,
				unit, enemyUnit, targetSector , moveSeen));
	}

	public UnitMoves getUnitMoves(AttackType attackType, Unit unit, WorldSector worldSector,
			WorldView worldView) {
		return spring.autowire(new UnitMoves( attackType, unit, worldSector,
				worldView));
	}

	public UnitBombsSector getUnitBombsSector(Nation actor, Unit bomber,
			WorldSector targetSector, Collection<Unit> units,
			AttackReadiness attackReadiness) {
		return spring.autowire(new UnitBombsSector( actor, bomber,
				targetSector, units, attackReadiness ));
	}

	public Interdiction getInterdiction(Unit targetUnit, SectorCoords excludeCoords) {
		return spring.autowire(new Interdiction( targetUnit, excludeCoords ));
	}
	public Interception getInterception(UnitsToMove unitsToMove, SectorCoords coords) {
		return spring.autowire(new Interception(unitsToMove, coords));
	}

	public LaunchRocket getLaunchRocket(CoordMeasure coordMeasure, Unit unit, SectorCoords target, MoveSeen moveSeen) {
		return spring.autowire(new LaunchRocket( coordMeasure, unit, target , moveSeen));
	}

	public UnitsCede getSIUnitsCede(Nation nation, List<SIUnit> siunits,
			int nationId, WorldView worldView) {
		List<Unit> units = getUnits(nation, siunits, null);
		return spring.autowire(new UnitsCede( nation, units, nationId, worldView ));
	}
}
