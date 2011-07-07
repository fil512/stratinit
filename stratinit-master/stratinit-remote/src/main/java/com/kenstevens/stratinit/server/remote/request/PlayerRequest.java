package com.kenstevens.stratinit.server.remote.request;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;

import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.PlayerDao;
import com.kenstevens.stratinit.main.Spring;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.remote.mail.SMTPService;
import com.kenstevens.stratinit.server.remote.request.write.PostAnnouncementRequest;
import com.kenstevens.stratinit.server.remote.session.PlayerSession;
import com.kenstevens.stratinit.server.remote.session.StratInitSessionManager;
import com.kenstevens.stratinit.server.remote.session.ThreadLocalContext;
import com.kenstevens.stratinit.server.remote.state.ServerStatus;
import com.kenstevens.stratinit.util.StackTraceHelper;

public abstract class PlayerRequest<T> {
	private Logger logger = Logger.getLogger(getClass());



	@Autowired
	private ServerStatus serverStatus;
	@Autowired
	private SMTPService smtpService;
	@Autowired
	private Spring spring;

	private T resultValue;
	private boolean gameRequired = true;
	private PlayerSession playerSession;

	protected abstract T execute();

	public Result<T> process() {
		Result<T> retval;
		try {
			// logger.info("Processing request: "+this.getClass());
			if (!serverStatus.isRunning()) {
				return new Result<T>("The server is not running.", false);
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
		playerSession = spring.autowire(new PlayerSession());
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
