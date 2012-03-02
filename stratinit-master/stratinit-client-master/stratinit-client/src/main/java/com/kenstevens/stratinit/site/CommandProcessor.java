package com.kenstevens.stratinit.site;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.event.ArrivedDataEventAccumulator;
import com.kenstevens.stratinit.ui.shell.StatusReporter;
import com.kenstevens.stratinit.ui.shell.WidgetContainer;

@Service
public class CommandProcessor {
	@Autowired
	private WidgetContainer widgetContainer;
	@Autowired
	private StatusReporter statusReporter;
	@Autowired
	private ArrivedDataEventAccumulator arrivedDataEventAccumulator;

	private Command<? extends Object> lastCommand = null;

	public void process(Action action) {
		Command<? extends Object> command = action.getCommand();
		try {
			statusReporter.reportAction(command.getDescription());
			CommandExecutor commandExecutor = new CommandExecutor(widgetContainer.getProgressBarControl());
			arrivedDataEventAccumulator.clear();
			action.preRequest();
			commandExecutor.execute(command);
			action.postRequest();
			arrivedDataEventAccumulator.fireEvents();
			action.postEvents();
			lastCommand = command;
		} finally {
			widgetContainer.getProgressBarControl().reset();
		}
	}

	public Command<? extends Object> getLastCommand() {
		return lastCommand;
	}
}
