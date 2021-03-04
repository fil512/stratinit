package com.kenstevens.stratinit.server.rest;

import com.kenstevens.stratinit.DaoConfig;
import com.kenstevens.stratinit.TestConfig;
import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.dao.*;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.event.EventScheduler;
import com.kenstevens.stratinit.model.*;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.type.SectorType;
import com.kenstevens.stratinit.type.UnitType;
import com.kenstevens.stratinit.util.ExpungeSvc;
import com.kenstevens.stratinit.util.GameScheduleHelper;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.dialect.Dialect;
import org.hibernate.internal.SessionImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class, classes = {DaoConfig.class, TestConfig.class})
public abstract class StratInitDaoBase {
	private static final long SCHEDULED_TO_STARTED_MILLIS = DateUtils.MILLIS_PER_DAY;
	final Logger logger = LoggerFactory.getLogger(getClass());
	private final AtomicInteger playerIndex = new AtomicInteger();

	@PersistenceContext
	private EntityManager entityManager;
	@Autowired
	protected GameDao gameDao;
	@Autowired
	protected RelationDao relationDao;
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
	@Autowired
	private ExpungeSvc expungeSvc;
	@Autowired
	private EventScheduler eventScheduler;

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

	protected String[] islands = {
			"000....111.....", // 0
			"000....111.....", // 1
			"000....111.....", // 2
			"000....111.....", // 3
			"000....111.....", // 4
			"000....111.....", // 5
			"000....111.....", // 6
			"000....111.....", // 7
			"000....111.....", // 8
			"000....111.....", // 9
			"000....111.....", // 10
			"000....111.....", // 11
			"000....121.....", // 12
			"000....111.....", // 13
			"000....121.....", // 14
	};

	@BeforeEach
	public void validateRunningUnderH2() {
		SessionImpl session = (SessionImpl) entityManager.getDelegate();
		Dialect dialect = session.getFactory().getJdbcServices().getDialect();
		assertTrue(dialect instanceof org.hibernate.dialect.H2Dialect, "RUNNING IN H2.  Actual dialect = " + dialect);
	}

	@BeforeEach
	public void init() {
		logger.info("--- @BeforeEach start ---");
		// Note this creates a new game for every test
		setupGame();
		eventScheduler.updateGamesAndStartTimer();
		logger.info("--- @BeforeEach end ---");
	}

	public void setupGame() {
		testGame = new Game(null, GAME_SIZE);
		testGame.setBlitz(true);
		GameScheduleHelper.setStartTimeBasedOnNow(testGame, SCHEDULED_TO_STARTED_MILLIS);
		testGame.setBlitz(false);
		setIslands(NUM_ISLANDS);
		testGame.setMapped();
		gameDao.save(testGame);
		testGameId = testGame.getId();
		World world = new World(testGame, true);
		populate(world, getTypes(), getIslands());
		sectorDao.save(world);
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
		playerDao.save(player);
		return player;
	}

	protected String[] getIslands() {
		return islands;
	}

	protected String[] getTypes() {
		return types;
	}

	@AfterEach
	public void removeGame() {
		logger.info("--- @AfterEach start ---");
		expungeSvc.expungeAll();
		dataCache.clear();
		logger.info("--- @AfterEach end ---");
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
        String name = PLAYER_NAME + playerIndex.incrementAndGet();
        Player player = new Player(name);
        logger.info("Creating player with name {}", name);
        player.setEmail("foo@foo.com");
        playerDao.save(player);
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
        assertTrue(result.isSuccess(), result.toString());
    }

    protected void assertFalseResult(Result<? extends Object> result) {
        assertFalse(result.isSuccess(), result.toString());
    }

    protected void assertFalseResult(String string,
                                     Result<? extends Object> result) {
        assertFalse(result.isSuccess(), string);
    }

    protected void assertTimeNear(long time1, long time2) {
        assertTrue(Math.abs(time2 - time1) < TIME_TOLERANCE,
                new Date(time1) + " is not within " + TIME_TOLERANCE
                        + " ms of " + new Date(time2) + ".  The difference is "
                        + (time2 - time1) + " ms.");
    }
}
