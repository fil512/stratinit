package com.kenstevens.stratinit.server.remote.request;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;

import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.PlayerDao;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.remote.mail.SMTPService;
import com.kenstevens.stratinit.server.remote.session.StratInitSessionManager;
import com.kenstevens.stratinit.server.remote.session.ThreadLocalContext;
import com.kenstevens.stratinit.server.remote.state.ServerStatus;

public abstract class PlayerRequest<T> {
	private Logger logger = Logger.getLogger(getClass());
	@Autowired
	private PlayerDao playerDao;
	@Autowired
	private GameDao gameDao;
	@Autowired
	private ThreadLocalContext context;
	@Autowired
	private StratInitSessionManager sessionManager;
	@Autowired
	private ServerStatus serverStatus;
	@Autowired
	private SMTPService smtpService;

	private Player player;
	private Nation nation;
	private Game game;
	private T resultValue;
	private boolean gameRequired = true;

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
			String subject = "Stratinit PlayerRequest Exception "
				+ nation.getName() + " " + (game == null ? "NO_GAME" : game.getId());
			smtpService.sendException(subject, e);

			retval = new Result<T>(message, false);
		}
		return retval;
	}

	private void addCommandPoints(Result<T> result) {
		if (getNation() != null) {
			result.setCommandPoints(getNation().getCommandPoints());
		}
	}

	// Required for game joining where no nation exists yet
	public Result<T> process(int gameId) {
		game = gameDao.findGame(gameId);
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
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		if (authentication == null) {
			return;
		}
		String username = authentication.getName();
		player = playerDao.find(username);
		nation = setContext(player);
	}

	protected Player getPlayer() {
		return player;
	}

	protected Nation getNation() {
		return nation;
	}

	protected Game getGame() {
		if (game != null) {
			return game;
		}
		if (nation == null) {
			return null;
		}
		return nation.getGame();
	}

	protected Nation setContext(Player player) {
		context.initialize(player, sessionManager);
		return context.getNation();
	}

	public boolean isGameRequired() {
		return gameRequired;
	}
}
