package com.kenstevens.stratinit.dao.impl;

import com.kenstevens.stratinit.StratInitTest;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Relation;
import com.kenstevens.stratinit.type.RelationType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

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
	
	@Test
	public void testAlliesMustBeMutual() {
		createNation2();
		Relation relation1 = new Relation(testNation1, testNation2);
		relation1.setType(RelationType.ALLIED);
		gameDao.save(relation1);
		Collection<Nation> allies = gameDao.getAllies(testNation2);
		assertEquals(0, allies.size());
	}

	@Test
	public void testAlliesMustBeMutualReverse() {
		createNation2();
		Relation relation1 = new Relation(testNation1, testNation2);
		relation1.setType(RelationType.ALLIED);
		gameDao.save(relation1);
		Collection<Nation> allies = gameDao.getAllies(testNation1);
		assertEquals(0, allies.size());
	}

	@Test
	public void testMutualAllies() {
		createNation2();
		Relation relation1 = new Relation(testNation1, testNation2);
		relation1.setType(RelationType.ALLIED);
		gameDao.save(relation1);
		Relation relation2 = new Relation(testNation2, testNation1);
		relation2.setType(RelationType.ALLIED);
		gameDao.save(relation2);
		Collection<Nation> allies = gameDao.getAllies(testNation1);
		assertEquals(1, allies.size());
	}
}
