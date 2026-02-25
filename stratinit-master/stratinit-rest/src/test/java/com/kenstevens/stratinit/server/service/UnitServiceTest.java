package com.kenstevens.stratinit.server.service;

import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.model.UnitSeen;
import com.kenstevens.stratinit.client.model.WorldSector;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.server.rest.TwoPlayerBase;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UnitServiceTest extends TwoPlayerBase {
    private static final SectorCoords PORT = new SectorCoords(2, 2);
    private static final SectorCoords SEA = new SectorCoords(3, 2);
    private static final SectorCoords TOP = new SectorCoords(0, 0);
    private static final SectorCoords BOTTOM = new SectorCoords(0, 3);
    @Autowired
    protected SectorService sectorService;

    @Test
    public void disableUnitSeen() {
        unitService.buildUnit(nationMe, SEA,
                UnitType.DESTROYER);
        cityService.captureCity(nationThem, PORT);
        Unit inf = unitService.buildUnit(nationThem, PORT, UnitType.INFANTRY);
        UnitSeen unitSeen = unitDao.findUnitSeen(nationMe, inf);
        assertNotNull(unitSeen);
        unitService.disable(unitSeen.getUnitSeenPK());
    }

    @Test
    public void updateUnit() {
        Unit dest = unitService.buildUnit(nationMe, SEA,
                UnitType.DESTROYER);
        cityService.captureCity(nationMe, PORT);
        dest.setMobility(0);
        dest.setHp(1);
        unitService.updateUnit(dest, new Date());
        assertEquals(dest.getUnitBase().getMobility(), dest.getMobility());
        assertEquals(1 + dest.getUnitBase().getHp() * Constants.SUPPLY_HEAL_PERCENT / 100, dest.getHp());
    }

    @Test
    public void updateUnitTwiceFirstTimeMovesPartiallySecondTimeArrives() {
        Unit inf = unitService.buildUnit(nationMe, TOP,
                UnitType.INFANTRY);
        inf.setMobility(0);
        unitService.setUnitMove(inf, BOTTOM);
        // First tick: gains 2 mobility, moves as far as possible (2 hexes at cost 1 in supply)
        unitService.updateUnit(inf, new Date());
        int baseMob = inf.getUnitBase().getMobility();
        assertEquals(0, inf.getMobility(), "Should have used all mobility moving partway");
        assertTrue(inf.getCoords().y > TOP.y, "Unit should have moved south from " + TOP);
        assertNotNull(inf.getUnitMove(), "Move order should still be set for remaining distance");
        assertEquals(BOTTOM, inf.getUnitMove().getCoords());
        SectorCoords afterFirstTick = inf.getCoords();
        // Second tick: gains 2 more mobility, reaches destination
        unitService.updateUnit(inf, new Date());
        assertEquals(BOTTOM, inf.getCoords());
        int expectedMob = baseMob - BOTTOM.distanceTo(testWorld, afterFirstTick);
        assertEquals(expectedMob, inf.getMobility());
        assertNull(inf.getUnitMove());
    }

    @Test
    public void updateUnitWithMove() {
        Unit dest = unitService.buildUnit(nationMe, SEA,
                UnitType.DESTROYER);
        cityService.captureCity(nationMe, PORT);
        dest.setMobility(0);
        unitService.setUnitMove(dest, PORT);
        unitService.updateUnit(dest, new Date());
        assertEquals(dest.getUnitBase().getMobility() - 1, dest.getMobility());
        assertEquals(PORT, dest.getCoords());
    }

    @Test
    public void updateXportWithMove() {
        Unit xport = unitService.buildUnit(nationMe, SEA,
                UnitType.TRANSPORT);
        Unit inf = unitService.buildUnit(nationMe, SEA,
                UnitType.INFANTRY);
        cityService.captureCity(nationMe, PORT);
        xport.setMobility(0);
        unitService.setUnitMove(xport, PORT);
        unitService.updateUnit(xport, new Date());
        assertEquals(xport.getUnitBase().getMobility() - 1, xport.getMobility());
        assertEquals(PORT, xport.getCoords());
        assertEquals(PORT, inf.getCoords());
    }

    @Test
    public void getCargoPassengersTest() {
        UnitType holderType = UnitType.CARGO_PLANE;
        SectorCoords coords = SEA;
        List<Unit> units = new ArrayList<>();

        List<Unit> passengers = getPassengers(holderType, coords, units);
        assertAllButOnePassengers(holderType, units, passengers);
    }

    @Test
    public void getCargoPassengersInAirportTest() {
        UnitType holderType = UnitType.CARGO_PLANE;
        SectorCoords coords = PORT;
        cityService.captureCity(nationMe, PORT);
        setBuild(PORT, UnitType.FIGHTER);
        List<Unit> units = new ArrayList<>();

        List<Unit> passengers = getPassengers(holderType, coords, units);
        assertAllButOnePassengers(holderType, units, passengers);

    }


    private void assertAllButOnePassengers(UnitType holderType,
                                           List<Unit> units, List<Unit> passengers) {
        int capacity = getCapacity(holderType);
        for (int i = 0; i < capacity; ++i) {
            assertTrue(passengers.contains(units.get(i)));
        }
        assertFalse(passengers.contains(units.get(capacity)));

    }

    @Test
    public void getXportPassengersTest() {
        UnitType holderType = UnitType.TRANSPORT;
        SectorCoords coords = SEA;
        List<Unit> units = new ArrayList<>();

        List<Unit> passengers = getPassengers(holderType, coords, units);
        assertAllButOnePassengers(holderType, units, passengers);
    }

    @Test
    public void getXportPassengersInPortTest() {
        UnitType holderType = UnitType.TRANSPORT;
        SectorCoords coords = PORT;
        cityService.captureCity(nationMe, PORT);
        setBuild(PORT, UnitType.TRANSPORT);
        List<Unit> units = new ArrayList<>();

        List<Unit> passengers = getPassengers(holderType, coords, units);
        assertAllButOnePassengers(holderType, units, passengers);
    }

    private List<Unit> getPassengers(UnitType holderType,
                                     SectorCoords coords, List<Unit> units) {
        int capacity = getCapacity(holderType);
        Unit holder = unitService.buildUnit(nationThem, coords,
                holderType);
        for (int i = 0; i < capacity + 1; ++i) {
            Unit inf = unitService.buildUnit(nationThem, coords,
                    UnitType.INFANTRY);
            units.add(inf);
        }
        WorldView WORLD = sectorService.getAllWorldView(nationMe);
        WorldSector worldSector = WORLD.getWorldSector(coords);

        List<Unit> passengers = unitService.getPassengers(holder, worldSector);
        return passengers;
    }
}
