package com.kenstevens.stratinit;

import com.kenstevens.stratinit.client.event.EventScheduler;
import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.client.rest.RestClient;
import com.kenstevens.stratinit.client.rest.StratInitServerClient;
import com.kenstevens.stratinit.client.site.ActionQueue;
import com.kenstevens.stratinit.client.site.action.post.ActionFactory;
import com.kenstevens.stratinit.client.site.processor.UpdateProcessor;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.helper.GameHelper;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.world.WorldHelper;
import com.kenstevens.stratinit.world.WorldPrinter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class ClientServerTest extends ClientServerBase {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StratInitServerClient stratInitServerClient;
    @Autowired
    private RestClient restClient;
    @Autowired
    private Account account;

    private static boolean initialized;
    // FIXME move these into a static helper extension
    private static Game testGame;
    private static Integer testGameId;
    private static List<CityView> cities;
    private static List<UnitView> units;

    @Autowired
    private EventScheduler eventScheduler;
    @Autowired
    GameDao gameDao;
    @Autowired
    SectorDao sectorDao;
    @Autowired
    Data db;
    @Autowired
    UpdateProcessor updateProcessor;
    @Autowired
    ActionFactory actionFactory;
    @Autowired
    ActionQueue actionQueue;

    @BeforeEach
    public void login() throws IOException {
        if (!initialized) {
            actionQueue.start();
            setupGame();
            eventScheduler.updateGamesAndStartTimer();
            goodLogin();
            restClient.registerInterceptor(new LoggingInterceptor());
        }
        initialized = true;
    }

    public void setupGame() {
        testGame = GameHelper.newMappedGame(2);
        gameDao.save(testGame);
        testGameId = testGame.getId();
        World world = WorldHelper.newWorld(testGame);
        WorldPrinter worldPrinter = new WorldPrinter(world);
        worldPrinter.print();
        sectorDao.save(world);
    }

    @Test
    public void test01GoodLogin() throws IOException {
        Result<String> result = stratInitServerClient.getVersion();
        assertResult(result);
        String reply = result.getValue();
        assertEquals(Constants.SERVER_VERSION, reply);
    }

    @Test
    public void test02TwoRequests() throws IOException {
        Result<String> result = stratInitServerClient.getVersion();
        assertResult(result);
        result = stratInitServerClient.getVersion();
        assertResult(result);
        String reply = result.getValue();
        assertEquals(Constants.SERVER_VERSION, reply);
    }

    private void goodLogin() throws IOException {
        setPlayer("test1");
    }

    private void setPlayer(String username) {
        account.setUsername(username);
        account.setPassword("testy");
        restClient.setAccount();
    }

    @Test
    public void test03GetUnjoinedGames() {
        actionFactory.getUnjoinedGames();
        awaitResponses();
        assertEquals(2, db.getUnjoinedGameList().size());
    }

    private void awaitResponses() {
        await().until(() -> actionQueue.isEmpty());
    }

    @Test
    public void test04GetMyGames() throws IOException {
        actionFactory.getGames();
        awaitResponses();
        assertEquals(0, db.getGameList().size());
    }

    @Test
    public void test05JoinGame() {
        actionFactory.joinGame(testGameId, true);
        awaitResponses();
        assertEquals(1, db.getGameList().size());

        actionFactory.getUnjoinedGames();
        awaitResponses();
        assertEquals(2, db.getUnjoinedGameList().size());

        actionFactory.getGames();
        awaitResponses();
        assertEquals(1, db.getGameList().size());
        GameView gameView = db.getGameList().get(testGameId);
        assertEquals(GameHelper.gameName, gameView.getGamename());
        assertNotNull(gameView.getMapped());
    }

    @Test
    public void test06GetUpdate() {
        db.setSelectedGameId(testGameId);
        actionFactory.getVersion();
        actionFactory.setGame(testGameId, true);
        actionFactory.updateAll(true);
        awaitResponses();

        cities = db.getCityList().getCities();
        assertThat(cities, hasSize(2));
        units = db.getUnitList().getUnits();
        assertThat(units, hasSize(5));
    }

    // FIXME moar test

    protected void assertResult(Result<? extends Object> result) {
        assertTrue(result.isSuccess(), result.toString());
    }
}
