package com.kenstevens.stratinit.client.site;

import com.kenstevens.stratinit.client.api.IProgressBar;
import com.kenstevens.stratinit.client.event.ArrivedDataEventAccumulator;
import com.kenstevens.stratinit.shell.StatusReporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommandProcessor {
	@Autowired
	private IProgressBar progressBar;
	@Autowired
	private StatusReporter statusReporter;
	@Autowired
	private ArrivedDataEventAccumulator arrivedDataEventAccumulator;

	private Command<? extends Object> lastCommand = null;

	public void process(Action action) {
		Command<? extends Object> command = action.getCommand();
		try {
			statusReporter.reportAction(command.getDescription());
			CommandExecutor commandExecutor = new CommandExecutor();
			arrivedDataEventAccumulator.clear();
			action.preRequest();
			commandExecutor.execute(command);
			action.postRequest();
			arrivedDataEventAccumulator.fireEvents();
			action.postEvents();
			lastCommand = command;
		} finally {
			progressBar.reset();
		}
	}

	public Command<? extends Object> getLastCommand() {
		return lastCommand;
	}
}
