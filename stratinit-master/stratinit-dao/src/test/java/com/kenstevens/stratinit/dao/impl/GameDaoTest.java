package com.kenstevens.stratinit.dao.impl;

import com.kenstevens.stratinit.StratInitTest;
import com.kenstevens.stratinit.dao.GameDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class GameDaoTest extends StratInitTest {
	@Autowired
	private GameDao gameDao;

	@Test
	public void testGamePersistence() {
		createGame();
		assertNotNull(gameDao.findGame(testGame.getId()));
	}

	@Test
	public void testGameRemove() {
		createGame();
		gameDao.remove(testGame);
		assertNull(gameDao.findGame(testGame.getId()));
	}

	@Test
	public void testNationPersistence() {
		createNation1();
		assertNotNull(gameDao.findNation(testGame, testPlayer1));
	}

	@Test
	public void testNationRemove() {
		createNation1();
		gameDao.remove(testGame);
		assertNull(gameDao.findNation(testGame, testPlayer1));
	}
}
