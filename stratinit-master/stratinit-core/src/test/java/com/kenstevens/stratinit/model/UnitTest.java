package com.kenstevens.stratinit.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

public class UnitTest {
	private Player player = new Player();
	private Game game = new Game();
	private Nation nation = new Nation(game, player);
	private SectorCoords coords = new SectorCoords(0, 0);
	private Unit inf;
	private Unit tank;
	private Unit fighter;
	private Unit transport;
	private Unit dest;
	private Unit supply;

	@Before
	public void init() {
		Date aSecondAgo = new Date(new Date().getTime() - 1000);
		game.setStartTime(aSecondAgo);
		game.setMapped();
		inf = new Unit(nation, UnitType.INFANTRY, coords);
		tank = new Unit(nation, UnitType.TANK, coords);
		fighter = new Unit(nation, UnitType.FIGHTER, coords);
		transport = new Unit(nation, UnitType.TRANSPORT, coords);
		supply = new Unit(nation, UnitType.SUPPLY, coords);
		dest = new Unit(nation, UnitType.DESTROYER, coords);
	}

	@Test
	public void constructor() {
		assertUnitConstructor(inf, UnitType.INFANTRY);
		assertUnitConstructor(fighter, UnitType.FIGHTER);
	}



	@Test
	public void canSupply() {
		assertTrue(supply.supplies(inf));
		assertTrue(supply.supplies(dest));
		assertFalse(supply.supplies(fighter));
		assertTrue(transport.supplies(inf));
		assertFalse(transport.supplies(dest));
		assertFalse(transport.supplies(fighter));
	}

	@Test
	public void heal() {
		inf.setHp(1);
		inf.healPercent(Constants.CITY_HEAL_PERCENT);
		assertEquals(3, inf.getHp());
		tank.setHp(1);
		tank.healPercent(Constants.CITY_HEAL_PERCENT);
		assertEquals(4, tank.getHp());
	}
	
	@Test
	public void healFromFull() {
		inf.healPercent(Constants.CITY_HEAL_PERCENT);
		assertEquals(inf.getUnitBase().getHp(), inf.getHp());
	}
	
	private void assertUnitConstructor(Unit unit, UnitType type) {
		assertNotNull(unit.getLastUpdated());
		assertEquals(type, unit.getType());
		UnitBase unitBase = UnitBase.getUnitBase(type);
		assertEquals(unit.getUnitBase(), unitBase);
		assertEquals(unitBase.getSight(), unit.getSight());
		assertEquals(unitBase.getMobility(), unit.getMobility());
		assertEquals(unitBase.getHp(), unit.getHp());
		assertEquals(unitBase.getAmmo(), unit.getAmmo());
		if (unitBase.isRequiresFuel()) {
			assertEquals(unitBase.getMobility(), unit.getFuel());
		} else {
			assertEquals(0, unit.getFuel());
		}
		assertEquals(unitBase.isCanSeeSubs(), unit.isCanSeeSubs());
	}
}
