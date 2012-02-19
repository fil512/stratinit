package com.kenstevens.stratinit.server.daoservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.server.remote.StratInitDaoBase;

public class GameCreatorTest extends StratInitDaoBase {
	@Autowired
	private GameCreator gameCreator;
	@Autowired
	private GameDao gameDao;

	@Test
	public void createGame() {
		List<Game> games;
		games = gameDao.getAllGames();
		int size = games.size();
		assertTrue(size > 0);
		assertTrue(games.get(size - 1).isMapped());
	
		for (Game game : games) {
			game.setMapped();
		}
		
		gameCreator.createGameIfAllMapped();
		games = gameDao.getAllGames();
		assertEquals(size+1, games.size());
		assertFalse(games.get(size).isMapped());
		
		gameCreator.createGameIfAllMapped();
		games = gameDao.getAllGames();
		assertEquals(size+1, games.size());
	}

}
