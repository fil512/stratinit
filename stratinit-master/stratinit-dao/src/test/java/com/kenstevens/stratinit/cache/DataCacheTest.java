package com.kenstevens.stratinit.cache;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.kenstevens.stratinit.StratInitTest;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.PlayerDao;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;

public class DataCacheTest extends StratInitTest {
	@Autowired
	GameDao gameDao;
	@Autowired
	PlayerDao playerDao;
	@Autowired
	DataCache dataCache;

	@Test
	public void getGameCachesNoReturnDisabledGames() {
		Game game1 = new Game();
		gameDao.persist(game1);
		Game game2 = new Game();
		gameDao.persist(game2);
		int origSize = dataCache.getGameCaches().size();
		game1.setEnabled(false);
		gameDao.merge(game1);
		assertEquals(1, origSize - dataCache.getGameCaches().size());
	}
	
	@Transactional
	@Test
	public void onlyOnePlayerEntity() {
		// create game 1 nation 1
		createNation1();

		clearPersistanceContext();

		// create game 2 nation 1
		
		createGame2();
		createNation1(testGame2);

		clearPersistanceContext();

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

	private void clearPersistanceContext() {
		entityManager.clear();
	}	
}
