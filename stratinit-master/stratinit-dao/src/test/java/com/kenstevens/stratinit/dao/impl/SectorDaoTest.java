package com.kenstevens.stratinit.dao.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kenstevens.stratinit.StratInitTest;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.model.SectorSeen;
import com.kenstevens.stratinit.type.UnitType;

public class SectorDaoTest extends StratInitTest {
	@Autowired
	private SectorDao sectorDao;

	@Test
	public void testSectorPersistance() {
		createGame();
		Sector result = sectorDao.getWorld(testGame.getId()).getSector(testCoords);
		Assert.assertEquals(testGame.getId(), result.getGame().getId());
	}
	
	@Test
	public void testSectorSeenDoubleSaveIfNew() {
		createNation1();
		Sector sector = sectorDao.getWorld(testGame.getId()).getSector(testCoords);
		SectorSeen sectorSeen = new SectorSeen(testNation1, sector);
		assertNull(sectorDao.findSectorSeen(testNation1, sector));
		assertNull(entityManager.find(SectorSeen.class, sectorSeen.getSectorSeenPK()));
		sectorDao.saveIfNew(testNation1, sector);
		sectorDao.saveIfNew(testNation1, sector);
	}

	@Test
	public void testSectorSeenSaveIfNewThenPersist() {
		createNation1();
		Sector sector = sectorDao.getWorld(testGame.getId()).getSector(testCoords);
		SectorSeen sectorSeen = new SectorSeen(testNation1, sector);
		assertNull(sectorDao.findSectorSeen(testNation1, sector));
		assertNull(entityManager.find(SectorSeen.class, sectorSeen.getSectorSeenPK()));
		sectorDao.saveIfNew(testNation1, sector);
		sectorDao.persist(sectorSeen);
	}
	
	@Test
	public void testSectorSeenSaveIfNew() {
		createNation1();
		Sector sector = sectorDao.getWorld(testGame.getId()).getSector(testCoords);
		SectorSeen sectorSeen = new SectorSeen(testNation1, sector);
		assertNull(sectorDao.findSectorSeen(testNation1, sector));
		assertNull(entityManager.find(SectorSeen.class, sectorSeen.getSectorSeenPK()));
		sectorDao.saveIfNew(testNation1, sector);
		assertNotNull(sectorDao.findSectorSeen(testNation1, sector));
		assertNotNull(entityManager.find(SectorSeen.class, sectorSeen.getSectorSeenPK()));
	}

	@Test
	public void testSectorSeenDoublePersist() {
		createNation1();
		SectorSeen sectorSeen = new SectorSeen(testNation1, sectorDao.getWorld(testGame.getId()).getSector(testCoords));
		sectorDao.persist(sectorSeen);
		sectorDao.persist(sectorSeen);
	}

	@Test
	public void testCityPersistance() {
		try {
			createNation1();
		} catch (Exception e) {
			e.printStackTrace();
		}
		City city = new City(testSector, testNation1, UnitType.INFANTRY);
		sectorDao.persist(city);

		City result = sectorDao.getCity(testSector);
		Assert.assertEquals(city, result);
		Assert.assertEquals(city.getNation(), result.getNation());
	}
}
