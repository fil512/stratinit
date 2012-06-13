package com.kenstevens.stratinit.ui.window;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.Subscribe;
import com.kenstevens.stratinit.control.TopLevelController;
import com.kenstevens.stratinit.event.EmptyGameListEvent;
import com.kenstevens.stratinit.event.GameListArrivedEvent;
import com.kenstevens.stratinit.event.StratinitEventBus;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.GameView;
import com.kenstevens.stratinit.shell.WidgetContainer;
import com.kenstevens.stratinit.site.action.ActionFactory;
import com.kenstevens.stratinit.ui.tabs.GameTable;
import com.kenstevens.stratinit.ui.tabs.GameTableControl;
import com.kenstevens.stratinit.util.Spring;

@Scope("prototype")
@Component
public class ManageGamesWindowControl implements TopLevelController {
	private final Log logger = LogFactory.getLog(getClass());

	@Autowired
	private StratinitEventBus eventBus;

	@Autowired
	private ActionFactory actionFactory;
	@Autowired
	private Spring spring;
	@Autowired
	private Data db;
	@Autowired
	private WidgetContainer widgetContainer;

	private final ManageGamesWindow window;

	private GameTableControl gameTableControl;

	public ManageGamesWindowControl(ManageGamesWindow window) {
		this.window = window;
		setButtonListeners();
		setTabFolderListeners();
		setTableListeners();
	}
	
	@SuppressWarnings("unused")
	@PostConstruct
	private void addObservers() {
		eventBus.register(this);
	}

	@Subscribe
	public void handleEmptyGameListEvent(EmptyGameListEvent event) {
		window.selectJoinGameTab();
	}
	
	@Subscribe
	public void handleGameListArrivedEvent(GameListArrivedEvent event) {
		if (event.isJoinedGames() && db.getGameList().isEmpty()) {
			actionFactory.getUnjoinedGames();
			window.selectJoinGameTab();
		}
	}
	
	private final void setTableListeners() {
		final Table myGamesTable = window.getMyGamesTable().getTable();
		myGamesTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				super.mouseDoubleClick(e);
				selectGame();
			}
		});
		
		myGamesTable.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				TableItem[] items = myGamesTable.getSelection();
				if (items == null || items.length != 1) {
					return;
				}
				GameView game = (GameView) items[0].getData();
				if (game == null) {
					return;
				}
				window.setNoAlliancesButton(game);
			}
		});
	}

	private final void setTabFolderListeners() {
		final TabFolder tabFolder = window.getTabFolder();
		tabFolder.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (tabFolder.getSelectionIndex() == 1) {
					actionFactory.getUnjoinedGames();
					window.setNoAlliancesButton(null);
				}
			}
		});
	}

	private final void setButtonListeners() {
		window.getSelectGameButton().addSelectionListener(
				new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent e) {
						try {
							selectGame();
						} catch (Exception e1) {
							logger.error(e1.getMessage(), e1);
						}
					}
				});
		window.getJoinButton().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				try {
					joinGame(window.isNoAlliancesAllowedSelected());
					window.selectGamesTab();
				} catch (Exception e1) {
					logger.error(e1.getMessage(), e1);
				}
			}
		});
	}

	
	
	protected final void selectGame() {
		GameTable table = window.getMyGamesTable();
		TableItem[] items = table.getSelection();
		if (items.length < 1) {
			return;
		}
		Game game = (Game) items[0].getData();
		if (game == null) {
			return;
		}
		if (!game.isMapped()) {
			alertGameNotMapped(game);
			return;
		}
		boolean noAlliancesAllowedSelected = window.isNoAlliancesAllowedSelected();
		window.close();
		widgetContainer.getGameManager().selectGame(game, noAlliancesAllowedSelected);
	}

	private boolean alertGameNotMapped(Game game) {
		MessageBox messageBox = new MessageBox(window.getShell(), SWT.OK
				| SWT.CANCEL | SWT.ICON_WARNING);
		messageBox.setMessage("Cannot load game " + game
				+ " since it is not open yet.");
		int button = messageBox.open();
		return button == SWT.OK;
	}

	public void setControllers() {
		gameTableControl = spring.autowire(new GameTableControl(window
				.getMyGamesTable(), db.getGameList()));
		spring.autowire(new GameTableControl(window.getJoinGameTable(), db
				.getUnjoinedGameList()));
	}

	private void joinGame(boolean noAlliances) {
		GameTable table = window.getJoinGameTable();
		TableItem[] items = table.getSelection();
		if (items.length < 1) {
			return;
		}
		Game game = (Game) items[0].getData();
		if (game == null) {
			return;
		}
		int gameId = game.getId();
		actionFactory.joinGame(gameId, noAlliances);
	}

	public void setContents() {
		gameTableControl.setContents();
	}
}
