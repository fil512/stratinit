package com.kenstevens.stratinit.dao;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Sector;
import com.kenstevens.stratinit.server.rest.TwoPlayerBase;
import com.kenstevens.stratinit.type.SectorCoords;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SectorDaoTest extends TwoPlayerBase {
    private static final SectorCoords I_SEE = new SectorCoords(1, 3);

    @Test
    public void getSectorsWithin() {
        List<Sector> sectors = testWorld.getSectorsWithin(new SectorCoords(0, 0), 2, true);
        assertEquals(25, sectors.size());
    }

    @Test
    public void countCities() {
        int cities = cityDao.getNumberOfCities(nationMe);
        assertEquals(2, cities);
    }


    @Test
    public void testOtherNations() {
        Collection<Nation> nations = sectorDao.getOtherNationsThatSeeThisSector(nationMe, I_SEE);
        assertTrue(nations.isEmpty());
    }

    @Test
    public void testOtherNations2() {
        List<Nation> nations = new ArrayList<>(sectorDao.getOtherNationsThatSeeThisSector(nationThem, I_SEE));
        assertEquals(1, nations.size());
        assertEquals(nationMe, nations.get(0));
    }


    @Test
    public void canEstablishCityByCity() {
        assertTrue(cityDaoService.canEstablishCity(nationMe, testWorld.getSectorOrNull(new SectorCoords(0, 0))));
        assertTrue(cityDaoService.canEstablishCity(nationMe, testWorld.getSectorOrNull(new SectorCoords(1, 1))));
        assertFalse(cityDaoService.canEstablishCity(nationMe, testWorld.getSectorOrNull(new SectorCoords(2, 2))));
        assertTrue(cityDaoService.canEstablishCity(nationMe, testWorld.getSectorOrNull(new SectorCoords(1, 2))));
        assertTrue(cityDaoService.canEstablishCity(nationMe, testWorld.getSectorOrNull(new SectorCoords(1, 3))));
        assertFalse(cityDaoService.canEstablishCity(nationMe, testWorld.getSectorOrNull(new SectorCoords(1, 4))));

        assertTrue(cityDaoService.canEstablishCity(nationMe, testWorld.getSectorOrNull(new SectorCoords(7, 0))));
        assertTrue(cityDaoService.canEstablishCity(nationMe, testWorld.getSectorOrNull(new SectorCoords(7, 1))));
        assertFalse(cityDaoService.canEstablishCity(nationMe, testWorld.getSectorOrNull(new SectorCoords(7, 2))));
        assertTrue(cityDaoService.canEstablishCity(nationMe, testWorld.getSectorOrNull(new SectorCoords(8, 2))));
        assertFalse(cityDaoService.canEstablishCity(nationMe, testWorld.getSectorOrNull(new SectorCoords(8, 3))));
        assertFalse(cityDaoService.canEstablishCity(nationMe, testWorld.getSectorOrNull(new SectorCoords(8, 4))));
    }

    @Test
    public void canEstablishCityByWater() {
        assertTrue(cityDaoService.canEstablishCity(nationMe, testWorld.getSectorOrNull(new SectorCoords(0, 0))));
        assertTrue(cityDaoService.canEstablishCity(nationMe, testWorld.getSectorOrNull(new SectorCoords(1, 0))));
        assertTrue(cityDaoService.canEstablishCity(nationMe, testWorld.getSectorOrNull(new SectorCoords(2, 0))));
        assertTrue(cityDaoService.canEstablishCity(nationMe, testWorld.getSectorOrNull(new SectorCoords(3, 0))));
        assertFalse(cityDaoService.canEstablishCity(nationMe, testWorld.getSectorOrNull(new SectorCoords(4, 0))));
        assertFalse(cityDaoService.canEstablishCity(nationMe, testWorld.getSectorOrNull(new SectorCoords(5, 0))));
        assertTrue(cityDaoService.canEstablishCity(nationMe, testWorld.getSectorOrNull(new SectorCoords(6, 0))));
        assertTrue(cityDaoService.canEstablishCity(nationMe, testWorld.getSectorOrNull(new SectorCoords(7, 0))));
        assertTrue(cityDaoService.canEstablishCity(nationMe, testWorld.getSectorOrNull(new SectorCoords(8, 0))));
        assertTrue(cityDaoService.canEstablishCity(nationMe, testWorld.getSectorOrNull(new SectorCoords(9, 0))));
        assertTrue(cityDaoService.canEstablishCity(nationMe, testWorld.getSectorOrNull(new SectorCoords(10, 0))));
        assertFalse(cityDaoService.canEstablishCity(nationMe, testWorld.getSectorOrNull(new SectorCoords(11, 0))));
        assertFalse(cityDaoService.canEstablishCity(nationMe, testWorld.getSectorOrNull(new SectorCoords(12, 0))));
    }
}
