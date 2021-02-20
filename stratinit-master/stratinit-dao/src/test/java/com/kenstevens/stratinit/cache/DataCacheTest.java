package com.kenstevens.stratinit.cache;

import com.kenstevens.stratinit.StratInitTest;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.PlayerDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.model.*;
import com.kenstevens.stratinit.repo.GameRepo;
import com.kenstevens.stratinit.repo.SectorRepo;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.SectorType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DataCacheTest extends StratInitTest {
	@Autowired
	GameDao gameDao;
	@Autowired
	GameRepo gameRepo;
	@Autowired
	PlayerDao playerDao;
	@Autowired
	DataCache dataCache;
	@Autowired
	SectorDao sectorDao;
	@Autowired
	SectorRepo sectorRepo;

	@Test
	public void getGameCachesNoReturnDisabledGames() {
		Game game1 = new Game();
		gameDao.save(game1);
		Game game2 = new Game();
		gameDao.save(game2);
		int origSize = dataCache.getGameCaches().size();
		game1.setEnabled(false);
		gameDao.merge(game1);
		assertEquals(1, origSize - dataCache.getGameCaches().size());
	}
	
	@Test
	public void onlyOnePlayerEntity() {
		// create game 1 nation 1
		createNation1();

		// create game 2 nation 1
		
		createGame2();
		createNation1(testGame2);

		// score game 1
		List<Nation> nations = gameDao.getNations(testGame);
		assertEquals(1, nations.size());
		Player player1 = nations.get(0).getPlayer();
		player1.setWins(5);
		playerDao.merge(player1);
		assertEquals(5, player1.getWins());
		
		// check game 1 nation 1
		
		List<Nation> nations2 = gameDao.getNations(testGame2);
		Player player2 = nations2.get(0).getPlayer();

		assertEquals(5, player2.getWins());
	}

	@Test
	public void sectorMerge() {
		createGame();
		Sector newSector = findByGameAndCoords(testGame, testSector.getCoords());
		newSector.setType(SectorType.PLAYER_CITY);
		sectorRepo.save(newSector);
		Sector newSector2 = findByGameAndCoords(testGame, testSector.getCoords());
		assertEquals(SectorType.PLAYER_CITY, newSector2.getType());
	}

	@Test
	public void sectorPersist() {
		createGame();

		SectorType oldType = testSector.getType();
		SectorType newType = SectorType.PLAYER_CITY;
		assertFalse(oldType == newType);

		SectorCoords coords = testSector.getCoords();

		World world = dataCache.getGameCache(testGame).getWorld();
		Sector newSector = world.getSector(coords);
		newSector.setType(newType);
		sectorDao.merge(newSector);

		Game dbGame = gameRepo.findById(testGame.getId()).get();
		Sector dbSector = findByGameAndCoords(dbGame, coords);
		assertEquals(oldType, dbSector.getType());
		assertFalse(dataCache.getGameCache(testGame).isModified());
		assertTrue(dataCache.getGameCache(testGame).isWorldModified());
		dataCache.flush();
		assertFalse(dataCache.getGameCache(testGame).isModified());
		assertFalse(dataCache.getGameCache(testGame).isWorldModified());

		dbSector = sectorDao.getWorld(testGame).getSector(coords);
		assertEquals(newType, dbSector.getType());
	}

	private Sector findByGameAndCoords(Game dbGame, SectorCoords coords) {
		return sectorRepo.findOne(QSector.sector.sectorPK.game.eq(dbGame).and(QSector.sector.sectorPK.coords.eq(coords))).get();
	}

	@Test
	public void dataCacheHasNextUpdate() {
		assertNotNull(dataCache.getLastUpdated());
	}
}
