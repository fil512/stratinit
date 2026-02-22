package com.kenstevens.stratinit.server.rest.request;

import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Player;
import com.kenstevens.stratinit.remote.exception.StratInitException;
import com.kenstevens.stratinit.server.rest.session.PlayerSession;
import com.kenstevens.stratinit.server.rest.session.PlayerSessionFactory;
import com.kenstevens.stratinit.server.rest.state.ServerStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class RequestProcessor {

    @Autowired
    private PlayerSessionFactory playerSessionFactory;
    @Autowired
    private ServerStatus serverStatus;

    public <T> T process(Function<Nation, T> action) {
        checkServerRunning();
        PlayerSession session = playerSessionFactory.getPlayerSession();
        Nation nation = session.getNation();
        if (nation == null || session.getGame() == null) {
            throw new StratInitException("Game not set.");
        }
        return action.apply(nation);
    }

    public <T> T processNoGame(Function<Player, T> action) {
        checkServerRunning();
        PlayerSession session = playerSessionFactory.getPlayerSession();
        Player player = session.getPlayer();
        return action.apply(player);
    }

    public <T> T processWithGame(Function<Game, T> action) {
        checkServerRunning();
        PlayerSession session = playerSessionFactory.getPlayerSession();
        Nation nation = session.getNation();
        if (nation == null || session.getGame() == null) {
            throw new StratInitException("Game not set.");
        }
        return action.apply(session.getGame());
    }

    private void checkServerRunning() {
        if (!serverStatus.isRunning()) {
            throw new StratInitException("The server is not running.  Server is " + serverStatus.getState());
        }
    }
}
