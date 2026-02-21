package com.kenstevens.stratinit.server.rest.move;

import com.kenstevens.stratinit.client.model.MoveCost;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.model.UnitSeen;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.rest.TwoPlayerBase;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UnitSeenTest extends TwoPlayerBase {
    private static final SectorCoords SHIP1 = new SectorCoords(12, 0);
    private static final SectorCoords NEXT = new SectorCoords(12, 2);
    private static final SectorCoords SHIP2 = new SectorCoords(12, 3);
    private static final SectorCoords PLANE = new SectorCoords(2, 3);
    private static final SectorCoords CITY = new SectorCoords(1, 4);
    private static final SectorCoords AWAY = new SectorCoords(14, 14);
    private static final SectorCoords NEAR_THEM_CITY = new SectorCoords(8, 2);
    private static final SectorCoords THEM_CITY = new SectorCoords(8, 4);

    @Test
    public void doNothing() {

    }

    @Test
    public void moveCanSee() {
        Unit xport1 = unitService.buildUnit(nationMe, SHIP1, UnitType.TRANSPORT);
        Unit xport2 = unitService.buildUnit(nationThem, SHIP2, UnitType.TRANSPORT);
        UnitSeen unitSeen = unitDao.findUnitSeen(nationMe, xport2);
        assertNull(unitSeen);
        Result<MoveCost> result = moveUnits(makeUnitList(xport1), NEXT);
        assertResult(result);
        unitSeen = unitDao.findUnitSeen(nationMe, xport2);
        assertNotNull(unitSeen);
        assertTrue(unitSeen.getUnit() == xport2);
    }

    @Test
    public void moveIsSeen() {
        Unit xport1 = unitService.buildUnit(nationMe, SHIP1, UnitType.TRANSPORT);
        unitService.buildUnit(nationThem, SHIP2, UnitType.TRANSPORT);
        UnitSeen unitSeen = unitDao.findUnitSeen(nationThem, xport1);
        assertNull(unitSeen);
        Result<MoveCost> result = moveUnits(makeUnitList(xport1), NEXT);
        assertResult(result);
        unitSeen = unitDao.findUnitSeen(nationThem, xport1);
        assertNotNull(unitSeen);
    }

    @Test
    public void subMoveNoCanSee() {
        Unit xport1 = unitService.buildUnit(nationMe, SHIP1, UnitType.TRANSPORT);
        Unit sub = unitService.buildUnit(nationThem, SHIP2, UnitType.SUBMARINE);
        UnitSeen unitSeen = unitDao.findUnitSeen(nationMe, sub);
        assertNull(unitSeen);
        Result<MoveCost> result = moveUnits(makeUnitList(xport1), NEXT);
        assertResult(result);
        unitSeen = unitDao.findUnitSeen(nationMe, sub);
        assertNull(unitSeen);
    }

    @Test
    public void subMoveIsNotSeen() {
        Unit sub = unitService.buildUnit(nationMe, SHIP1, UnitType.SUBMARINE);
        unitService.buildUnit(nationThem, SHIP2, UnitType.TRANSPORT);
        UnitSeen unitSeen = unitDao.findUnitSeen(nationThem, sub);
        assertNull(unitSeen);
        Result<MoveCost> result = moveUnits(makeUnitList(sub), NEXT);
        assertResult(result);
        unitSeen = unitDao.findUnitSeen(nationThem, sub);
        assertNull(unitSeen);
    }

    @Test
    public void subAttackIsSeen() {
        declareWar();
        unitService.buildUnit(nationThem, SHIP2, UnitType.TRANSPORT);
        Unit sub = unitService.buildUnit(nationMe, SHIP1, UnitType.SUBMARINE);
        sub.addMobility();
        UnitSeen unitSeen = unitDao.findUnitSeen(nationThem, sub);
        assertNull(unitSeen);
        Result<MoveCost> result = moveUnits(makeUnitList(sub), SHIP2);
        assertResult(result);
        unitSeen = unitDao.findUnitSeen(nationThem, sub);
        assertNotNull(unitSeen, result.toString());
    }

    @Test
    public void subMoveCanSeeByDest() {
        Unit dest = unitService.buildUnit(nationMe, SHIP1, UnitType.DESTROYER);
        Unit sub = unitService.buildUnit(nationThem, SHIP2, UnitType.SUBMARINE);
        UnitSeen unitSeen = unitDao.findUnitSeen(nationMe, sub);
        assertNull(unitSeen);
        Result<MoveCost> result = moveUnits(makeUnitList(dest), NEXT);
        assertResult(result);
        unitSeen = unitDao.findUnitSeen(nationMe, sub);
        assertNotNull(unitSeen);
    }

    @Test
    public void subMoveIsSeenByDest() {
        Unit sub = unitService.buildUnit(nationMe, SHIP1, UnitType.SUBMARINE);
        unitService.buildUnit(nationThem, SHIP2, UnitType.DESTROYER);
        UnitSeen unitSeen = unitDao.findUnitSeen(nationThem, sub);
        assertNull(unitSeen);
        Result<MoveCost> result = moveUnits(makeUnitList(sub), NEXT);
        assertResult(result);
        unitSeen = unitDao.findUnitSeen(nationThem, sub);
        assertNotNull(unitSeen);
    }

    @Test
    public void planeIntoFOWDisappears() {
        Unit plane = unitService.buildUnit(nationThem, PLANE, UnitType.FIGHTER);
        UnitSeen unitSeen = unitDao.findUnitSeen(nationMe, plane);
        assertNotNull(unitSeen);
        Result<MoveCost> result = moveUnits(nationThem, makeUnitList(plane), AWAY);
        assertResult(result);
        unitSeen = unitDao.findUnitSeen(nationMe, plane);
        assertNull(unitSeen);
    }

    @Test
    public void buildCanBeSeen() {
        unitService.buildUnit(nationThem, PLANE, UnitType.FIGHTER);
        Unit inf = unitService.buildUnit(nationMe, CITY, UnitType.INFANTRY);
        UnitSeen unitSeen = unitDao.findUnitSeen(nationThem, inf);
        assertNotNull(unitSeen);
    }

    @Test
    public void buildCanSee() {
        Unit inf = unitService.buildUnit(nationMe, NEAR_THEM_CITY, UnitType.INFANTRY);
        UnitSeen unitSeen = unitDao.findUnitSeen(nationThem, inf);
        assertNull(unitSeen);
        unitService.buildUnit(nationThem, THEM_CITY, UnitType.INFANTRY);
        unitSeen = unitDao.findUnitSeen(nationThem, inf);
        assertNull(unitSeen);
        unitService.buildUnit(nationThem, THEM_CITY, UnitType.ZEPPELIN);
        unitSeen = unitDao.findUnitSeen(nationThem, inf);
        assertNotNull(unitSeen);
    }

}
