package com.kenstevens.stratinit;

import com.kenstevens.stratinit.client.event.EventScheduler;
import com.kenstevens.stratinit.client.model.Account;
import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.World;
import com.kenstevens.stratinit.client.rest.RestClient;
import com.kenstevens.stratinit.client.rest.StratInitServerClient;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.helper.GameHelper;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.SetGameJson;
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
    private static Game testGame;
    @Autowired
    private EventScheduler eventScheduler;
    @Autowired
    GameDao gameDao;
    @Autowired
    SectorDao sectorDao;

    @BeforeEach
    public void login() throws IOException {
        if (!initialized) {
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
    public void test03GetMyGames() throws IOException {
        Result<List<SIGame>> result = stratInitServerClient.getJoinedGames();
        assertResult(result);
        List<SIGame> games = result.getValue();

        assertThat(games, hasSize(0));
        for (SIGame game : games) {
            logger.info("Game #" + game.id + ": " + game.name);
        }
    }

    @Test
    public void test04JoinGame() {
        {
            SetGameJson request = new SetGameJson(testGame.getId(), true);
            Result<SINation> result = stratInitServerClient.joinGame(request);
            assertResult(result);
        }

        {
            Result<List<SIGame>> result = stratInitServerClient.getJoinedGames();
            assertResult(result);
            List<SIGame> games = result.getValue();

            assertThat(games, hasSize(1));
            assertEquals(testGame.getId(), games.get(0).id);
            assertNotNull(testGame.getMapped());
        }
    }

    @Test
    public void test05GetUpdate() {
        {
            Result<SIUpdate> result = stratInitServerClient.getUpdate();
            assertFalseResult(result, "Game not set.");
        }

        {
            Result<None> result = stratInitServerClient.setGame(new SetGameJson(testGame.getId(), true));
            assertResult(result);
        }
        {
            Result<SIUpdate> result = stratInitServerClient.getUpdate();
            assertResult(result);
        }

    }

    private void assertFalseResult(Result<SIUpdate> result, String message) {
        assertFalse(result.isSuccess());
        assertEquals(message, result.toString());
    }

    protected void assertResult(Result<? extends Object> result) {
        assertTrue(result.isSuccess(), result.toString());
    }
}
