package com.kenstevens.stratinit.server.remote.attack;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kenstevens.stratinit.model.MoveCost;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.remote.TwoPlayerBase;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

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
		assertTrue(result.toString(), dest.getUnitBase().getMobility() > dest.getMobility());
		assertTrue(result.toString(), dest.getUnitBase().getAmmo() > dest.getAmmo());
		assertFalse(result.toString(), transport.isAlive());
		assertFalse(result.toString(), inf.isAlive());
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
		assertFalse(result.toString(), carrier.isAlive());
		assertFalse(result.toString(), fighters[CARRIER_CAPACITY-1].isAlive());
		assertTrue(result.toString(), fighters[CARRIER_CAPACITY].isAlive());
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
		assertTrue(result.toString(), dest.getUnitBase().getMobility() > dest.getMobility());
		assertTrue(result.toString(), dest.getUnitBase().getAmmo() > dest.getAmmo());
		assertFalse(result.toString(), transport.isAlive());
		assertTrue(result.toString(), inf.isAlive());
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
		assertTrue(result.toString(), transport.isAlive());
	}
}
