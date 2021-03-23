package com.kenstevens.stratinit.client.site;

import com.kenstevens.stratinit.client.api.IProgressBar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class CommandExecutor {
	protected static final int MAX_SECONDS = 60;
	protected static final int INCREMENT_SECONDS = 100;

	@Autowired
	private IProgressBar progressBar;
	private boolean done;

	public void execute(final Command<? extends Object> command) {

		Runnable execution = () -> {
			try {
				while (!done) {
					progressBar.reset();
					progressBar.setMaximum(MAX_SECONDS);
					for (int i = 0; !done && i < MAX_SECONDS; ++i) {
						progressBar.incrementSelection();
						Thread.sleep(INCREMENT_SECONDS);
					}
				}
			} catch (InterruptedException e) {
				done = true;
			}
		};
		final Thread progressBarThread = new Thread(execution);
		progressBarThread.start();

		try {
			command.process();
		} finally {
			done = true;
			if (progressBarThread.isAlive()) {
				progressBarThread.interrupt();
			}
		}
	}
}
