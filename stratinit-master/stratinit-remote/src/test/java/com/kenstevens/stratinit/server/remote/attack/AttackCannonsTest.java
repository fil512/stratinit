package com.kenstevens.stratinit.server.remote.attack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.kenstevens.stratinit.model.MoveCost;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.remote.TwoPlayerBase;
import com.kenstevens.stratinit.type.MoveType;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

public class AttackCannonsTest extends TwoPlayerBase {
	private static final SectorCoords PORT = new SectorCoords(2, 2);
	private static final SectorCoords SEA = new SectorCoords(3, 2);

	@Test
	public void destAttacksEMpty() {
		declareWar();
		Unit dest = unitDaoService.buildUnit(nationMe, SEA,
				UnitType.DESTROYER);
		sectorDaoService.captureCity(nationThem, PORT);
		Result<MoveCost> result = moveUnits(
				makeUnitList(dest), PORT);
		assertFalseResult(result);
		assertFalse(result.toString().contains("Cannons hit"));
		assertNotDamaged(result, dest);
		assertNotFired(result, dest);
	}
	
	@Test
	public void destAttacksBase() {
		declareWar();
		Unit dest = unitDaoService.buildUnit(nationMe, SEA,
				UnitType.DESTROYER);
		sectorDaoService.captureCity(nationThem, PORT);
		Unit sat = unitDaoService.buildUnit(nationThem, PORT, UnitType.SATELLITE);
		Result<MoveCost> result = moveUnits(
				makeUnitList(dest), PORT);
		assertResult(result);
		assertTrue(result.toString().contains("Cannons hit"));
		assertDamaged(result, dest);
		assertEquals(result.toString(), dest.getHp() + 1, dest.getUnitBase().getHp());
		assertFired(result, dest);
		assertDamaged(result, sat);
		assertEquals(MoveType.ATTACK, result.getValue().getMoveType());
	}

	@Test
	public void destAttacksFort() {
		declareWar();
		Unit dest = unitDaoService.buildUnit(nationMe, SEA,
				UnitType.DESTROYER);
		sectorDaoService.captureCity(nationThem, PORT);
		setBuild(PORT, UnitType.INFANTRY);
		Unit sat = unitDaoService.buildUnit(nationThem, PORT, UnitType.SATELLITE);
		Result<MoveCost> result = moveUnits(
				makeUnitList(dest), PORT);
		assertResult(result);
		assertTrue(result.toString().contains("Cannons hit"));
		assertDamaged(result, dest);
		assertFired(result, dest);
		assertDamaged(result, sat);
	}

	@Test
	public void destAttacksAirport() {
		declareWar();
		Unit dest = unitDaoService.buildUnit(nationMe, SEA,
				UnitType.DESTROYER);
		sectorDaoService.captureCity(nationThem, PORT);
		setBuild(PORT, UnitType.FIGHTER);
		Unit sat = unitDaoService.buildUnit(nationThem, PORT, UnitType.SATELLITE);
		Result<MoveCost> result = moveUnits(
				makeUnitList(dest), PORT);
		assertResult(result);
		assertFalse(result.toString().contains("Cannons hit"));
		assertNotDamaged(result, dest);
		assertFired(result, dest);
		assertDamaged(result, sat);
	}

	@Test
	public void destAttacksPort() {
		declareWar();
		Unit dest = unitDaoService.buildUnit(nationMe, SEA,
				UnitType.DESTROYER);
		sectorDaoService.captureCity(nationThem, PORT);
		setBuild(PORT, UnitType.TRANSPORT);
		Unit sat = unitDaoService.buildUnit(nationThem, PORT, UnitType.SATELLITE);
		Result<MoveCost> result = moveUnits(
				makeUnitList(dest), PORT);
		assertResult(result);
		assertTrue(result.toString().contains("Cannons hit"));
		assertDamaged(result, dest);
		assertFired(result, dest);
		assertDamaged(result, sat);
	}

}
