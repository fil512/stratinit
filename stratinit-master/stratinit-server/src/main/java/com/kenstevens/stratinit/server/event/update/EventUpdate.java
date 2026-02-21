package com.kenstevens.stratinit.server.event.update;

import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.cache.GameCache;
import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.util.StackTraceHelper;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.server.rest.mail.SMTPService;
import com.kenstevens.stratinit.server.rest.state.ServerStatus;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class EventUpdate {
    @Autowired
    DataCache dataCache;
    @Autowired
    private GameDao gameDao;
    @Autowired
    private ServerStatus serverStatus;
    private Game game;
    @Autowired
    private SMTPService smtpService;

    public final void update(int gameId) {
        try {
            checkRunning();
            game = gameDao.findGame(gameId);
            GameCache gameCache = dataCache.getGameCache(game);
            synchronized (gameCache) {
                executeWrite();
            }
        } catch (RuntimeException e) {
            smtpService.sendException("Stratinit Update Exception " + gameId, StackTraceHelper.getStackTrace(e));
            throw e;
        }
    }

    protected Game getGame() {
        return game;
    }

    protected abstract void executeWrite();

    private void checkRunning() {
        if (!serverStatus.isRunning()) {
            throw new IllegalStateException(
                    "The server is not running.  It is "
                            + serverStatus.getState());
        }
    }
}
