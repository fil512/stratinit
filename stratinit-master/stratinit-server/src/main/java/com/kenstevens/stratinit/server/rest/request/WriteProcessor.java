package com.kenstevens.stratinit.server.rest.request;

import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.cache.GameCache;
import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.exception.CommandFailedException;
import com.kenstevens.stratinit.remote.exception.InsufficientCommandPointsException;
import com.kenstevens.stratinit.remote.exception.StratInitException;
import com.kenstevens.stratinit.server.service.GameService;
import com.kenstevens.stratinit.server.service.PlayerService;
import com.kenstevens.stratinit.server.rest.session.PlayerSession;
import com.kenstevens.stratinit.server.rest.session.PlayerSessionFactory;
import com.kenstevens.stratinit.server.rest.state.ServerStatus;
import com.kenstevens.stratinit.server.rest.svc.GameNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Function;

@Service
public class WriteProcessor {

    @Autowired
    private PlayerSessionFactory playerSessionFactory;
    @Autowired
    private ServerStatus serverStatus;
    @Autowired
    private DataCache dataCache;
    @Autowired
    private GameService gameService;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private GameNotificationService gameNotificationService;

    public record CommandResult<T>(Result<T> result, int commandCost) {}

    public <T> T process(Function<Nation, Result<T>> action, int commandCost) {
        checkServerRunning();
        PlayerSession session = playerSessionFactory.getPlayerSession();
        Nation nation = session.getNation();
        if (nation == null || session.getGame() == null) {
            throw new StratInitException("Game not set.");
        }
        Game game = session.getGame();
        GameCache gameCache = dataCache.getGameCache(game);
        synchronized (gameCache) {
            return executeWrite(nation, game, action, commandCost);
        }
    }

    public <T> T processForGame(int gameId, Function<PlayerSession, Result<T>> action) {
        checkServerRunning();
        PlayerSession session = playerSessionFactory.getPlayerSession();
        session.setGame(gameId);
        GameCache gameCache = dataCache.getGameCache(gameId);
        if (gameCache == null) {
            throw new StratInitException("Game not set.");
        }
        synchronized (gameCache) {
            Result<T> result = action.apply(session);
            return unwrapResult(result);
        }
    }

    public <T> T processDynamicCost(Function<Nation, CommandResult<T>> action, int preCheckCost) {
        checkServerRunning();
        PlayerSession session = playerSessionFactory.getPlayerSession();
        Nation nation = session.getNation();
        if (nation == null || session.getGame() == null) {
            throw new StratInitException("Game not set.");
        }
        Game game = session.getGame();
        GameCache gameCache = dataCache.getGameCache(game);
        synchronized (gameCache) {
            if (preCheckCost > 0) {
                checkCommandPoints(nation, preCheckCost);
            }
            setLastAction(nation);
            CommandResult<T> commandResult = action.apply(nation);
            Result<T> result = commandResult.result();
            if (result.isSuccess()) {
                nation.decreaseCommandPoints(commandResult.commandCost());
                gameNotificationService.notifyGameUpdate(game.getId(), nation.getNationId());
            }
            return unwrapResult(result);
        }
    }

    private <T> T executeWrite(Nation nation, Game game, Function<Nation, Result<T>> action, int commandCost) {
        if (commandCost > 0) {
            checkCommandPoints(nation, commandCost);
        }
        setLastAction(nation);
        Result<T> result = action.apply(nation);
        if (result.isSuccess() && commandCost > 0) {
            nation.decreaseCommandPoints(commandCost);
        }
        if (result.isSuccess()) {
            gameNotificationService.notifyGameUpdate(game.getId(), nation.getNationId());
        }
        return unwrapResult(result);
    }

    private <T> T unwrapResult(Result<T> result) {
        if (!result.isSuccess()) {
            throw new CommandFailedException(result.getMessages());
        }
        return result.getValue();
    }

    private void checkCommandPoints(Nation nation, int commandCost) {
        if (nation.getCommandPoints() <= 0) {
            throw new InsufficientCommandPointsException("You are out of command points.");
        }
        if (nation.getCommandPoints() < commandCost) {
            throw new InsufficientCommandPointsException(
                    "Insufficient command points.  Need " + commandCost + " have " + nation.getCommandPoints() + ".");
        }
    }

    private void setLastAction(Nation nation) {
        if (dataCache.getGameCache(nation.getGame()) == null) {
            return;
        }
        Date now = new Date();
        nation.setLastAction(now);
        gameService.merge(nation);
        playerService.setLastLogin(nation.getPlayer(), now);
    }

    private void checkServerRunning() {
        if (!serverStatus.isRunning()) {
            throw new StratInitException("The server is not running.  Server is " + serverStatus.getState());
        }
    }
}
