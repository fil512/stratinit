package com.kenstevens.stratinit.site;

import com.kenstevens.stratinit.dto.SIBattleLog;
import com.kenstevens.stratinit.event.ArrivedDataEventAccumulator;
import com.kenstevens.stratinit.event.CommandPointsArrivedEvent;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.NationView;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.rest.IStratInitServer;
import com.kenstevens.stratinit.shell.StatusReporter;
import com.kenstevens.stratinit.site.processor.BattleLogProcessor;
import com.kenstevens.stratinit.site.processor.ResultBattleLogProcessor;
import com.kenstevens.stratinit.type.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;

import java.util.List;

public abstract class Command<T> {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	protected IStratInitServer stratInitServer;
	@Autowired
	private StatusReporter statusReporter;
	@Autowired
	protected ArrivedDataEventAccumulator arrivedDataEventAccumulator;
	@Autowired
	private ResultBattleLogProcessor resultBattleLogProcessor;
	@Autowired
	private BattleLogProcessor battleLogProcessor;
	@Autowired
	private Data db;

	public abstract Result<T> execute();

	public abstract String getDescription();

	public void process() {
		try {
			Result<T> result = execute();
			if (result.isSuccess()) {
				handleSuccess(result.getValue());
				processResult(result);
				processBattleLogs(result.getBattleLogs());
			} else {
				reportError(getDescription());
			}
			statusReporter.reportResult(this, result);
		} catch (RemoteConnectFailureException e) {
			statusReporter.reportError("The server is down.  Try again later.");
			logger.error(e.getMessage(), e);
		} catch (RemoteAccessException e) {
			statusReporter
					.reportError("Server error.  Possibly invalid username or password.  Please check stratinit.log or your Account Settings and try again.");
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			statusReporter.reportError(e);
			logger.error(e.getMessage(), e);
		}
	}

	private void processResult(Result<T> result) {
		if (result.getCommandPoints() == Constants.UNASSIGNED) {
			return;
		}
		NationView nation = db.getNation();
		if (nation != null) {
			nation.setCommandPoints(result.getCommandPoints());
			arrivedDataEventAccumulator.addEvent(new CommandPointsArrivedEvent());
		}
	}

	protected void reportError(String description) {
		statusReporter.reportError("ERROR: [" + description + "] FAILED.");
	}

	public abstract void handleSuccess(T result);

	private void processBattleLogs(List<SIBattleLog> silogs) {
		if (silogs.isEmpty()) {
			return;
		}
		resultBattleLogProcessor.process(silogs);
		battleLogProcessor.process(silogs);
	}
}
