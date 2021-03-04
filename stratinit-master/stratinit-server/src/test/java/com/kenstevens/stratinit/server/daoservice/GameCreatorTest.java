package com.kenstevens.stratinit.server.daoservice;

import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.server.rest.StratInitDaoBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameCreatorTest extends StratInitDaoBase {
    @Autowired
    private GameCreator gameCreator;
    @Autowired
    private GameDao gameDao;

    @Test
    public void createGame() {
        List<Game> games;
        games = gameDao.getAllGames();
        assertEquals(2, games.size(), "Actual games: " + games);
        assertTrue(games.get(0).isMapped());
        assertFalse(games.get(1).isMapped());

        // Map the last game
        games.get(1).setMapped();
        gameCreator.createGameIfAllMapped();

        // This should create a third unmapped game
        games = gameDao.getAllGames();
        assertEquals(3, games.size());
        assertTrue(games.get(0).isMapped());
        assertTrue(games.get(1).isMapped());
        assertFalse(games.get(2).isMapped());

        // Nothing should change if it's not mapped
        gameCreator.createGameIfAllMapped();
        games = gameDao.getAllGames();
        assertEquals(3, games.size());
        assertTrue(games.get(0).isMapped());
        assertTrue(games.get(1).isMapped());
        assertFalse(games.get(2).isMapped());
    }

}
