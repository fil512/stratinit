package com.kenstevens.stratinit.server.rest.request.write;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.model.SectorSeen;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.remote.Result;
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
        Result<SIUpdate> result = buildCity(eng);
        assertEquals(cp, nationMe.getCommandPoints());
        assertFalseResult(result);
        assertTrue(result.toString().contains("mobility"));
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
        Result<SIUpdate> result = buildCity(tank);
        assertFalseResult(result);
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
        Result<SIUpdate> result = buildCity(eng);
        sectors = sectorDao.getSectorsSeen(nationMe);
        assertEquals(84, sectors.size());
        assertTrue(sectors.contains(near));
        assertEquals(cp - Constants.COMMAND_COST_BUILD_CITY, nationMe.getCommandPoints());
        assertResult(result);
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
        Result<SIUpdate> result = buildCity(eng);
        assertFalseResult(result);
        cities = cityDao.getCities(nationMe);
        assertEquals(2, cities.size());
    }
}
