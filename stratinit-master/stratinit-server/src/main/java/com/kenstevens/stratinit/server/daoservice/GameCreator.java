package com.kenstevens.stratinit.server.daoservice;

import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.dao.GameDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameCreator {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private GameDao gameDao;

    public void createGameIfAllMapped() {
        List<Game> games = gameDao.getAllGames();
        for (Game game : games) {
            if (!game.isMapped()) {
                return;
            }
        }
        Game game = new Game();
        gameDao.save(game);
        logger.info("Created game " + game.getGamename());
    }

}
