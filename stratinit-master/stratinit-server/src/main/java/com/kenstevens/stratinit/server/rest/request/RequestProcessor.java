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
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class RequestProcessor {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PlayerSessionFactory playerSessionFactory;
    @Autowired
    private ServerStatus serverStatus;
    @Autowired
    private SMTPService smtpService;

    public <T> Result<T> process(Function<Nation, T> action) {
        try {
            if (!serverStatus.isRunning()) {
                return new Result<>("The server is not running.  Server is " + serverStatus.getState(), false);
            }
            PlayerSession session = playerSessionFactory.getPlayerSession();
            Nation nation = session.getNation();
            if (nation == null || session.getGame() == null) {
                return new Result<>("Game not set.", false);
            }
            T value = action.apply(nation);
            Result<T> result = Result.make(value);
            result.setCommandPoints(nation.getCommandPoints());
            return result;
        } catch (Exception e) {
            return handleException(e);
        }
    }

    public <T> Result<T> processNoGame(Function<Player, T> action) {
        try {
            if (!serverStatus.isRunning()) {
                return new Result<>("The server is not running.  Server is " + serverStatus.getState(), false);
            }
            PlayerSession session = playerSessionFactory.getPlayerSession();
            Player player = session.getPlayer();
            T value = action.apply(player);
            return Result.make(value);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    public <T> Result<T> processWithGame(Function<Game, T> action) {
        try {
            if (!serverStatus.isRunning()) {
                return new Result<>("The server is not running.  Server is " + serverStatus.getState(), false);
            }
            PlayerSession session = playerSessionFactory.getPlayerSession();
            Nation nation = session.getNation();
            if (nation == null || session.getGame() == null) {
                return new Result<>("Game not set.", false);
            }
            T value = action.apply(session.getGame());
            Result<T> result = Result.make(value);
            result.setCommandPoints(nation.getCommandPoints());
            return result;
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private <T> Result<T> handleException(Exception e) {
        String message = e.getMessage();
        if (message == null) {
            message = e.getClass().getName();
        }
        logger.error(message, e);
        smtpService.sendException("Stratinit RequestProcessor Exception", StackTraceHelper.getStackTrace(e));
        return new Result<>(message, false);
    }
}
