package com.kenstevens.stratinit.server.rest.supply;

import com.kenstevens.stratinit.BaseStratInitControllerTest;
import com.kenstevens.stratinit.client.model.MoveCost;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.service.SectorService;
import com.kenstevens.stratinit.supply.Supply;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class SupplyTest extends BaseStratInitControllerTest {
    private static final SectorCoords IN_SUPPLY = new SectorCoords(0, 0);
    private static final SectorCoords OUT_SUPPLY = new SectorCoords(0, 12);
    private static final SectorCoords ENGINEER = new SectorCoords(0, 7);
    private static final SectorCoords SEA_IN_REACH = new SectorCoords(6, 0);
    private static final SectorCoords IN_TSUPPLY = new SectorCoords(9, 5);
    private static final SectorCoords OUT_TSUPPLY = new SectorCoords(9, 6);
    private static final SectorCoords SEA_OUT_OF_REACH = new SectorCoords(11, 0);
    private static final SectorCoords FAR_PORT = new SectorCoords(6, 2);
    private static final SectorCoords CLOSE_ENOUGH_TO_PORT = new SectorCoords(6, 3);
    private static final SectorCoords NEAR_PORT = new SectorCoords(6, 8);
    private static final SectorCoords PORT = new SectorCoords(7, 8);
    private static final SectorCoords FARXPORT = new SectorCoords(10, 13);
    private static final SectorCoords FARXPORT1 = new SectorCoords(11, 13);
    private static final SectorCoords FARLAND = new SectorCoords(9, 13);
    @Autowired
    protected SectorService sectorService;

    @BeforeEach
    public void doJoinGame() {
        joinGamePlayerMe();
    }

    @Test
    public void unitInSupply() {
        Unit inf = unitService.buildUnit(nationMe, IN_SUPPLY,
                UnitType.INFANTRY);
        WorldView worldView = sectorService.getSupplyWorldView(inf);
        Supply supply = new Supply(worldView);
        assertTrue(supply.inSupply(inf));
    }

    @Test
    public void unitOutSupply() {
        Unit inf = unitService.buildUnit(nationMe, OUT_SUPPLY,
                UnitType.INFANTRY);
        WorldView worldView = sectorService.getSupplyWorldView(inf);
        Supply supply = new Supply(worldView);
        assertFalse(supply.inSupply(inf));
    }

    @Test
    public void engineerSupplies() {
        Unit inf = unitService.buildUnit(nationMe, OUT_SUPPLY,
                UnitType.INFANTRY);
        unitService.buildUnit(nationMe, ENGINEER,
                UnitType.ENGINEER);
        WorldView worldView = sectorService.getSupplyWorldView(inf);
        Supply supply = new Supply(worldView);
        assertTrue(supply.inSupply(inf));
    }

    @Test
    public void unitInTSupply() {
        unitService.buildUnit(nationMe, SEA_IN_REACH, UnitType.TRANSPORT);

        Unit inf = unitService.buildUnit(nationMe, IN_TSUPPLY,
                UnitType.INFANTRY);
        WorldView worldView = sectorService.getSupplyWorldView(inf);
        Supply supply = new Supply(worldView);
        assertTrue(supply.inSupply(inf));
    }

    @Test
    public void unitOutTSupply() {
        unitService.buildUnit(nationMe, SEA_IN_REACH, UnitType.SUPPLY);

        Unit inf = unitService.buildUnit(nationMe, OUT_TSUPPLY,
                UnitType.INFANTRY);
        unitDao.save(inf);
        WorldView worldView = sectorService.getSupplyWorldView(inf);
        Supply supply = new Supply(worldView);
        assertFalse(supply.inSupply(inf));
    }

    @Test
    public void unitInSSupply() {
        unitService.buildUnit(nationMe, SEA_IN_REACH, UnitType.SUPPLY);
        Unit inf = unitService.buildUnit(nationMe, IN_TSUPPLY,
                UnitType.INFANTRY);
        WorldView worldView = sectorService.getSupplyWorldView(inf);
        Supply supply = new Supply(worldView);
        assertTrue(supply.inSupply(inf));

    }

    @Test
    public void unitOutSSupply() {
        unitService.buildUnit(nationMe, SEA_IN_REACH, UnitType.SUPPLY);
        Unit inf = unitService.buildUnit(nationMe, OUT_TSUPPLY,
                UnitType.INFANTRY);
        WorldView worldView = sectorService.getSupplyWorldView(inf);
        Supply supply = new Supply(worldView);
        assertFalse(supply.inSupply(inf));
    }

    @Test
    public void unitOnTransportInSupply() {
        Unit inf = unitService.buildUnit(nationMe, SEA_OUT_OF_REACH,
                UnitType.INFANTRY);
        unitService.buildUnit(nationMe, SEA_OUT_OF_REACH, UnitType.TRANSPORT);
        WorldView worldView = sectorService.getSupplyWorldView(inf);
        Supply supply = new Supply(worldView);
        assertTrue(supply.inSupply(inf));
    }

    @Test
    public void shipNearPortSupply() {
        Unit dest = unitService.buildUnit(nationMe, NEAR_PORT,
                UnitType.DESTROYER);
        WorldView worldView = sectorService.getSupplyWorldView(dest);
        Supply supply = new Supply(worldView);
        assertFalse(supply.inSupply(dest));
        cityService.captureCity(nationMe, PORT);
        setBuild(PORT, UnitType.TRANSPORT);
        worldView = sectorService.getSupplyWorldView(dest);
        supply = new Supply(worldView);
        assertTrue(supply.inSupply(dest));
    }

    @Test
    public void shipFarPortSupply() {
        Unit dest = unitService.buildUnit(nationMe, FAR_PORT,
                UnitType.DESTROYER);
        WorldView worldView = sectorService.getSupplyWorldView(dest);
        Supply supply = new Supply(worldView);
        assertFalse(supply.inSupply(dest));
        cityService.captureCity(nationMe, PORT);
        worldView = sectorService.getSupplyWorldView(dest);
        supply = new Supply(worldView);
        assertFalse(supply.inSupply(dest));
    }

    @Test
    public void destMoveIntoSupply() {
        Unit dest = unitService.buildUnit(nationMe, FAR_PORT,
                UnitType.DESTROYER);
        WorldView worldView = sectorService.getSupplyWorldView(dest);
        Supply supply = new Supply(worldView);
        assertFalse(supply.inSupply(dest));
        cityService.captureCity(nationMe, PORT);
        setBuild(PORT, UnitType.TRANSPORT);
        Result<MoveCost> result = moveUnits(
                makeUnitList(dest), CLOSE_ENOUGH_TO_PORT);
        assertResult(result);
        worldView = sectorService.getSupplyWorldView(dest);
        supply = new Supply(worldView);
        assertTrue(supply.inSupply(dest));
    }

    @Test
    public void supplyMoveIntoSupply() {
        Unit supply = unitService.buildUnit(nationMe, FAR_PORT,
                UnitType.SUPPLY);
        supply.decreaseAmmo();
        cityService.captureCity(nationMe, PORT);
        setBuild(PORT, UnitType.TRANSPORT);
        Result<MoveCost> result = moveUnits(
                makeUnitList(supply), CLOSE_ENOUGH_TO_PORT);
        assertResult(result);
        assertNotFired(result, supply);
    }

    @Test
    public void destMoveIntoSupplyRefillsAmmo() {
        Unit dest = unitService.buildUnit(nationMe, FAR_PORT,
                UnitType.DESTROYER);
        dest.decreaseAmmo();
        cityService.captureCity(nationMe, PORT);
        setBuild(PORT, UnitType.TRANSPORT);
        Result<MoveCost> result = moveUnits(
                makeUnitList(dest), CLOSE_ENOUGH_TO_PORT);
        assertResult(result);
        assertNotFired(result, dest);
    }

    @Test
    public void supplyMoveIntoSupplyRefillsAmmo() {
        Unit supply = unitService.buildUnit(nationMe, FAR_PORT,
                UnitType.SUPPLY);
        supply.decreaseAmmo();
        cityService.captureCity(nationMe, PORT);
        setBuild(PORT, UnitType.TRANSPORT);
        Result<MoveCost> result = moveUnits(
                makeUnitList(supply), CLOSE_ENOUGH_TO_PORT);
        assertResult(result);
        assertNotFired(result, supply);
    }

    @Test
    public void supplyFarInfMove() {
        Unit inf = unitService.buildUnit(nationMe, FARXPORT,
                UnitType.INFANTRY);
        Unit inf2 = unitService.buildUnit(nationMe, FARXPORT,
                UnitType.INFANTRY);
        Unit xport = unitService.buildUnit(nationMe, FARXPORT,
                UnitType.TRANSPORT);
        Supply supply = new Supply(sectorService.getSupplyWorldView(xport));
        assertFalse(supply.inSupply(xport));
        assertTrue(supply.inSupply(inf));
        assertTrue(supply.inSupply(inf2));
        Result<MoveCost> result = moveUnits(
                makeUnitList(inf), FARLAND);
        assertResult(result);
        assertEquals(inf.getUnitBase().getMobility() - 1, inf.getMobility());
    }

    @Test
    public void supplyFarSupMove() {
        Unit sup = unitService.buildUnit(nationMe, FARXPORT,
                UnitType.SUPPLY);
        Supply supply = new Supply(sectorService.getSupplyWorldView(sup));
        assertTrue(supply.inSupply(sup));
        Result<MoveCost> result = moveUnits(
                makeUnitList(sup), FARXPORT1);
        assertResult(result);
        assertEquals(sup.getUnitBase().getMobility() - 1, sup.getMobility());
    }

    @Test
    public void supplyFarEmptySupMove() {
        Unit sup = unitService.buildUnit(nationMe, FARXPORT,
                UnitType.SUPPLY);
        sup.setAmmo(0);
        Supply supply = new Supply(sectorService.getSupplyWorldView(sup));
        assertTrue(supply.inSupply(sup));
        Result<MoveCost> result = moveUnits(
                makeUnitList(sup), FARXPORT1);
        assertResult(result);
        assertEquals(sup.getUnitBase().getMobility() - 1, sup.getMobility());
    }

    @Test
    public void zepMoveIntoMyCityRefillsAmmo() {
        Unit zep = unitService.buildUnit(nationMe, NEAR_PORT,
                UnitType.ZEPPELIN);
        zep.decreaseAmmo();
        cityService.captureCity(nationMe, PORT);
        setBuild(PORT, UnitType.TRANSPORT);
        Result<MoveCost> result = moveUnits(
                makeUnitList(zep), PORT);
        assertResult(result);
        assertNotFired(result, zep);
        assertFullFuel(result, zep);
    }

    @Test
    public void zepMoveNoRefillsAmmo() {
        Unit zep = unitService.buildUnit(nationMe, NEAR_PORT,
                UnitType.ZEPPELIN);
        zep.decreaseAmmo();
        cityService.captureCity(nationMe, PORT);
        setBuild(PORT, UnitType.TRANSPORT);
        Result<MoveCost> result = moveUnits(
                makeUnitList(zep), CLOSE_ENOUGH_TO_PORT);
        assertResult(result);
        assertFired(result, zep);
        assertShortFuel(result, zep);
    }

}
