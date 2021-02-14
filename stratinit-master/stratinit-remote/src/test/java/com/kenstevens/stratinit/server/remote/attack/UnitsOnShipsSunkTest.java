package com.kenstevens.stratinit.server.remote.attack;

import com.kenstevens.stratinit.model.MoveCost;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.remote.TwoPlayerBase;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UnitsOnShipsSunkTest extends TwoPlayerBase {
	@Autowired protected SectorDaoService sectorDaoServiceImpl;
	private static final SectorCoords PORT = new SectorCoords(2, 2);
	private static final SectorCoords SEA = new SectorCoords(3, 0);
	private static final SectorCoords SEA2 = new SectorCoords(4, 0);

	@Test
	public void infOnXportAtSeaSunk() {
		declareWar();
		Unit inf = unitDaoService.buildUnit(nationThem, SEA, UnitType.INFANTRY);
		Unit transport = unitDaoService.buildUnit(nationThem, SEA, UnitType.TRANSPORT);
		Unit dest = unitDaoService.buildUnit(nationMe, SEA2, UnitType.DESTROYER);

		transport.setHp(1);
		unitDao.merge(transport);

		Result<MoveCost> result = moveUnits(
				makeUnitList(dest), SEA);
		assertResult(result);
		assertTrue(dest.getUnitBase().getMobility() > dest.getMobility(), result.toString());
		assertTrue(dest.getUnitBase().getAmmo() > dest.getAmmo(), result.toString());
		assertFalse(transport.isAlive(), result.toString());
		assertFalse(inf.isAlive(), result.toString());
	}
	@Test
	public void someAirOnCarrierAtSeaSunk() {
		declareWar();
		Unit[] fighters = new Unit[CARRIER_CAPACITY+1];
		for (int i = 0; i < CARRIER_CAPACITY + 1; ++i) {
			fighters[i] = unitDaoService.buildUnit(nationThem, SEA, UnitType.FIGHTER);
		}
		Unit carrier = unitDaoService.buildUnit(nationThem, SEA, UnitType.CARRIER);
		Unit dest = unitDaoService.buildUnit(nationMe, SEA2, UnitType.DESTROYER);

		carrier.setHp(1);
		unitDao.merge(carrier);

		Result<MoveCost> result = moveUnits(
				makeUnitList(dest), SEA);
		assertResult(result);
		assertMoved(result, dest);
		assertFired(result, dest);
		assertFalse(carrier.isAlive(), result.toString());
		assertFalse(fighters[CARRIER_CAPACITY - 1].isAlive(), result.toString());
		assertTrue(fighters[CARRIER_CAPACITY].isAlive(), result.toString());
	}
	
	@Test
	public void infOnXportInPortNotSunk() {
		declareWar();
		Unit transport = unitDaoService.buildUnit(nationThem, PORT, UnitType.TRANSPORT);
		Unit inf = unitDaoService.buildUnit(nationThem, PORT, UnitType.INFANTRY);
		Unit dest = unitDaoService.buildUnit(nationMe, SEA, UnitType.DESTROYER);
		sectorDaoServiceImpl.captureCity(nationThem, PORT);

		transport.setHp(1);
		unitDao.merge(transport);

		Result<MoveCost> result = moveUnits(
				makeUnitList(dest), PORT);
		assertResult(result);
		assertTrue(dest.getUnitBase().getMobility() > dest.getMobility(), result.toString());
		assertTrue(dest.getUnitBase().getAmmo() > dest.getAmmo(), result.toString());
		assertFalse(transport.isAlive(), result.toString());
		assertTrue(inf.isAlive(), result.toString());
	}
	
	@Test
	public void infOnXportAtSeaNotHit() {
		declareWar();
		Unit transport = unitDaoService.buildUnit(nationThem, SEA, UnitType.TRANSPORT);
		Unit inf = unitDaoService.buildUnit(nationThem, SEA, UnitType.INFANTRY);
		Unit dest = unitDaoService.buildUnit(nationMe, SEA2, UnitType.DESTROYER);

		Result<MoveCost> result = moveUnits(
				makeUnitList(dest), SEA);
		assertResult(result);
		assertMoved(result, dest);
		assertFired(result, dest);
		assertNotDamaged(result, inf);
		assertTrue(transport.isAlive(), result.toString());
	}
}
