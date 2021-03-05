package com.kenstevens.stratinit;

import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.client.util.ExpungeSvc;
import com.kenstevens.stratinit.client.util.GameScheduleHelper;
import com.kenstevens.stratinit.config.IServerConfig;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.PlayerDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.SectorType;
import com.kenstevens.stratinit.type.UnitType;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.sql.BatchUpdateException;
import java.sql.SQLException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DaoConfig.class, TestConfig.class})
public abstract class StratInitTest {
    final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String TEST_PLAYER1_USERNAME = "a";
    protected static Player testPlayer1;
    protected static Player testPlayer2;

    @Autowired
    protected GameDao gameDao;
    @Autowired
    protected UnitDao unitDao;
    @Autowired
    private PlayerDao playerDao;
    @Autowired
    private SectorDao sectorDao;
    @Autowired
    private ExpungeSvc expungeSvc;
    @Autowired
    private IServerConfig serverConfig;

    protected Game testGame;
    protected Game testGame2;
    protected Nation testNation1;
    protected Nation testNation2;
    protected Unit testUnit1;
    protected SectorCoords testCoords = new SectorCoords(0, 1);
    protected Sector testSector;
    protected Sector testSector2;


    @BeforeEach
    public void stratInit() {
        testPlayer1 = new Player(TEST_PLAYER1_USERNAME);
        testPlayer1.setEmail("foo@foo.com");
        playerDao.save(testPlayer1);
        testPlayer2 = new Player("b");
        testPlayer2.setEmail("foo@foo.com");
        playerDao.save(testPlayer2);
    }

    // TODO junit 5
    @AfterEach
    @Transactional
    public void clearDb() {
        expungeSvc.expungeAll();
    }

    protected void handleException(Exception e) throws Exception {
        int index = ExceptionUtils.indexOfThrowable(e, BatchUpdateException.class);
        if (index != -1) {
            BatchUpdateException bue = (BatchUpdateException) ExceptionUtils.getThrowables(e)[index];
            SQLException sqlException = bue.getNextException();
            if (sqlException != null) {
                logger.error("SQL STACK TRACE: " + sqlException.getMessage(), sqlException);
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
        GameScheduleHelper.setStartTimeBasedOnNow(testGame, serverConfig.getScheduledToStartedMillis());
        testGame.setBlitz(false);
        gameDao.save(testGame);
        testSector = new Sector(testGame, testCoords, SectorType.LAND);
        World world = new World(testGame, true);
        world.setSector(testSector);
        sectorDao.save(world);
    }

    protected void createGame2() {
        testGame2 = new Game("test", 11);
        testGame2.setBlitz(true);
        GameScheduleHelper.setStartTimeBasedOnNow(testGame2, serverConfig.getScheduledToStartedMillis());
        testGame2.setBlitz(false);
        gameDao.save(testGame2);
        testSector2 = new Sector(testGame2, testCoords, SectorType.LAND);
        World world = new World(testGame2, true);
        world.setSector(testSector2);
        sectorDao.save(world);
    }

    protected void createNation1() {
        createGame();
        createNation1(testGame);
    }

    protected void createNation1(Game game) {
        Player player = playerDao.find(testPlayer1.getUsername());
        testNation1 = new Nation(game, player);
        testNation1.setStartCoords(new SectorCoords(0, 0));
        gameDao.save(testNation1);
    }

    protected void createNation2() {
        createNation1();
        testNation2 = new Nation(testGame, testPlayer2);
        testNation2.setStartCoords(new SectorCoords(4, 0));
        gameDao.save(testNation2);
    }

    protected void createUnit1() {
        createNation1();
        testUnit1 = new Unit(testNation1, UnitType.INFANTRY, testCoords);
        unitDao.save(testUnit1);
    }
}
