package com.kenstevens.stratinit.client.site;

import com.kenstevens.stratinit.client.api.IProgressBar;
import com.kenstevens.stratinit.client.api.IStatusReporter;
import com.kenstevens.stratinit.client.event.ArrivedDataEventAccumulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommandProcessor {
    @Autowired
    private IProgressBar progressBar;
    @Autowired
    private IStatusReporter statusReporter;
    @Autowired
    private ArrivedDataEventAccumulator arrivedDataEventAccumulator;
    @Autowired
    private CommandExecutorFactory commandExecutorFactory;

    private Command<? extends Object> lastCommand = null;

    public void process(Action action) {
        Command<? extends Object> command = action.getCommand();
        try {
            statusReporter.reportAction(command.getDescription());
            CommandExecutor commandExecutor = commandExecutorFactory.newCommandExecutor();
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
