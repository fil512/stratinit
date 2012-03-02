package com.kenstevens.stratinit.site;

import com.kenstevens.stratinit.ui.shell.ProgressBarControl;



public class CommandExecutor {
	protected static final int MAX_SECONDS = 60;
	protected static final int INCREMENT_SECONDS = 100;

	private final ProgressBarControl progressBarControl;
	private boolean done;

	public CommandExecutor(ProgressBarControl progressBarControl) {
		this.progressBarControl = progressBarControl;
	}

	public void execute(final Command<? extends Object> command) {

		Runnable execution = new Runnable() {
			@Override
			public void run() {
				try {
					while (!done) {
						progressBarControl.reset();
						progressBarControl.setMaximum(MAX_SECONDS);
						for (int i = 0; !done && i < MAX_SECONDS; ++i) {
							progressBarControl.incrementSelection();
							Thread.sleep(INCREMENT_SECONDS);
						}
					}
				} catch (InterruptedException e) {
					done = true;
				}
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
