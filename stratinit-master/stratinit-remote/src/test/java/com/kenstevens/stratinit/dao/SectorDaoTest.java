package com.kenstevens.stratinit.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.server.remote.TwoPlayerBase;
import com.kenstevens.stratinit.type.SectorCoords;

public class SectorDaoTest extends TwoPlayerBase {
	private static final SectorCoords I_SEE = new SectorCoords(1,3);

	@Test
	public void getSectorsWithin() {
		List<Sector>sectors = testWorld.getSectorsWithin(new SectorCoords(0,0), 2, true);
		assertEquals(25, sectors.size());
	}
	
	@Test
	public void countCities() {
		int cities = sectorDao.getNumberOfCities(nationMe);
		assertEquals(2, cities);
	}

	
	@Test
	public void testOtherNations() {
		Collection<Nation> nations = sectorDao.getOtherNationsThatSeeThisSector(nationMe, I_SEE);
		assertTrue(nations.isEmpty());
	}

	@Test
	public void testOtherNations2() {
		List<Nation> nations = Lists.newArrayList(sectorDao.getOtherNationsThatSeeThisSector(nationThem, I_SEE));
		assertEquals(1, nations.size());
		assertEquals(nationMe, nations.get(0));
	}
	

	@Test
	public void canEstablishCityByCity() {
		assertTrue(sectorDao.canEstablishCity(nationMe, testWorld.getSector(new SectorCoords(0,0))));
		assertTrue(sectorDao.canEstablishCity(nationMe, testWorld.getSector(new SectorCoords(1,1))));
		assertFalse(sectorDao.canEstablishCity(nationMe, testWorld.getSector(new SectorCoords(2,2))));
		assertTrue(sectorDao.canEstablishCity(nationMe, testWorld.getSector(new SectorCoords(1,2))));
		assertTrue(sectorDao.canEstablishCity(nationMe, testWorld.getSector(new SectorCoords(1,3))));
		assertFalse(sectorDao.canEstablishCity(nationMe, testWorld.getSector(new SectorCoords(1,4))));
		
		assertTrue(sectorDao.canEstablishCity(nationMe, testWorld.getSector(new SectorCoords(7,0))));
		assertTrue(sectorDao.canEstablishCity(nationMe, testWorld.getSector(new SectorCoords(7,1))));
		assertFalse(sectorDao.canEstablishCity(nationMe, testWorld.getSector(new SectorCoords(7,2))));
		assertTrue(sectorDao.canEstablishCity(nationMe, testWorld.getSector(new SectorCoords(8,2))));
		assertFalse(sectorDao.canEstablishCity(nationMe, testWorld.getSector(new SectorCoords(8,3))));
		assertFalse(sectorDao.canEstablishCity(nationMe, testWorld.getSector(new SectorCoords(8,4))));
	}

	@Test
	public void canEstablishCityByWater() {
		assertTrue(sectorDao.canEstablishCity(nationMe, testWorld.getSector(new SectorCoords(0,0))));
		assertTrue(sectorDao.canEstablishCity(nationMe, testWorld.getSector(new SectorCoords(1,0))));
		assertTrue(sectorDao.canEstablishCity(nationMe, testWorld.getSector(new SectorCoords(2,0))));
		assertTrue(sectorDao.canEstablishCity(nationMe, testWorld.getSector(new SectorCoords(3,0))));
		assertFalse(sectorDao.canEstablishCity(nationMe, testWorld.getSector(new SectorCoords(4,0))));
		assertFalse(sectorDao.canEstablishCity(nationMe, testWorld.getSector(new SectorCoords(5,0))));
		assertTrue(sectorDao.canEstablishCity(nationMe, testWorld.getSector(new SectorCoords(6,0))));
		assertTrue(sectorDao.canEstablishCity(nationMe, testWorld.getSector(new SectorCoords(7,0))));
		assertTrue(sectorDao.canEstablishCity(nationMe, testWorld.getSector(new SectorCoords(8,0))));
		assertTrue(sectorDao.canEstablishCity(nationMe, testWorld.getSector(new SectorCoords(9,0))));
		assertTrue(sectorDao.canEstablishCity(nationMe, testWorld.getSector(new SectorCoords(10,0))));
		assertFalse(sectorDao.canEstablishCity(nationMe, testWorld.getSector(new SectorCoords(11,0))));
		assertFalse(sectorDao.canEstablishCity(nationMe, testWorld.getSector(new SectorCoords(12,0))));
	}
}
