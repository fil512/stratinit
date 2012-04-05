package com.kenstevens.stratinit.server.event;

import org.springframework.beans.factory.annotation.Autowired;

import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.main.Spring;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.server.remote.helper.DataWriter;
import com.kenstevens.stratinit.server.remote.helper.SynchronizedDataAccess;
import com.kenstevens.stratinit.server.remote.mail.SMTPService;
import com.kenstevens.stratinit.server.remote.state.ServerStatus;
import com.kenstevens.stratinit.util.StackTraceHelper;

public abstract class EventUpdate implements DataWriter {
	@Autowired
	private GameDao gameDao;
	@Autowired
	private ServerStatus serverStatus;
	private Game game;
	@Autowired
	private Spring spring;
	@Autowired
	private SMTPService smtpService;

	public final void update(int gameId) {
		try {
			checkRunning();
			game = gameDao.findGame(gameId);
			spring.autowire(new SynchronizedDataAccess(game, this)).write();
		} catch (RuntimeException e) {
			smtpService.sendException("Stratinit Update Exception "+gameId, StackTraceHelper.getStackTrace(e));
			throw e;
		}
	}

	public void writeData() {
		executeWrite();
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
