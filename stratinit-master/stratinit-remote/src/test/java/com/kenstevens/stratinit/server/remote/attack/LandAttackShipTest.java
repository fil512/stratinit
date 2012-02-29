package com.kenstevens.stratinit.server.remote.attack;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.remote.TwoPlayerBase;
import com.kenstevens.stratinit.server.remote.move.cost.MoveCost;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

public class LandAttackShipTest extends TwoPlayerBase {
	@Autowired protected SectorDaoService sectorDaoServiceImpl;
	private static final SectorCoords LAND = new SectorCoords(2, 0);
	private static final SectorCoords SEA = new SectorCoords(3, 0);
	private static final SectorCoords SEA2 = new SectorCoords(4, 0);

	@Test
	public void infCannotAttackShip() {
		declareWar();
		Unit pat = unitDaoService.buildUnit(nationThem, SEA, UnitType.PATROL);
		Unit inf = unitDaoService.buildUnit(nationMe, LAND, UnitType.INFANTRY);
		Result<MoveCost> result = moveUnits(
				makeUnitList(inf), SEA);
		assertFalseResult(result);
		assertNotMoved(result, inf);
		assertTrue(result.toString(), inf.getUnitBase().getAmmo() == inf.getAmmo());
		assertNotDamaged(result, pat);
	}

	@Test
	public void tankCanAttackShip() {
		declareWar();
		Unit pat = unitDaoService.buildUnit(nationThem, SEA, UnitType.PATROL);
		Unit tank = unitDaoService.buildUnit(nationMe, LAND, UnitType.TANK);
		Result<MoveCost> result = moveUnits(
				makeUnitList(tank), SEA);
		assertResult(result);
		assertMoved(result, tank);
		assertDamaged(result, pat);
		assertNotDamaged(result, tank);
	}

	@Test
	public void tankOnShipCannotAttackShip() {
		declareWar();
		Unit pat = unitDaoService.buildUnit(nationThem, SEA, UnitType.PATROL);
		unitDaoService.buildUnit(nationMe, SEA2, UnitType.TRANSPORT);
		Unit tank = unitDaoService.buildUnit(nationMe, SEA2, UnitType.TANK);
		Result<MoveCost> result = moveUnits(
				makeUnitList(tank), SEA);
		assertFalseResult(result);
		assertNotMoved(result, tank);
		assertNotDamaged(result, tank);
		assertNotDamaged(result, pat);
	}
}
