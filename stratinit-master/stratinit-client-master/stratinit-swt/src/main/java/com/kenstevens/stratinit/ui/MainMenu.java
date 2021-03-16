package com.kenstevens.stratinit.ui;

import com.kenstevens.stratinit.client.util.OSValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class MainMenu extends Composite {
	private MenuItem mapMenuItem;
	private MenuItem messagesMenuItem;
	private MenuItem forumMenuItem;
	private MenuItem statsMenuItem;
	private MenuItem gameInfoMenuItem;
	private MenuItem accountSettingsMenuItem;
	private MenuItem manageGamesItemMenuItem;
	private MenuItem newsMenuItem;
	private MenuItem helpMenuItem;
	private MenuItem supportMenuItem;
	private MenuItem concedeMenuItem;
	private MenuItem prefsMenuItem;

	/**
	 * @wbp.parser.entryPoint
	 */
	public MainMenu(Composite parent, int style) {
		super(parent, style);
		createContents(parent.getShell());
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	private void createContents(Shell shell) {
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		if (OSValidator.isMac()) {
			MenuItem menuHeader = new MenuItem(menu, SWT.CASCADE);
			menuHeader.setText("&Tools");
			menu = new Menu(shell, SWT.DROP_DOWN);
			menuHeader.setMenu(menu);
			
		}

		mapMenuItem = new MenuItem(menu, SWT.NONE);
		mapMenuItem.setText("Map");

		messagesMenuItem = new MenuItem(menu, SWT.NONE);
		messagesMenuItem.setText("Inbox");

		forumMenuItem = new MenuItem(menu, SWT.NONE);
		forumMenuItem.setText("Message Board");

		newsMenuItem = new MenuItem(menu, SWT.NONE);
		newsMenuItem.setText("News");

		statsMenuItem = new MenuItem(menu, SWT.NONE);
		statsMenuItem.setText("Stats");

		gameInfoMenuItem = new MenuItem(menu, SWT.NONE);
		gameInfoMenuItem.setText("Technology");

		manageGamesItemMenuItem = new MenuItem(menu, SWT.NONE);
		manageGamesItemMenuItem.setText("Games");

		accountSettingsMenuItem = new MenuItem(menu, SWT.NONE);
		accountSettingsMenuItem.setText("Account Settings");

		concedeMenuItem = new MenuItem(menu, SWT.NONE);
		concedeMenuItem.setText("Resign");

		prefsMenuItem = new MenuItem(menu, SWT.NONE);
		prefsMenuItem.setText("Prefs");

		helpMenuItem = new MenuItem(menu, SWT.NONE);
		helpMenuItem.setText("Help");

		supportMenuItem = new MenuItem(menu, SWT.NONE);
		supportMenuItem.setText("Support");
	}

	public MenuItem getMapMenuItem() {
		return mapMenuItem;
	}

	public MenuItem getMessagesMenuItem() {
		return messagesMenuItem;
	}

	public MenuItem getGameInfoMenuItem() {
		return gameInfoMenuItem;
	}

	public MenuItem getAccountSettingsMenuItem() {
		return accountSettingsMenuItem;
	}

	public MenuItem getManageGamesItemMenuItem() {
		return manageGamesItemMenuItem;
	}

	public MenuItem getNewsMenuItem() {
		return newsMenuItem;
	}

	public MenuItem getStatsMenuItem() {
		return statsMenuItem;
	}

	public MenuItem getHelpMenuItem() {
		return helpMenuItem;
	}

	public MenuItem getSupportMenuItem() {
		return supportMenuItem;
	}
	public MenuItem getForumMenuItem() {
		return forumMenuItem;
	}
	public MenuItem getConcedeMenuItem() {
		return concedeMenuItem;
	}
	public MenuItem getPrefsMenuItem() {
		return prefsMenuItem;
	}
}
