package com.kenstevens.stratinit.server.rest.request;

import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Player;
import com.kenstevens.stratinit.client.util.StackTraceHelper;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.rest.mail.SMTPService;
import com.kenstevens.stratinit.server.rest.session.PlayerSession;
import com.kenstevens.stratinit.server.rest.session.PlayerSessionFactory;
import com.kenstevens.stratinit.server.rest.state.ServerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public abstract class PlayerRequest<T> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ServerStatus serverStatus;
    @Autowired
    private SMTPService smtpService;
    @Autowired
    private PlayerSessionFactory playerSessionFactory;

    private T resultValue;
    private boolean gameRequired = true;
    private PlayerSession playerSession;

    protected abstract T execute();

    public Result<T> process() {
        Result<T> retval;
        try {
            // logger.info("Processing request: "+this.getClass());
            if (!serverStatus.isRunning()) {
                return new Result<T>("The server is not running.  Server is " + serverStatus.getState(), false);
            }
            if (getGame() == null && gameRequired) {
                return new Result<T>("Game not set.", false);
            }
            resultValue = execute();
            retval = getResult();
            addCommandPoints(retval);
        } catch (Exception e) {

            String message = e.getMessage();
            if (message == null) {
                message = e.getClass().getName();
            }
            logger.error(message, e);
            Game game = getGame();
            String subject = "Stratinit PlayerRequest Exception "
                    + getNation().getName() + " " + (game == null ? "NO_GAME" : game.getId());
            smtpService.sendException(subject, StackTraceHelper.getStackTrace(e));

            retval = new Result<T>(message, false);
        }
        return retval;
    }

    protected Game getGame() {
        return playerSession.getGame();
    }

    private void addCommandPoints(Result<T> result) {
        if (getNation() != null) {
            result.setCommandPoints(getNation().getCommandPoints());
        }
    }

    // Required for game joining where no nation exists yet
    public Result<T> process(int gameId) {
        playerSession.setGame(gameId);
        return process();
    }

    public Result<T> processNoGame() {
        gameRequired = false;
        return process();
    }

    protected Result<T> getResult() {
        return Result.make(resultValue);
    }

    @SuppressWarnings("unused")
    @PostConstruct
    private void init() {
        playerSession = playerSessionFactory.getPlayerSession();
    }

    protected Player getPlayer() {
        return playerSession.getPlayer();
    }

    protected Nation getNation() {
        return playerSession.getNation();
    }

    public boolean isGameRequired() {
        return gameRequired;
    }

    protected void setContext(Player joiningPlayer) {
        playerSession.setContext(joiningPlayer);
    }
}
