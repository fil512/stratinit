package com.kenstevens.stratinit.server.rest.request;

import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.cache.GameCache;
import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.util.StackTraceHelper;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.GameDaoService;
import com.kenstevens.stratinit.server.daoservice.PlayerDaoService;
import com.kenstevens.stratinit.server.rest.mail.SMTPService;
import com.kenstevens.stratinit.server.rest.session.PlayerSession;
import com.kenstevens.stratinit.server.rest.session.PlayerSessionFactory;
import com.kenstevens.stratinit.server.rest.state.ServerStatus;
import com.kenstevens.stratinit.server.rest.svc.GameNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Function;

@Service
public class WriteProcessor {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PlayerSessionFactory playerSessionFactory;
    @Autowired
    private ServerStatus serverStatus;
    @Autowired
    private SMTPService smtpService;
    @Autowired
    private DataCache dataCache;
    @Autowired
    private GameDaoService gameDaoService;
    @Autowired
    private PlayerDaoService playerDaoService;
    @Autowired
    private GameNotificationService gameNotificationService;

    public record CommandResult<T>(Result<T> result, int commandCost) {}

    public <T> Result<T> process(Function<Nation, Result<T>> action, int commandCost) {
        try {
            if (!serverStatus.isRunning()) {
                return new Result<>("The server is not running.  Server is " + serverStatus.getState(), false);
            }
            PlayerSession session = playerSessionFactory.getPlayerSession();
            Nation nation = session.getNation();
            if (nation == null || session.getGame() == null) {
                return new Result<>("Game not set.", false);
            }
            Game game = session.getGame();
            GameCache gameCache = dataCache.getGameCache(game);
            synchronized (gameCache) {
                return executeWrite(nation, game, action, commandCost);
            }
        } catch (Exception e) {
            return handleException(e);
        }
    }

    public <T> Result<T> processForGame(int gameId, Function<PlayerSession, Result<T>> action) {
        try {
            if (!serverStatus.isRunning()) {
                return new Result<>("The server is not running.  Server is " + serverStatus.getState(), false);
            }
            PlayerSession session = playerSessionFactory.getPlayerSession();
            session.setGame(gameId);
            GameCache gameCache = dataCache.getGameCache(gameId);
            if (gameCache == null) {
                return new Result<>("Game not set.", false);
            }
            synchronized (gameCache) {
                Result<T> result = action.apply(session);
                Nation nation = session.getNation();
                if (nation != null) {
                    result.setCommandPoints(nation.getCommandPoints());
                }
                return result;
            }
        } catch (Exception e) {
            return handleException(e);
        }
    }

    public <T> Result<T> processDynamicCost(Function<Nation, CommandResult<T>> action, int preCheckCost) {
        try {
            if (!serverStatus.isRunning()) {
                return new Result<>("The server is not running.  Server is " + serverStatus.getState(), false);
            }
            PlayerSession session = playerSessionFactory.getPlayerSession();
            Nation nation = session.getNation();
            if (nation == null || session.getGame() == null) {
                return new Result<>("Game not set.", false);
            }
            Game game = session.getGame();
            GameCache gameCache = dataCache.getGameCache(game);
            synchronized (gameCache) {
                if (preCheckCost > 0) {
                    Result<T> costCheck = checkCommandPoints(nation, preCheckCost);
                    if (costCheck != null) {
                        return costCheck;
                    }
                }
                setLastAction(nation);
                CommandResult<T> commandResult = action.apply(nation);
                Result<T> result = commandResult.result();
                if (result.isSuccess()) {
                    nation.decreaseCommandPoints(commandResult.commandCost());
                    gameNotificationService.notifyGameUpdate(game.getId(), nation.getNationId());
                }
                result.setCommandPoints(nation.getCommandPoints());
                return result;
            }
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private <T> Result<T> executeWrite(Nation nation, Game game, Function<Nation, Result<T>> action, int commandCost) {
        if (commandCost > 0) {
            Result<T> costCheck = checkCommandPoints(nation, commandCost);
            if (costCheck != null) {
                return costCheck;
            }
        }
        setLastAction(nation);
        Result<T> result = action.apply(nation);
        if (result.isSuccess() && commandCost > 0) {
            nation.decreaseCommandPoints(commandCost);
        }
        if (result.isSuccess()) {
            gameNotificationService.notifyGameUpdate(game.getId(), nation.getNationId());
        }
        result.setCommandPoints(nation.getCommandPoints());
        return result;
    }

    private <T> Result<T> checkCommandPoints(Nation nation, int commandCost) {
        if (nation.getCommandPoints() <= 0) {
            return new Result<>("You are out of command points.", false);
        }
        if (nation.getCommandPoints() < commandCost) {
            return new Result<>("Insufficient command points.  Need " + commandCost + " have " + nation.getCommandPoints() + ".", false);
        }
        return null;
    }

    private void setLastAction(Nation nation) {
        if (dataCache.getGameCache(nation.getGame()) == null) {
            return;
        }
        Date now = new Date();
        nation.setLastAction(now);
        gameDaoService.merge(nation);
        playerDaoService.setLastLogin(nation.getPlayer(), now);
    }

    private <T> Result<T> handleException(Exception e) {
        String message = e.getMessage();
        if (message == null) {
            message = e.getClass().getName();
        }
        logger.error(message, e);
        smtpService.sendException("Stratinit WriteProcessor Exception", StackTraceHelper.getStackTrace(e));
        return new Result<>(message, false);
    }
}
