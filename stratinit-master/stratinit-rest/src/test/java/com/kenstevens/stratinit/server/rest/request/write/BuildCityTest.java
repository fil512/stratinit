package com.kenstevens.stratinit.server.rest.request.write;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.SectorSeen;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.remote.exception.CommandFailedException;
import com.kenstevens.stratinit.server.rest.TwoPlayerBase;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BuildCityTest extends TwoPlayerBase {
    private static final SectorCoords LAND = new SectorCoords(1, 11);
    private static final SectorCoords NEAR = new SectorCoords(1, 13);
    private static final SectorCoords PORT = new SectorCoords(2, 11);

    @Test
    public void buildCityNotEnoughMob() {
        Unit eng = unitService.buildUnit(nationMe, LAND,
                UnitType.ENGINEER);
        List<City> cities = cityDao.getCities(nationMe);
        assertEquals(2, cities.size());
        long cp = nationMe.getCommandPoints();
        assertThrows(CommandFailedException.class, () -> buildCity(eng));
        assertEquals(cp, nationMe.getCommandPoints());
        cities = cityDao.getCities(nationMe);
        assertEquals(2, cities.size());
    }

    @Test
    public void buildCityNotEng() {
        Unit tank = unitService.buildUnit(nationMe, LAND,
                UnitType.TANK);
        tank.setMobility(9);
        List<City> cities = cityDao.getCities(nationMe);
        assertEquals(2, cities.size());
        assertThrows(CommandFailedException.class, () -> buildCity(tank));
        cities = cityDao.getCities(nationMe);
        assertEquals(2, cities.size());
    }

    @Test
    public void buildCity() {
        Unit eng = unitService.buildUnit(nationMe, LAND,
                UnitType.ENGINEER);
        eng.setMobility(eng.getMaxMobility());
        List<City> cities = cityDao.getCities(nationMe);
        assertEquals(2, cities.size());
        long cp = nationMe.getCommandPoints();
        Collection<SectorSeen> sectors = sectorDao.getSectorsSeen(nationMe);
        assertEquals(58, sectors.size());
        SectorSeen near = new SectorSeen(nationMe, NEAR);
        assertFalse(sectors.contains(near));
        buildCity(eng);
        sectors = sectorDao.getSectorsSeen(nationMe);
        assertEquals(84, sectors.size());
        assertTrue(sectors.contains(near));
        assertEquals(cp - Constants.COMMAND_COST_BUILD_CITY, nationMe.getCommandPoints());
        assertEquals(0, eng.getMobility());
        assertTrue(eng.isAlive());
        cities = cityDao.getCities(nationMe);
        assertEquals(3, cities.size());
    }

    @Test
    public void buildCityBadLoc() {
        Unit eng = unitService.buildUnit(nationMe, LAND,
                UnitType.ENGINEER);
        cityService.captureCity(nationThem, PORT);
        eng.setMobility(eng.getMaxMobility());
        List<City> cities = cityDao.getCities(nationMe);
        assertEquals(2, cities.size());
        assertThrows(CommandFailedException.class, () -> buildCity(eng));
        cities = cityDao.getCities(nationMe);
        assertEquals(2, cities.size());
    }

    @Test
    public void buildCityNextToAlly() {
        // Ally captures a city next to our build location
        cityService.captureCity(nationThem, PORT);
        // Set mutual alliance
        declareAlliance();
        allianceDeclared();
        // Verify alliance is active
        Collection<Nation> allies = relationDao.getAllies(nationMe);
        assertTrue(allies.contains(nationThem), "nationThem should be an ally of nationMe");
        // Build engineer next to ally's city - should succeed
        Unit eng = unitService.buildUnit(nationMe, LAND, UnitType.ENGINEER);
        eng.setMobility(eng.getMaxMobility());
        List<City> cities = cityDao.getCities(nationMe);
        assertEquals(2, cities.size());
        buildCity(eng);
        cities = cityDao.getCities(nationMe);
        assertEquals(3, cities.size());
    }
}
