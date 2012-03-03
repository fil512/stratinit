package com.kenstevens.stratinit.site;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.RemoteConnectFailureException;

import com.kenstevens.stratinit.dto.SIBattleLog;
import com.kenstevens.stratinit.event.ArrivedDataEventAccumulator;
import com.kenstevens.stratinit.event.CommandPointsArrivedEvent;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.NationView;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.StratInit;
import com.kenstevens.stratinit.site.processor.BattleLogProcessor;
import com.kenstevens.stratinit.site.processor.ResultBattleLogProcessor;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.ui.shell.StatusReporter;

public abstract class Command<T> {
	private final Log logger = LogFactory.getLog(getClass());
	@Autowired
	protected StratInit stratInit;
	@Autowired
	private StatusReporter statusReporter;
	@Autowired
	private ResultBattleLogProcessor resultBattleLogProcessor;
	@Autowired
	private BattleLogProcessor battleLogProcessor;
	@Autowired
	private Data db;

	@Autowired
	protected ArrivedDataEventAccumulator arrivedDataEventAccumulator;

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
			statusReporter.reportResult(result);
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
