package com.kenstevens.stratinit.server.rest.helper;

import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.PlayerDao;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorldManagerHelper {
    @Autowired
    PlayerDao playerDao;
    @Autowired
    GameDao gameDao;

    public Nation createNation(int gameId) {
        Player player = createPlayer();
        Game game = gameDao.findGame(gameId);
        Nation nation = new Nation(game, player);
        gameDao.save(nation);
        return nation;
    }

    public Player createPlayer() {
        Player player = new Player("addMe");
        playerDao.save(player);
        return player;
    }

}