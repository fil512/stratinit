package com.kenstevens.stratinit.dao.impl;

import com.kenstevens.stratinit.StratInitTest;
import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.model.Sector;
import com.kenstevens.stratinit.client.model.SectorSeen;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.repo.SectorSeenRepo;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class SectorDaoTest extends StratInitTest {
	@Autowired
	private SectorDao sectorDao;
	@Autowired
	private SectorSeenRepo sectorSeenRepo;

	@Test
	public void testSectorPersistance() {
		createGame();
		Sector result = sectorDao.getWorld(testGame).getSector(testCoords);
		assertEquals(testGame.getId(), result.getGame().getId());
	}

	@Test
	public void testSectorSeenDoubleSaveIfNew() {
		createNation1();
		Sector sector = sectorDao.getWorld(testGame).getSector(testCoords);
		SectorSeen sectorSeen = new SectorSeen(testNation1, sector);
		assertNull(sectorDao.findSectorSeen(testNation1, sector));
		assertTrue(sectorSeenRepo.findById(sectorSeen.getSectorSeenPK()).isEmpty());
		sectorDao.saveIfNew(testNation1, sector);
		sectorDao.saveIfNew(testNation1, sector);
	}

	@Test
	public void testSectorSeenSaveIfNewThenPersist() {
		createNation1();
		Sector sector = sectorDao.getWorld(testGame).getSector(testCoords);
		SectorSeen sectorSeen = new SectorSeen(testNation1, sector);
		assertNull(sectorDao.findSectorSeen(testNation1, sector));
		assertTrue(sectorSeenRepo.findById(sectorSeen.getSectorSeenPK()).isEmpty());
		sectorDao.saveIfNew(testNation1, sector);
		sectorDao.save(sectorSeen);
	}
	
	@Test
	public void testSectorSeenSaveIfNew() {
		createNation1();
		Sector sector = sectorDao.getWorld(testGame).getSector(testCoords);
		SectorSeen sectorSeen = new SectorSeen(testNation1, sector);
		assertNull(sectorDao.findSectorSeen(testNation1, sector));
		assertTrue(sectorSeenRepo.findById(sectorSeen.getSectorSeenPK()).isEmpty());
		sectorDao.saveIfNew(testNation1, sector);
		assertNotNull(sectorDao.findSectorSeen(testNation1, sector));
		assertTrue(sectorSeenRepo.findById(sectorSeen.getSectorSeenPK()).isPresent());
	}

	@Test
	public void testSectorSeenDoublePersist() {
		createNation1();
		SectorSeen sectorSeen = new SectorSeen(testNation1, sectorDao.getWorld(testGame).getSector(testCoords));
		sectorDao.save(sectorSeen);
		sectorDao.save(sectorSeen);
	}

	@Test
	public void testCityPersistance() {
		try {
			createNation1();
		} catch (Exception e) {
			e.printStackTrace();
		}
		City city = new City(testSector, testNation1, UnitType.INFANTRY);
		sectorDao.save(city);

		City result = sectorDao.getCity(testSector);
		assertEquals(city, result);
		assertEquals(city.getNation(), result.getNation());
	}
}
