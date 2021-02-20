package com.kenstevens.stratinit.ui.window;

import com.kenstevens.stratinit.model.Account;
import com.kenstevens.stratinit.shell.StatusReporter;
import com.kenstevens.stratinit.shell.TopShell;
import com.kenstevens.stratinit.site.action.ActionFactory;
import com.kenstevens.stratinit.util.Spring;
import org.eclipse.swt.widgets.Display;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WindowDirectorImpl implements WindowDirector {

	@Autowired
	private AccountSettingsWindow accountSettingsWindow;

	@Autowired
	private TopShell topShell;
	@Autowired
	private Spring spring;
	@Autowired
	private ActionFactory actionFactory;
	@Autowired
	private Account account;
	@Autowired
	private StatusReporter statusReporter;
	@Autowired
	private NewsWindow newsWindow;
	@Autowired
	private StatsWindow statsWindow;
	@Autowired
	private ManageGamesWindow manageGamesWindow;

	public void accountSettingsWindow() {
		topShell.open(accountSettingsWindow);
		accountSettingsWindow.setContents();
	}

	public void openNewsWindow() {
		final Display display = Display.getDefault();

		if (display.isDisposed()) {
			return;
		}
		display.asyncExec(new Runnable() {
			public void run() {
				topShell.open(newsWindow);
				spring.autowire(new NewsWindowControl( newsWindow ));
				actionFactory.readMessageBoard();
				actionFactory.getNews();
			}
		});

	}

	public void openStatsWindow(final int gameId) {
		final Display display = Display.getDefault();

		if (display.isDisposed()) {
			return;
		}
		display.asyncExec(new Runnable() {
			public void run() {
				topShell.open(statsWindow);
				statsWindow.setContents(gameId);
			}
		});

	}

	public void chooseGameWindow() {
		if (!authenticate()) {
			return;
		}
		topShell.open(manageGamesWindow);

		ManageGamesWindowControl manageGamesWindowController = spring.autowire(new ManageGamesWindowControl( manageGamesWindow ));
		manageGamesWindowController.setControllers();
		manageGamesWindowController.setContents();
		actionFactory.getGames();
	}

	private boolean authenticate() {
		if (account.getUsername().isEmpty()) {
			statusReporter
					.reportError("Please specify a username in Account Settings before managing games...");
			return false;
		} else if (account.getPassword().isEmpty()) {
			statusReporter
					.reportError("Please specify a password in Account Settings before managing games...");
			return false;
		}
		return true;
	}

}
