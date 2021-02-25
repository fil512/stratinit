package com.kenstevens.stratinit.server.daoservice;

import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.server.remote.StratInitDaoBase;
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
        int size = games.size();
        assertTrue(size > 0);
        assertTrue(games.get(size - 1).isMapped());

        for (Game game : games) {
            game.setMapped();
        }

        gameCreator.createGameIfAllMapped();
        games = gameDao.getAllGames();
        assertEquals(size + 1, games.size());
        assertFalse(games.get(size).isMapped());

        gameCreator.createGameIfAllMapped();
        games = gameDao.getAllGames();
        assertEquals(size + 1, games.size());
    }

}
