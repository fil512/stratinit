package com.kenstevens.stratinit.server.rest.move;

import com.kenstevens.stratinit.BaseStratInitControllerTest;
import com.kenstevens.stratinit.model.MoveCost;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShipStackTest extends BaseStratInitControllerTest {
    private static final SectorCoords SHIP1 = new SectorCoords(3, 0);
    private static final SectorCoords BETWEEN = new SectorCoords(3, 1);
    private static final SectorCoords SHIP2 = new SectorCoords(3, 2);
    private static final SectorCoords PORT = new SectorCoords(2, 2);

    @Test
    public void oneShipPerSector() {
        joinGamePlayerMe();
        Unit xport1 = unitDaoService.buildUnit(nationMe, SHIP1, UnitType.TRANSPORT);
        unitDaoService.buildUnit(nationMe, SHIP2, UnitType.TRANSPORT);
        Result<MoveCost> result = moveUnits(makeUnitList(xport1), BETWEEN);
        assertResult(result);
        result = moveUnits(makeUnitList(xport1), SHIP2);
        assertFalseResult(result);
    }

    @Test
    public void twoShipsPerPort() {
        joinGamePlayerMe();
        Unit xport1 = unitDaoService.buildUnit(nationMe, SHIP1, UnitType.TRANSPORT);
        Unit xport2 = unitDaoService.buildUnit(nationMe, SHIP2, UnitType.TRANSPORT);
        sectorDaoService.captureCity(nationMe, PORT);
        setBuild(PORT, UnitType.TRANSPORT);
        Result<MoveCost> result = moveUnits(makeUnitList(xport1), PORT);
        assertResult(result);
        result = moveUnits(makeUnitList(xport2), PORT);
        assertResult(result);
        assertEquals(xport2.getCoords(), PORT);
    }

    @Test
    public void twoShipsCannotMoveAtOnce() {
        joinGamePlayerMe();
        Unit xport1 = unitDaoService.buildUnit(nationMe, PORT, UnitType.TRANSPORT);
        Unit xport2 = unitDaoService.buildUnit(nationMe, PORT, UnitType.TRANSPORT);
        sectorDaoService.captureCity(nationMe, PORT);
        Result<MoveCost> result = moveUnits(makeUnitList(xport1, xport2), BETWEEN);
        assertFalseResult(result);
        assertTrue(xport1.getCoords().equals(PORT), result.toString());
        assertTrue(xport2.getCoords().equals(PORT), result.toString());
    }
}
