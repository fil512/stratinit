package com.kenstevens.stratinit.server.remote.move;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.kenstevens.stratinit.model.AttackType;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitBase;
import com.kenstevens.stratinit.server.remote.StratInitWebBase;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

public class AttackValueHelperTest extends StratInitWebBase {
	private static final SectorCoords SEA = new SectorCoords(4,0);
	
	@Before
	public void join() {
		joinGamePlayerMe();
	}
	
	@Test
	public void testBBDestValues() {
		Unit att = unitDaoService.buildUnit(nationMe, SEA, UnitType.BATTLESHIP);
		Unit def = unitDaoService.buildUnit(nationMe, SEA, UnitType.DESTROYER);
		UnitBase attType = UnitBase.getUnitBase(UnitType.BATTLESHIP);
		assertEquals(attType.getAttack(), AttackValueHelper.getInitialAttackValue(AttackType.INITIAL_ATTACK, att, def));
	}

	@Test
	public void testTankInfValues() {
		Unit att = unitDaoService.buildUnit(nationMe, SEA, UnitType.TANK);
		Unit def = unitDaoService.buildUnit(nationMe, SEA, UnitType.INFANTRY);
		UnitBase attType = UnitBase.getUnitBase(UnitType.TANK);
		assertEquals(attType.getAttack(), AttackValueHelper.getInitialAttackValue(AttackType.INITIAL_ATTACK, att, def));
	}
	@Test
	public void testBombCounterValues() {
		Unit att = unitDaoService.buildUnit(nationMe, SEA, UnitType.NAVAL_BOMBER);
		Unit def = unitDaoService.buildUnit(nationMe, SEA, UnitType.DESTROYER);
		assertEquals(Constants.MIN_ATTACK, AttackValueHelper.getInitialAttackValue(AttackType.COUNTER_ATTACK, att, def));
	}


	@Test
	public void testNavyVsAirCounterValues() {
		Unit att = unitDaoService.buildUnit(nationMe, SEA, UnitType.DESTROYER);
		Unit def = unitDaoService.buildUnit(nationMe, SEA, UnitType.FIGHTER);
		assertEquals(Constants.MIN_ATTACK, AttackValueHelper.getInitialAttackValue(AttackType.COUNTER_ATTACK, att, def));
	}

	@Test
	public void testCapVsSubCounterValues() {
		Unit att = unitDaoService.buildUnit(nationMe, SEA, UnitType.CARRIER);
		Unit def = unitDaoService.buildUnit(nationMe, SEA, UnitType.SUBMARINE);
		assertEquals(Constants.MIN_ATTACK, AttackValueHelper.getInitialAttackValue(AttackType.COUNTER_ATTACK, att, def));
	}
	
	@Test
	public void testSubVsAirCounterValues() {
		Unit att = unitDaoService.buildUnit(nationMe, SEA, UnitType.SUBMARINE);
		Unit def = unitDaoService.buildUnit(nationMe, SEA, UnitType.FIGHTER);
		assertEquals(Constants.MIN_ATTACK, AttackValueHelper.getInitialAttackValue(AttackType.INITIAL_ATTACK, att, def));
	}
	
	@Test
	public void testNBVsAirCounterValues() {
		Unit att = unitDaoService.buildUnit(nationMe, SEA, UnitType.NAVAL_BOMBER);
		Unit def = unitDaoService.buildUnit(nationMe, SEA, UnitType.CARGO_PLANE);
		assertEquals(Constants.MIN_ATTACK, AttackValueHelper.getInitialAttackValue(AttackType.INITIAL_ATTACK, att, def));
	}
	
}
