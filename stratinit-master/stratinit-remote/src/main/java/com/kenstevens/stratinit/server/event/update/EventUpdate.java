package com.kenstevens.stratinit.server.event.update;

import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.server.remote.helper.DataWriter;
import com.kenstevens.stratinit.server.remote.helper.SynchronizedDataAccess;
import com.kenstevens.stratinit.server.remote.mail.SMTPService;
import com.kenstevens.stratinit.server.remote.state.ServerStatus;
import com.kenstevens.stratinit.util.StackTraceHelper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public abstract class EventUpdate implements DataWriter {
	@Autowired
	DataCache dataCache;
	@Autowired
	private GameDao gameDao;
	@Autowired
	private ServerStatus serverStatus;
	private Game game;
	@Autowired
	private SMTPService smtpService;
	private SynchronizedDataAccess synchronizedDataAccess;

	@PostConstruct
	public void init() {
		synchronizedDataAccess = new SynchronizedDataAccess(dataCache, this);
	}

	public final void update(int gameId) {
		try {
			checkRunning();
			game = gameDao.findGame(gameId);
			synchronizedDataAccess.write(game);
		} catch (RuntimeException e) {
			smtpService.sendException("Stratinit Update Exception " + gameId, StackTraceHelper.getStackTrace(e));
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
