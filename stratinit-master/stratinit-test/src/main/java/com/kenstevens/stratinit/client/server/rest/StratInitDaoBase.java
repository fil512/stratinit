package com.kenstevens.stratinit.client.server.rest;

import com.kenstevens.stratinit.DaoConfig;
import com.kenstevens.stratinit.TestConfig;
import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.client.event.EventScheduler;
import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.client.util.ExpungeSvc;
import com.kenstevens.stratinit.dao.*;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.helper.GameHelper;
import com.kenstevens.stratinit.helper.PlayerHelper;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.type.UnitType;
import com.kenstevens.stratinit.world.WorldHelper;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class, classes = {DaoConfig.class, TestConfig.class})
public abstract class StratInitDaoBase {
	final Logger logger = LoggerFactory.getLogger(getClass());

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
	protected static final int CARRIER_CAPACITY = UnitBase.getUnitBase(UnitType.CARRIER).getCapacity();
	protected World testWorld;
	protected Player playerMe;
	protected Nation nationMe;
	protected int nationMeId;
	protected static final int TRANSPORT_CAPACITY = UnitBase.getUnitBase(UnitType.TRANSPORT).getCapacity();
	protected static final int HELICOPTER_CAPACITY = UnitBase.getUnitBase(UnitType.HELICOPTER).getCapacity();
	protected static final int ENGINEER_CAPACITY = UnitBase.getUnitBase(UnitType.ENGINEER).getCapacity();
	protected static final int SUB_CAPACITY = UnitBase.getUnitBase(UnitType.SUBMARINE).getCapacity();
	protected int testGameId;
	protected static final int CARGO_CAPACITY = UnitBase.getUnitBase(UnitType.CARGO_PLANE).getCapacity();
	private static final int TIME_TOLERANCE = 100;

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
		testGame = GameHelper.newMappedGame(getNumIslands());
		gameDao.save(testGame);
		testGameId = testGame.getId();
		World world = getWorld(testGame);
		sectorDao.save(world);
		playerMe = createPlayer(PlayerHelper.PLAYER_ME);
		testWorld = dataCache.getGameCache(testGameId).getWorld();
	}

	protected World getWorld(Game testGame) {
		return WorldHelper.newWorld(testGame);
	}

	protected int getNumIslands() {
		return 2;
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

	@AfterEach
	public void removeGame() {
		logger.info("--- @AfterEach start ---");
		expungeSvc.expungeAll();
		dataCache.clear();
		logger.info("--- @AfterEach end ---");
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
		Player player = PlayerHelper.newPlayer();
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
