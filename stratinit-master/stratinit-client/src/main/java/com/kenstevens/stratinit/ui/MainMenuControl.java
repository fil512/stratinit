package com.kenstevens.stratinit.ui;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gwt.event.shared.HandlerManager;
import com.kenstevens.stratinit.event.NationListArrivedEvent;
import com.kenstevens.stratinit.event.NationListArrivedEventHandler;
import com.kenstevens.stratinit.main.ClientConstants;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.NationView;
import com.kenstevens.stratinit.site.action.ActionFactory;
import com.kenstevens.stratinit.ui.image.ImageLibrary;
import com.kenstevens.stratinit.ui.messages.MessageWindow;
import com.kenstevens.stratinit.ui.messages.MessageWindowControl;
import com.kenstevens.stratinit.ui.shell.StatusReporter;
import com.kenstevens.stratinit.ui.shell.TopShell;
import com.kenstevens.stratinit.ui.window.GameInfoWindow;
import com.kenstevens.stratinit.ui.window.GameInfoWindowControl;
import com.kenstevens.stratinit.ui.window.MapWindow;
import com.kenstevens.stratinit.ui.window.MapWindowControl;
import com.kenstevens.stratinit.ui.window.PrefsWindow;
import com.kenstevens.stratinit.ui.window.PrefsWindowControl;
import com.kenstevens.stratinit.ui.window.WindowDirector;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class MainMenuControl {
	private final Log logger = LogFactory.getLog(getClass());

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
	private HandlerManager handlerManager;
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

	public MainMenuControl(MainMenu mainMenu) {
		this.mainMenu = mainMenu;
		setMenuListeners();
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void addObservers() {
		handlerManager.addHandler(NationListArrivedEvent.TYPE,
				new NationListArrivedEventHandler() {
					@Override
					public void dataArrived() {
						setFlag();
					}
				});
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
