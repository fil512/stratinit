package com.kenstevens.stratinit.util;

import com.kenstevens.stratinit.model.*;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.SectorType;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UnitHelperTest {

    private Unit inf;
    private Unit fighter;
    private Unit transport;
    private Unit dest;
    private Unit carrier;
    private Unit heli;
    private WorldSector landSector;
    private WorldSector citySector;
    private WorldSector waterSector;
    private Nation nation;
    private final SectorCoords coords = new SectorCoords(0, 0);

    @BeforeEach
    public void init() {
        Game game = new Game();
        nation = new Nation(game, new Player());
        inf = new Unit(nation, UnitType.INFANTRY, coords);
        fighter = new Unit(nation, UnitType.FIGHTER, coords);
        transport = new Unit(nation, UnitType.TRANSPORT, coords);
        dest = new Unit(nation, UnitType.DESTROYER, coords);
        carrier = new Unit(nation, UnitType.CARRIER, coords);
        heli = new Unit(nation, UnitType.HELICOPTER, coords);
        landSector = new WorldSector(game, coords, SectorType.LAND, 1);
		waterSector = new WorldSector(game, coords, SectorType.WATER, 1);
		citySector = new WorldSector(game, coords, SectorType.PLAYER_CITY, 1);
	}


	@Test
	public void canAttack() {
		assertFalse(UnitHelper.canAttack(inf, AttackType.INTERCEPTION));
		assertFalse(UnitHelper.canAttack(inf, AttackType.INTERDICTION));
		assertTrue(UnitHelper.canAttack(inf, AttackType.INITIAL_ATTACK));
		assertTrue(UnitHelper.canAttack(fighter, AttackType.INTERCEPTION));
		assertFalse(UnitHelper.canAttack(fighter, AttackType.INTERDICTION));
		assertTrue(UnitHelper.canAttack(fighter, AttackType.INITIAL_ATTACK));
		assertFalse(UnitHelper.canAttack(transport, AttackType.INTERCEPTION));
		assertFalse(UnitHelper.canAttack(transport, AttackType.INTERDICTION));
		assertFalse(UnitHelper.canAttack(transport, AttackType.INITIAL_ATTACK));
		assertFalse(UnitHelper.canAttack(dest, AttackType.INTERCEPTION));
		assertTrue(UnitHelper.canAttack(dest, AttackType.INTERDICTION));
		assertTrue(UnitHelper.canAttack(dest, AttackType.INITIAL_ATTACK));
	}

	@Test
	public void infCanCarry() {
		assertFalse(UnitHelper.canCarry(inf, inf, landSector));
	}

	@Test
	public void carrierCanCarry() {
		assertTrue(UnitHelper.canCarry(carrier, inf, waterSector));
		assertTrue(UnitHelper.canCarry(carrier, heli, waterSector));
		assertTrue(UnitHelper.canCarry(carrier, fighter, waterSector));
		assertFalse(UnitHelper.canCarry(carrier, transport, waterSector));
		assertFalse(UnitHelper.canCarry(carrier, dest, waterSector));
	}

	@Test
	public void transportCanCarry() {
		assertTrue(UnitHelper.canCarry(transport, inf, waterSector));
		assertFalse(UnitHelper.canCarry(transport, heli, waterSector));
		assertFalse(UnitHelper.canCarry(transport, fighter, waterSector));
		assertFalse(UnitHelper.canCarry(transport, transport, waterSector));
		assertFalse(UnitHelper.canCarry(transport, dest, waterSector));
	}

	@Test
	public void heliCanCarry() {
		assertTrue(UnitHelper.canCarry(heli, inf, waterSector));
		assertFalse(UnitHelper.canCarry(heli, heli, waterSector));
		assertFalse(UnitHelper.canCarry(heli, fighter, waterSector));
		assertFalse(UnitHelper.canCarry(heli, transport, waterSector));
		assertFalse(UnitHelper.canCarry(heli, dest, waterSector));
		Unit hurtInf = new Unit(nation, UnitType.INFANTRY, coords);
		hurtInf.setHp(1);
		assertTrue(UnitHelper.canCarry(heli, hurtInf, waterSector));
		assertFalse(UnitHelper.canCarry(heli, hurtInf, citySector));
		Unit immobInf = new Unit(nation, UnitType.INFANTRY, coords);
		immobInf.setMobility(0);
		assertTrue(UnitHelper.canCarry(heli, immobInf, waterSector));
		assertFalse(UnitHelper.canCarry(heli, immobInf, citySector));
	}
}
