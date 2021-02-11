package com.kenstevens.stratinit;

import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.PlayerDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.model.*;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.SectorType;
import com.kenstevens.stratinit.type.UnitType;
import com.kenstevens.stratinit.util.GameScheduleHelper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.internal.SessionImpl;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.BatchUpdateException;
import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public abstract class StratInitTest {
	final Logger logger = LoggerFactory.getLogger(getClass());

	@PersistenceContext
	protected EntityManager entityManager;
	@Autowired
	protected GameDao gameDao;
	@Autowired
	private PlayerDao playerDao;
	@Autowired
	protected UnitDao unitDao;
	@Autowired
	private SectorDao sectorDao;

	private static boolean initialized = false;
	protected Game testGame;
	protected Game testGame2;
	protected static Player testPlayer1;
	protected static Player testPlayer2;
	protected Nation testNation1;
	protected Nation testNation2;
	protected Unit testUnit1;
	protected SectorCoords testCoords = new SectorCoords(0,1);
	protected Sector testSector;
	protected Sector testSector2;

	@Before
	public void stratInit() {
		if (!initialized) {
			SessionImpl session = (SessionImpl) entityManager.getDelegate();
			assertTrue("RUNNING IN H2", session.getFactory().getJdbcServices().getDialect() instanceof org.hibernate.dialect.H2Dialect);
			initialized = true;
			testPlayer1 = new Player("a");
			testPlayer1.setEmail("foo@foo.com");
			playerDao.save(testPlayer1);
			testPlayer2 = new Player("b");
			testPlayer2.setEmail("foo@foo.com");
			playerDao.save(testPlayer2);
		}
	}

	protected void handleException(Exception e) throws Exception {
		int index = ExceptionUtils.indexOfThrowable(e, BatchUpdateException.class);
		if (index != -1) {
			BatchUpdateException bue = (BatchUpdateException) ExceptionUtils.getThrowables(e)[index];
			SQLException sqlException = bue.getNextException();
			if (sqlException != null) {
				logger.error("SQL STACK TRACE: "+sqlException.getMessage(), sqlException);
			} else {
				logger.error("No SQLException found");
			}
		} else {
			logger.error("No SQLException found");
		}
		throw e;
	}

	protected void createGame() {
		testGame = new Game("test", 10);
		testGame.setBlitz(true);
		GameScheduleHelper.setStartTimeBasedOnNow(testGame);
		testGame.setBlitz(false);
		gameDao.persist(testGame);
		testSector = new Sector(testGame, testCoords, SectorType.LAND);
		World world = new World(testGame, true);
		world.setSector(testSector);
		sectorDao.persist(world);
	}

	protected void createGame2() {
		testGame2 = new Game("test", 11);
		testGame2.setBlitz(true);
		GameScheduleHelper.setStartTimeBasedOnNow(testGame2);
		testGame2.setBlitz(false);
		gameDao.persist(testGame2);
		testSector2 = new Sector(testGame2, testCoords, SectorType.LAND);
		World world = new World(testGame2, true);
		world.setSector(testSector2);
		sectorDao.persist(world);
	}

	protected void createNation1() {
		createGame();
		createNation1(testGame);
	}
	
	protected void createNation1(Game game) {
		Player player = playerDao.find(testPlayer1.getUsername());
		testNation1 = new Nation(game, player);
		testNation1.setStartCoords(new SectorCoords(0,0));
		gameDao.persist(testNation1);
	}

	protected void createNation2() {
		createNation1();
		testNation2 = new Nation(testGame, testPlayer2);
		testNation2.setStartCoords(new SectorCoords(4,0));
		gameDao.persist(testNation2);
	}

	protected void createUnit1() {
		createNation1();
		testUnit1 = new Unit(testNation1, UnitType.INFANTRY, testCoords);
		unitDao.persist(testUnit1);
	}
}
