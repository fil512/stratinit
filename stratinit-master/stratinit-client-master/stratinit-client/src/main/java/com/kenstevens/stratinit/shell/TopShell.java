package com.kenstevens.stratinit.shell;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;




@Component
public final class TopShell {
	@Autowired
	private BrowserWindow browserWindow;

	private Shell shell;

	public void setShell(Shell shell) {
		this.shell = shell;
	}

	public Shell getShell() {
		return shell;
	}

	public void open(StratInitWindow window) {
		window.open(shell);
	}

	public void browse(final String url) {
		final Display display = Display.getDefault();

		if (display.isDisposed()) {
			return;
		}
		display.asyncExec(new Runnable() {
			public void run() {
				browserWindow.open(shell, url);
			}
		});
	}
}
