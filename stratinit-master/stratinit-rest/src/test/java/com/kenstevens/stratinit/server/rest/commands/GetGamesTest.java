package com.kenstevens.stratinit.server.rest.commands;

import com.kenstevens.stratinit.BaseStratInitControllerTest;
import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.world.WorldHelper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class GetGamesTest extends BaseStratInitControllerTest {

    @Test
    public void validateWorld() {
        WorldHelper.validateWorld(dataCache.getSectors(testGameId));
    }

    @Test
    public void getBean() {
        assertNotNull(gameController);
    }

    @Test
    public void getUnjoinedGames() {
        Result<List<SIGame>> result = gameController.getUnjoinedGames();
        assertResult(result);
        List<SIGame> games = result.getValue();
        assertEquals(testGame.getId(), Integer.valueOf(testGameId));
        assertFalse(games.isEmpty());
        assertEquals(testGame.getId(), Integer.valueOf(games.get(games.size() - 2).id));
    }

    @Test
    public void findGame() {
        Game game = gameDao.findGame(testGameId);
        assertEquals(testGame, game);
    }

    @Test
    public void joinGameGetPlayerGames() {
        List<SIGame> games = gameController.getJoinedGames().getValue();
        assertTrue(games.isEmpty());
        Result<SINation> result = joinGamePlayerMe();
        assertTrue(result.isSuccess());
        assertEquals(nationMe.getPlayer().getUsername(), result.getValue().getName());
        games = gameController.getJoinedGames().getValue();
        assertEquals(1, games.size());
    }

    @Test
    public void getConfig() {
        Properties properties = gameController.getServerConfig().getValue();
        assertEquals(Constants.SERVER_VERSION, properties.get("SERVER_VERSION"));
    }

}
