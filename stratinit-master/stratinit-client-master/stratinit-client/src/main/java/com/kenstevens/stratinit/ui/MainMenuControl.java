package com.kenstevens.stratinit.ui;

import com.google.common.eventbus.Subscribe;
import com.kenstevens.stratinit.event.NationListArrivedEvent;
import com.kenstevens.stratinit.event.StratinitEventBus;
import com.kenstevens.stratinit.main.ClientConstants;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.NationView;
import com.kenstevens.stratinit.shell.StatusReporter;
import com.kenstevens.stratinit.shell.TopShell;
import com.kenstevens.stratinit.site.action.post.ActionFactory;
import com.kenstevens.stratinit.ui.image.ImageLibrary;
import com.kenstevens.stratinit.ui.messages.MessageWindow;
import com.kenstevens.stratinit.ui.messages.MessageWindowControl;
import com.kenstevens.stratinit.ui.window.*;
import com.kenstevens.stratinit.util.Spring;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Scope("prototype")
@Component
public class MainMenuControl {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final MainMenu mainMenu;
	@Autowired
	private MessageWindow messageWindow;
	@Autowired
	private MapWindow mapWindow;

	@Autowired
	private GameInfoWindow gameInfoWindow;
	@Autowired
	private PrefsWindow prefsWindow;
	@Autowired
	private Spring spring;
	@Autowired
	private StatusReporter statusReporter;
	@Autowired
	private ActionFactory actionFactory;
	@Autowired
	private ImageLibrary imageLibrary;
	@Autowired
	private TopShell topShell;
	@Autowired
	private WindowDirector windowDirector;
	@Autowired
	private Data db;
	@Autowired
	protected StratinitEventBus eventBus;

	public MainMenuControl(MainMenu mainMenu) {
		this.mainMenu = mainMenu;
		setMenuListeners();
	}
	@Subscribe
	public void handleNationListArrivedEvent(NationListArrivedEvent event) {
		setFlag();
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void addObservers() {
		eventBus.register(this);
	}

	private void setFlag() {
		Nation me = db.getNation();
		MenuItem messagesMenuItem = mainMenu.getMessagesMenuItem();
		if (me.isNewMail()) {
			messagesMenuItem.setImage(imageLibrary.getFlag());
		} else {
			messagesMenuItem.setImage(null);
		}
	}

	private void setMenuListeners() {
		mainMenu.getMapMenuItem().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				topShell.open(mapWindow);

				MapWindowControl mapWindowControl = spring
						.autowire(new MapWindowControl(mapWindow.getCanvas()));
				mapWindowControl.redraw();
			}
		});
		mainMenu.getMessagesMenuItem().addSelectionListener(
				new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent e) {
						if (!db.isLoggedIn()) {
							statusReporter.reportLoginError();
							return;
						}
						// TODO REF Constants
						openMessageWindow(1);

						actionFactory.readMessages();
					}
				});

		mainMenu.getForumMenuItem().addSelectionListener(
				new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent e) {
						if (!db.isLoggedIn()) {
							statusReporter.reportLoginError();
							return;
						}
						// TODO REF Constants
						openMessageWindow(0);

						actionFactory.readMessageBoard();
					}
				});

		mainMenu.getManageGamesItemMenuItem().addSelectionListener(
				new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent e) {
						windowDirector.chooseGameWindow();
					}
				});
		mainMenu.getAccountSettingsMenuItem().addSelectionListener(
				new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent e) {
						windowDirector.accountSettingsWindow();
					}
				});
		mainMenu.getPrefsMenuItem().addSelectionListener(
				new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent e) {
						try {
							topShell.open(prefsWindow);
							PrefsWindowControl prefsWindowControl = spring
									.autowire(new PrefsWindowControl(
											prefsWindow));
							prefsWindowControl.setContents();
						} catch (Exception e1) {
							statusReporter.reportError(e1);
							logger.error(e1.getMessage(), e1);
						}
					}
				});
		mainMenu.getGameInfoMenuItem().addSelectionListener(
				new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent e) {
						if (!db.isLoggedIn()) {
							statusReporter.reportLoginError();
							return;
						}
						try {
							topShell.open(gameInfoWindow);
							GameInfoWindowControl gameInfoWindowControl = spring
									.autowire(new GameInfoWindowControl(
											gameInfoWindow));
							gameInfoWindowControl.addObservers();
							actionFactory.getNations();
						} catch (Exception e1) {
							statusReporter.reportError(e1);
							logger.error(e1.getMessage(), e1);
						}
					}
				});
		mainMenu.getNewsMenuItem().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (!db.isLoggedIn()) {
					statusReporter.reportLoginError();
					return;
				}
				windowDirector.openNewsWindow();
			}
		});

		mainMenu.getStatsMenuItem().addSelectionListener(
				new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent e) {
						if (!db.isLoggedIn()) {
							statusReporter.reportLoginError();
							return;
						}
						windowDirector.openStatsWindow(db.getSelectedGameId());
					}
				});
		mainMenu.getConcedeMenuItem().addSelectionListener(
				new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent e) {
						if (acceptConcede(true)) {
							if (acceptConcede(false)) {
								actionFactory.concede();
							}
						}
					}
				});
		mainMenu.getHelpMenuItem().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				topShell.browse(ClientConstants.HELP_URL);
			}
		});
		mainMenu.getSupportMenuItem().addSelectionListener(
				new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent e) {
						topShell.browse(ClientConstants.SUPPORT_URL);
					}
				});
	}

	private boolean acceptConcede(boolean firstTime) {
		MessageBox messageBox = new MessageBox(topShell.getShell(), SWT.OK
				| SWT.CANCEL | SWT.ICON_WARNING);
		NationView ally = db.getAlly();
		String really = firstTime ? "" : "REALLY ";
		if (ally != null && ally.getCities() > 0) {
			messageBox.setMessage("You are about to resign from this game.\n"
					+ "This will cede all your cities and units to "
					+ ally.getName() + ".\n\n" + "Are you " + really
					+ "sure you want to do this?");
		} else {
			messageBox.setMessage("You are about to resign from this game.\n"
					+ "This will destroy all your cities and units.\n\n"
					+ "Are you " + really + "sure you want to do this?");
		}
		int button = messageBox.open();
		return button == SWT.OK;
	}

	private void openMessageWindow(int tab) {
		topShell.open(messageWindow);

		MessageWindowControl messageWindowControl = spring
				.getBean(MessageWindowControl.class);
		messageWindowControl.setControllers();
		messageWindowControl.setContents();
		messageWindowControl.setTab(tab);
	}
}
