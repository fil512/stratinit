package com.kenstevens.stratinit.server.rest.helper;

import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Player;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.NationDao;
import com.kenstevens.stratinit.dao.PlayerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorldManagerHelper {
    @Autowired
    PlayerDao playerDao;
    @Autowired
    GameDao gameDao;
    @Autowired
    private NationDao nationDao;

    public Nation createNation(int gameId) {
        Player player = createPlayer();
        Game game = gameDao.findGame(gameId);
        Nation nation = new Nation(game, player);
        nationDao.save(nation);
        return nation;
    }

    public Player createPlayer() {
        Player player = new Player("addMe");
        playerDao.save(player);
        return player;
    }

}
