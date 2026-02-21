package com.kenstevens.stratinit.svc;

import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.Player;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.MessageDaoService;
import com.kenstevens.stratinit.server.daoservice.PlayerDaoService;
import com.kenstevens.stratinit.server.rest.ServerManager;
import com.kenstevens.stratinit.server.rest.session.PlayerSessionFactory;
import com.kenstevens.stratinit.server.rest.state.ServerStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StratInitAdmin {
    @Autowired
    private ServerManager serverManager;
    @Autowired
    private ServerStatus serverStatus;
    @Autowired
    private GameDao gameDao;
    @Autowired
    private PlayerDaoService playerDaoService;
    @Autowired
    private MessageDaoService messageDaoService;
    @Autowired
    private PlayerSessionFactory playerSessionFactory;

    public synchronized Result<None> shutdown() {
        // TODO SEC block access to this method to admin only
        if (!serverStatus.isRunning()) {
            return new Result<None>("The server is not running.", false);
        }
        serverManager.shutdown();
        return new Result<None>("SERVER SHUTDOWN COMPLETE", true);
    }

    public Result<Integer> postAnnouncement(String subject, String body) {
        Player player = playerSessionFactory.getPlayerSession().getPlayer();
        if (!playerDaoService.isAdmin(player)) {
            return new Result<>("Only administrators may post bulletins.", false);
        }
        int count = 0;
        for (Game game : gameDao.getAllGames()) {
            messageDaoService.postBulletin(game, subject, body);
            ++count;
        }
        return new Result<>("Bulletin posted to " + count + " games.", true, Integer.valueOf(count));
    }

    // FIXME test that only admin can shut down
}
