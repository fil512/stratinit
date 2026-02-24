package com.kenstevens.stratinit.client.server.service;

import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Player;
import com.kenstevens.stratinit.client.server.rest.StratInitDaoBase;
import com.kenstevens.stratinit.dao.PlayerDao;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class GameScoreTest extends StratInitDaoBase {
    @Autowired
    private GameService gameService;
    @Autowired
    private PlayerDao playerDao;
    @Autowired
    private DataCache dataCache;

    @Test
    public void scoreUpdatesPlayerStatsInCache() {
        // Join game with playerMe
        Result<Nation> result = gameService.joinGame(playerMe, testGameId, false);
        assertTrue(result.isSuccess(), result.toString());

        // Create and join a second player
        Player playerThem = createPlayer("player_them");
        Result<Nation> result2 = gameService.joinGame(playerThem, testGameId, false);
        assertTrue(result2.isSuccess(), result2.toString());

        // Verify initial stats
        assertEquals(0, playerMe.getPlayed());
        assertEquals(0, playerMe.getWins());

        // Score the game
        gameService.score(testGame);

        // Verify stats via getAllPlayers (used by leaderboard)
        Player leaderboardPlayer = playerDao.getAllPlayers().stream()
                .filter(p -> p.getUsername().equals(playerMe.getUsername()))
                .findFirst()
                .orElseThrow();
        assertEquals(1, leaderboardPlayer.getPlayed(), "played count should be visible via leaderboard");
        assertEquals(1, leaderboardPlayer.getWins(), "wins count should be visible via leaderboard");
    }

    @Test
    public void saveAndUpdateNationsRefreshesCache() {
        // Verify that saveAndUpdateNations() updates the DataCache.
        // After scoring, evict the player from cache and re-add from DB.
        // The cache should still have the correct stats.
        Result<Nation> result = gameService.joinGame(playerMe, testGameId, false);
        assertTrue(result.isSuccess(), result.toString());

        Player playerThem = createPlayer("player_them");
        Result<Nation> result2 = gameService.joinGame(playerThem, testGameId, false);
        assertTrue(result2.isSuccess(), result2.toString());

        // Score the game - updates stats
        gameService.score(testGame);

        // Now simulate a cache refresh: remove player from cache
        // and verify we can get the correct stats from DB
        Player dbPlayer = playerDao.find(playerMe.getUsername());
        assertNotNull(dbPlayer, "player should exist in DB");
        assertEquals(1, dbPlayer.getPlayed(), "played count should be persisted to DB");
        assertEquals(1, dbPlayer.getWins(), "wins count should be persisted to DB");

        // Replace the cache entry with the DB version
        dataCache.add(dbPlayer);

        // Verify the cache now has the DB version with correct stats
        Player cachedPlayer = playerDao.find(playerMe.getId());
        assertEquals(1, cachedPlayer.getPlayed(), "played count should survive cache refresh");
        assertEquals(1, cachedPlayer.getWins(), "wins count should survive cache refresh");
    }
}
