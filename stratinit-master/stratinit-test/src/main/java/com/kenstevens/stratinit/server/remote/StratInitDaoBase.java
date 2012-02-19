package com.kenstevens.stratinit.server.remote;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.impl.SessionImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.LogDao;
import com.kenstevens.stratinit.dao.PlayerDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitBase;
import com.kenstevens.stratinit.model.World;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.RunMode;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.SectorType;
import com.kenstevens.stratinit.type.UnitType;
import com.kenstevens.stratinit.util.GameScheduleHelper;

// Move ddl generation up to web project so there is only one real
// persistence.xml
// move proper persistence.xml up here
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/WEB-INF/applicationContext.xml")
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = true)
@Transactional
public abstract class StratInitDaoBase {
	
	@Autowired
	protected GameDao gameDao;
	@Autowired
	protected SectorDao sectorDao;
	@Autowired
	protected UnitDao unitDao;
	@Autowired
	protected LogDao logDao;
	@Autowired
	protected PlayerDao playerDao;
	@Autowired
	protected DataCache dataCache;

	@PersistenceContext
	protected EntityManager entityManager;
	private static boolean initialized = false;
	protected Game testGame;
	protected World testWorld;
	protected static final String PLAYER_ME_NAME = "me";
	protected Player playerMe;
	protected static final String PLAYER_NAME = "player";
	protected int testGameId;
	protected Nation nationMe;
	protected int nationMeId;
	protected static final int NUM_ISLANDS = 2;
	protected static final int GAME_SIZE = 15;
	protected static final int CARRIER_CAPACITY = UnitBase.getUnitBase(
			UnitType.CARRIER).getCapacity();
	protected static final int TRANSPORT_CAPACITY = UnitBase.getUnitBase(
			UnitType.TRANSPORT).getCapacity();
	protected static final int HELICOPTER_CAPACITY = UnitBase.getUnitBase(
			UnitType.HELICOPTER).getCapacity();
	protected static final int ENGINEER_CAPACITY = UnitBase.getUnitBase(
			UnitType.ENGINEER).getCapacity();
	protected static final int SUB_CAPACITY = UnitBase.getUnitBase(
			UnitType.SUBMARINE).getCapacity();
	protected static final int CARGO_CAPACITY = UnitBase.getUnitBase(UnitType.CARGO_PLANE).getCapacity();
	protected static final SectorCoords TEST_COORDS = new SectorCoords(0, 0);
	private static final int TIME_TOLERANCE = 100;

	protected String[] types = {
		//             11111
	      // 012345678901234
			"###....###.....", // 0
			"###....###.....", // 1
			"##C....C##.....", // 2
			"###....###.....", // 3
			"#S#....#S#.....", // 4
			"###....###.....", // 5
			"#S#....#S#.....", // 6
			"###....###.....", // 7
			"##C....C##.....", // 8
			"###....###.....", // 9
			"###....###.....", // 10
			"###....###.....", // 11
			"###....#S#.....", // 12
			"###....###.....", // 13
			"###....#S#.....", // 14
	};

	protected String[] islands = { "000....111.....", "000....111.....",
			"000....111.....", "000....111.....", "000....111.....",
			"000....111.....", "000....111.....", "000....111.....",
			"000....111.....", "000....111.....", "000....111.....",
			"000....111.....", "000....121.....", "000....111.....",
			"000....121.....", };

	public void setupGame() {
		testGame = new Game(null, GAME_SIZE);
		testGame.setBlitz(true);
		GameScheduleHelper.setStartTimeBasedOnNow(testGame);
		testGame.setBlitz(false);
		setIslands(NUM_ISLANDS);
		testGame.setMapped();
		gameDao.persist(testGame);
		testGameId = testGame.getId();
		World world = new World(testGame, true);
		populate(world, getTypes(), getIslands());
		sectorDao.persist(world);
		playerMe = createPlayer(PLAYER_ME_NAME);
		testWorld = dataCache.getGameCache(testGameId).getWorld();
	}

	protected void setIslands(int numIslands) {
		testGame.setIslands(NUM_ISLANDS);
	}

	protected Player createPlayer(String username) {
		Player player = playerDao.find(username);
		if (player != null) {
			throw new IllegalStateException("Player state was not cleaned up.");
		}
		player = new Player(username);
		player.setEmail("foo@foo.com");
		playerDao.persist(player);
		return player;
	}
	
	protected String[] getIslands() {
		return islands;
	}

	protected String[] getTypes() {
		return types;
	}

	@Before
	public void stratInit() {
		Constants.setRunMode(RunMode.PRODUCTION);
		if (!initialized) {
			SessionImpl session = getSession();
			assertTrue("Running in HSQL", session.getFactory().getSettings()
					.getDialect() instanceof org.hibernate.dialect.HSQLDialect);
			initialized = true;
		}

		// Note this creates a new game for every test
		setupGame();
	}

	@After
	public void removeGame() {
		dataCache.remove(testGame);
		List<Player> players = dataCache.getAllPlayers();
		for (Player player : players) {
			dataCache.remove(player);
		}
	}

	private SessionImpl getSession() {
		return (SessionImpl) entityManager.getDelegate();
	}

	private void populate(World world, String[] types, String[] islands) {
		int y = 0;
		for (String typeRow : types) {
			int x = 0;
			for (char typeChar : typeRow.toCharArray()) {
				if (typeChar == '.') {
					++x;
					continue;
				}
				Sector sector = world.getSector(x, y);
				char islandChar = islands[y].charAt(x);
				if (islandChar == '0') {
					sector.setIsland(0);
				} else if (islandChar == '1') {
					sector.setIsland(1);
				} else if (islandChar == '2') {
					sector.setIsland(2);
				}
				if (typeChar == '#') {
					sector.setType(SectorType.LAND);
				} else if (typeChar == 'C') {
					sector.setType(SectorType.NEUTRAL_CITY);
				} else if (typeChar == 'S') {
					sector.setType(SectorType.START_CITY);
				}
				++x;
			}
			++y;
		}
	}

	protected List<SIUnit> makeUnitList(Nation nation, Unit[] units) {
		List<SIUnit> retval = new ArrayList<SIUnit>();
		for (Unit unit : units) {
			SIUnit siunit = new SIUnit(unit);
			siunit.addPrivateData(nation, unit);
			retval.add(siunit);
		}
		return retval;
	}

	protected Player createPlayer() {
		Player player = new Player(PLAYER_NAME);
		player.setEmail("foo@foo.com");
		playerDao.persist(player);
		return player;
	}

	protected List<SIUnit> makeUnitList(Unit... units) {
		Nation nation = units[0].getNation();
		return makeUnitList(nation, units);
	}

	protected List<SIUnit> makeUnitList(List<Unit> units) {
		Nation nation = units.get(0).getNation();
		return makeUnitList(nation, units.toArray(new Unit[0]));
	}

	protected void assertResult(Result<? extends Object> result) {
		assertTrue(result.toString(), result.isSuccess());
	}

	protected void assertFalseResult(Result<? extends Object> result) {
		assertFalse(result.toString(), result.isSuccess());
	}

	protected void assertFalseResult(String string,
			Result<? extends Object> result) {
		assertFalse(string, result.isSuccess());
	}
	protected void assertTimeNear(long time1, long time2) {
		assertTrue(new Date(time1) + " is not within " + TIME_TOLERANCE
				+ " ms of " + new Date(time2) + ".  The difference is "
				+ (time2 - time1) + " ms.",
				Math.abs(time2 - time1) < TIME_TOLERANCE);
	}
}
