package com.kenstevens.stratinit.server.remote.commands;

import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.remote.StratInitWebBase;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.world.WorldHelper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class GetGamesTest extends StratInitWebBase {

    @Test
    public void validateWorld() {
        List<Sector> sectors = dataCache.getSectors(testGameId);
        new WorldHelper().validateWorld(sectors);
    }

    @Test
    public void getBean() {
        assertNotNull(stratInit);
    }

    @Test
    public void getUnjoinedGames() {
        Result<List<SIGame>> result = stratInit.getUnjoinedGames();
        assertResult(result);
        List<SIGame> games = result.getValue();
        assertEquals(testGame.getId(), Integer.valueOf(testGameId));
        assertFalse(games.isEmpty());
        assertEquals(testGame.getId(), Integer.valueOf(games.get(games.size() - 1).id));
    }

    @Test
    public void findGame() {
        Game game = gameDao.findGame(testGameId);
        assertEquals(testGame, game);
    }

    @Test
    public void joinGameGetPlayerGames() {
        List<SIGame> games = stratInit.getJoinedGames().getValue();
        assertTrue(games.isEmpty());
        Result<Nation> result = joinGamePlayerMe();
        assertTrue(result.isSuccess());
        assertEquals(nationMe, result.getValue());
        games = stratInit.getJoinedGames().getValue();
        assertEquals(1, games.size());
    }

    @Test
    public void getConfig() {
        Properties properties = stratInit.getServerConfig().getValue();
        assertEquals(Constants.SERVER_VERSION, properties.get("SERVER_VERSION"));
    }

}
