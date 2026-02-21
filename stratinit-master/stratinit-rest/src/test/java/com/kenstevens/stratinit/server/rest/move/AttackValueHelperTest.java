package com.kenstevens.stratinit.server.rest.move;

import com.kenstevens.stratinit.BaseStratInitControllerTest;
import com.kenstevens.stratinit.client.model.AttackType;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.model.UnitBase;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AttackValueHelperTest extends BaseStratInitControllerTest {
    private static final SectorCoords SEA = new SectorCoords(4, 0);

    @BeforeEach
    public void join() {
        joinGamePlayerMe();
    }

    @Test
    public void testBBDestValues() {
        Unit att = unitService.buildUnit(nationMe, SEA, UnitType.BATTLESHIP);
        Unit def = unitService.buildUnit(nationMe, SEA, UnitType.DESTROYER);
        UnitBase attType = UnitBase.getUnitBase(UnitType.BATTLESHIP);
        Assertions.assertEquals(attType.getAttack(), AttackValueHelper.getInitialAttackValue(AttackType.INITIAL_ATTACK, att, def));
    }

    @Test
    public void testTankInfValues() {
        Unit att = unitService.buildUnit(nationMe, SEA, UnitType.TANK);
        Unit def = unitService.buildUnit(nationMe, SEA, UnitType.INFANTRY);
        UnitBase attType = UnitBase.getUnitBase(UnitType.TANK);
        assertEquals(attType.getAttack(), AttackValueHelper.getInitialAttackValue(AttackType.INITIAL_ATTACK, att, def));
    }

    @Test
    public void testBombCounterValues() {
        Unit att = unitService.buildUnit(nationMe, SEA, UnitType.NAVAL_BOMBER);
        Unit def = unitService.buildUnit(nationMe, SEA, UnitType.DESTROYER);
        assertEquals(Constants.MIN_ATTACK, AttackValueHelper.getInitialAttackValue(AttackType.COUNTER_ATTACK, att, def));
    }


    @Test
    public void testNavyVsAirCounterValues() {
        Unit att = unitService.buildUnit(nationMe, SEA, UnitType.DESTROYER);
        Unit def = unitService.buildUnit(nationMe, SEA, UnitType.FIGHTER);
        assertEquals(Constants.MIN_ATTACK, AttackValueHelper.getInitialAttackValue(AttackType.COUNTER_ATTACK, att, def));
    }

    @Test
    public void testCapVsSubCounterValues() {
        Unit att = unitService.buildUnit(nationMe, SEA, UnitType.CARRIER);
        Unit def = unitService.buildUnit(nationMe, SEA, UnitType.SUBMARINE);
        assertEquals(Constants.MIN_ATTACK, AttackValueHelper.getInitialAttackValue(AttackType.COUNTER_ATTACK, att, def));
    }

    @Test
    public void testSubVsAirCounterValues() {
        Unit att = unitService.buildUnit(nationMe, SEA, UnitType.SUBMARINE);
        Unit def = unitService.buildUnit(nationMe, SEA, UnitType.FIGHTER);
        assertEquals(Constants.MIN_ATTACK, AttackValueHelper.getInitialAttackValue(AttackType.INITIAL_ATTACK, att, def));
    }

    @Test
    public void testNBVsAirCounterValues() {
        Unit att = unitService.buildUnit(nationMe, SEA, UnitType.NAVAL_BOMBER);
        Unit def = unitService.buildUnit(nationMe, SEA, UnitType.CARGO_PLANE);
        assertEquals(Constants.MIN_ATTACK, AttackValueHelper.getInitialAttackValue(AttackType.INITIAL_ATTACK, att, def));
    }

}
