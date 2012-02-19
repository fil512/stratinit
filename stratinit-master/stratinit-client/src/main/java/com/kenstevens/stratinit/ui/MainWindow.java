package com.kenstevens.stratinit.ui;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gwt.event.shared.HandlerManager;
import com.kenstevens.stratinit.audio.WavPlayer;
import com.kenstevens.stratinit.control.CommandListControlImpl;
import com.kenstevens.stratinit.control.TextControl;
import com.kenstevens.stratinit.event.CommandPointsArrivedEvent;
import com.kenstevens.stratinit.event.CommandPointsArrivedEventHandler;
import com.kenstevens.stratinit.event.GameChangedEvent;
import com.kenstevens.stratinit.event.NationListArrivedEvent;
import com.kenstevens.stratinit.event.NationListArrivedEventHandler;
import com.kenstevens.stratinit.event.StatusReportEvent;
import com.kenstevens.stratinit.event.StatusReportEventHandler;
import com.kenstevens.stratinit.main.ClientConstants;
import com.kenstevens.stratinit.model.Account;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.SelectedCoords;
import com.kenstevens.stratinit.model.SelectedUnits;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.site.ProgressBarControlImpl;
import com.kenstevens.stratinit.site.action.ActionFactory;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.ui.image.ImageLibrary;
import com.kenstevens.stratinit.ui.selection.MapCentre;
import com.kenstevens.stratinit.ui.shell.CommandListControl;
import com.kenstevens.stratinit.ui.shell.GameManager;
import com.kenstevens.stratinit.ui.shell.MapControl;
import com.kenstevens.stratinit.ui.shell.Message;
import com.kenstevens.stratinit.ui.shell.StatusReporter;
import com.kenstevens.stratinit.ui.shell.TopShell;
import com.kenstevens.stratinit.ui.shell.WidgetContainer;
import com.kenstevens.stratinit.ui.tabs.BattleTabItem;
import com.kenstevens.stratinit.ui.tabs.BattleTabItemControl;
import com.kenstevens.stratinit.ui.tabs.CityTabItem;
import com.kenstevens.stratinit.ui.tabs.CityTabItemControl;
import com.kenstevens.stratinit.ui.tabs.ControllerManager;
import com.kenstevens.stratinit.ui.tabs.FutureTabItem;
import com.kenstevens.stratinit.ui.tabs.FutureTabItemControl;
import com.kenstevens.stratinit.ui.tabs.HistoryTabItem;
import com.kenstevens.stratinit.ui.tabs.HistoryTabItemControl;
import com.kenstevens.stratinit.ui.tabs.PlayerTabItem;
import com.kenstevens.stratinit.ui.tabs.PlayerTabItemControl;
import com.kenstevens.stratinit.ui.tabs.SectorTabItem;
import com.kenstevens.stratinit.ui.tabs.SectorTabItemControl;
import com.kenstevens.stratinit.ui.tabs.SupplyTabItem;
import com.kenstevens.stratinit.ui.tabs.SupplyTabItemControl;
import com.kenstevens.stratinit.ui.tabs.TabManager;
import com.kenstevens.stratinit.ui.tabs.UnitTabItem;
import com.kenstevens.stratinit.ui.tabs.UnitTabItemControl;
import com.kenstevens.stratinit.ui.window.MapCanvasControl;
import com.kenstevens.stratinit.ui.window.MapImageManager;
import com.kenstevens.stratinit.ui.window.WindowDirector;
import com.kenstevens.stratinit.util.AccountPersister;
import com.kenstevens.stratinit.util.Spring;

@Component("MainWindow")
public class MainWindow implements MapControl, GameManager {
	private final Logger logger = Logger.getLogger(getClass());

	private Label distanceValueLabel;
	private Label coordsLabel;
	private Button updateButton;

	protected Shell shell;
	private MapCanvasControl mapCanvasControl;
	private FutureTabItemControl futureTabItemControl;
	private TextControl statusControl;

	@Autowired
	private MapImageManager mapImageManager;
	@Autowired
	private AccountPersister accountPersister;
	@Autowired
	private ImageLibrary imageLibrary;

	@Autowired
	private Data db;
	@Autowired
	private ActionFactory actionFactory;
	@Autowired
	private StatusReporter statusReporter;
	@Autowired
	private Spring spring;
	@Autowired
	private NewbHelper newbHelper;
	@Autowired
	private MapCentre mapCentre;
	@Autowired
	private TopShell topShell;
	@Autowired
	private WidgetContainer widgetContainer;
	@Autowired
	private WavPlayer wavPlayer;
	@Autowired
	private WindowDirector windowDirector;
	@Autowired
	private SelectedUnits selectedUnits;
	@Autowired
	private Account account;
	@Autowired
	private TabManager tabManager;
	@Autowired
	private ControllerManager controllerManager;
	@Autowired
	private HandlerManager handlerManager;

	private SupplyTabItemControl supplyTabItemControl;
	private Label distanceLabel;

	private Label lines;

	private TabItem battleTab;
	private Label commandPointsLabel;

	private SectorTabItem sectorTabItem;

	private SectorTabItemControl sectorTabItemControl;

	private UnitTabItemControl unitTabItemControl;

	/**
	 * Launch the application
	 * 
	 * @param args
	 */

	/**
	 * Open the window
	 */
	public void open() {
		setWidgetContainerValues();
		final Display display = Display.getDefault();

		try {
			buildInterface();
			setShellSize();
			shell.layout();
			shell.open();
			newbHelper.openNextWindow();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
		} finally {
			persistAccount();
			if (display.isDisposed()) {
				display.dispose();
			}
		}
	}

	private void buildInterface() {
		loadFiles();
		createContents();
		controllerManager.setControllers();
		addHandlers();
		supplyTabItemControl.setContents();
		controllerManager.setTitle(shell);
		mapImageManager.buildImage();
	}

	private void loadFiles() {
		String loadResult;
		try {
			loadResult = accountPersister.load();
			imageLibrary.loadImages();
			statusReporter.reportResult(loadResult);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			loadResult = e.getMessage();
			statusReporter.reportError(loadResult);
			submitError(e);
		}
	}

	private void setWidgetContainerValues() {
		// TODO REF these should be three separate widgets
		widgetContainer.setMapControl(this);
		widgetContainer.setTabControl(tabManager);
		widgetContainer.setGameManager(this);
	}

	private void addHandlers() {
		handlerManager.addHandler(NationListArrivedEvent.TYPE,
				new NationListArrivedEventHandler() {
					@Override
					public void dataArrived() {
						updateNation();
					}
				});
		handlerManager.addHandler(StatusReportEvent.TYPE,
				new StatusReportEventHandler() {

					@Override
					public void reportStatus(Message message) {
						if (message == null) {
							statusControl.setMessage("");
							return;
						} else if (message.isError()) {
							statusControl.setError(message.getText());
						} else {
							statusControl.setMessage(message.getText());
						}

					}
				});
		handlerManager.addHandler(CommandPointsArrivedEvent.TYPE,
				new CommandPointsArrivedEventHandler() {
					@Override
					public void dataArrived() {
						updateNation();
					}
				});
	}

	private void submitError(Exception e) {
		actionFactory.submitError(e);
	}

	private void setShellSize() {
		Rectangle rect = Display.getCurrent().getClientArea();
		if (account.getWidth() > 0) {
			shell.setSize(account.getWidth(), account.getHeight());
		} else {
			shell.setSize(shell.computeSize(rect.width
					- ClientConstants.HMARGIN, rect.height
					- ClientConstants.VMARGIN));
		}
	}

	private void setShellLocation() {
		if (account.getX() > -1) {
			shell.setLocation(account.getX(), account.getY());
		} else {
			shell.setLocation(0, 0);
		}
	}

	private void persistAccount() {
		try {
			accountPersister.save();
		} catch (Exception e) {
			logger.error("Failed to save file: " + e.getMessage());
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Create contents of the window
	 * 
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		shell = new Shell();
		setShellLocation();
		topShell.setShell(shell);
		shell.setLayout(new FormLayout());
		shell.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(final MouseEvent e) {
			}
		});
		shell.setSize(1072, 735);
		controllerManager.setTitle(shell);

		Canvas canvas = new Canvas(shell, SWT.NO_BACKGROUND
				| SWT.NO_REDRAW_RESIZE | SWT.V_SCROLL | SWT.H_SCROLL);

		final FormData fdCanvas = new FormData();
		fdCanvas.bottom = new FormAttachment(100, -5);
		fdCanvas.top = new FormAttachment(0, 5);
		fdCanvas.left = new FormAttachment(0, 5);
		canvas.setLayoutData(fdCanvas);

		MainMenu mainMenu = new MainMenu(shell, SWT.NONE);
		mainMenu.setLayoutData(new FormData());
		spring.autowire(new MainMenuControl(mainMenu));

		TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		fdCanvas.right = new FormAttachment(tabFolder, -5, SWT.LEFT);
		final FormData fdTabFolder = new FormData();
		fdTabFolder.top = new FormAttachment(0, 5);
		fdTabFolder.left = new FormAttachment(100, -421);
		fdTabFolder.right = new FormAttachment(100, -5);
		tabFolder.setLayoutData(fdTabFolder);

		mapCanvasControl = spring.autowire(new MapCanvasControl(canvas));
		controllerManager.add(mapCanvasControl);

		final TabItem sTabItem = new TabItem(tabFolder, SWT.NONE);
		tabManager.register(ClientConstants.SECTOR_TAB_ITEM_TITLE, sTabItem);
		sectorTabItem = new SectorTabItem(tabFolder, SWT.NONE);
		sTabItem.setControl(sectorTabItem);
		sectorTabItemControl = spring.autowire(new SectorTabItemControl(
				sectorTabItem));
		controllerManager.add(sectorTabItemControl);

		final TabItem uTabItem = new TabItem(tabFolder, SWT.NONE);
		tabManager.register(ClientConstants.UNIT_TAB_TITLE, uTabItem);
		UnitTabItem unitTabItem = new UnitTabItem(tabFolder, SWT.NONE);
		uTabItem.setControl(unitTabItem);
		unitTabItemControl = spring
				.autowire(new UnitTabItemControl(unitTabItem));
		controllerManager.add(unitTabItemControl);

		final TabItem cTabItem = new TabItem(tabFolder, SWT.NONE);
		tabManager.setCityTabIndex(tabManager.register(
				ClientConstants.CITY_TITLE, cTabItem));

		CityTabItem cityTabItem = new CityTabItem(tabFolder, SWT.NONE);
		cTabItem.setControl(cityTabItem);
		CityTabItemControl cityTabItemControl = spring
				.autowire(new CityTabItemControl(cityTabItem));
		new Label(cityTabItem.getBuildingCombos(), SWT.NONE);
		new Label(cityTabItem.getBuildingCombos(), SWT.NONE);
		new Label(cityTabItem.getBuildingCombos(), SWT.NONE);
		controllerManager.add(cityTabItemControl);

		battleTab = new TabItem(tabFolder, SWT.NONE);
		tabManager.setBattleTabIndex(tabManager.register(
				ClientConstants.BATTLE_TAB_ITEM_TITLE, battleTab));

		BattleTabItem battleTabItem = new BattleTabItem(tabFolder, SWT.NONE);
		battleTab.setControl(battleTabItem);
		BattleTabItemControl battleTabItemControl = spring
				.autowire(new BattleTabItemControl(battleTabItem));
		controllerManager.add(battleTabItemControl);

		final TabItem pTabItem = new TabItem(tabFolder, SWT.NONE);
		tabManager.setPlayerTabIndex(tabManager.register(
				ClientConstants.PLAYER_TAB_ITEM_TITLE, pTabItem));

		PlayerTabItem playerTabItem = new PlayerTabItem(tabFolder, SWT.NONE);
		playerTabItem.getRefreshButton().setImage(imageLibrary.getUpdate());
		pTabItem.setControl(playerTabItem);
		PlayerTabItemControl playerTabItemControl = spring
				.autowire(new PlayerTabItemControl(playerTabItem));
		controllerManager.add(playerTabItemControl);

		final TabItem hTabItem = new TabItem(tabFolder, SWT.NONE);
		tabManager.register(ClientConstants.HISTORY_TITLE, hTabItem);

		HistoryTabItem historyTabItem = new HistoryTabItem(tabFolder, SWT.NONE);
		hTabItem.setControl(historyTabItem);
		HistoryTabItemControl historyTabItemControl = spring
				.autowire(new HistoryTabItemControl(historyTabItem));
		controllerManager.add(historyTabItemControl);

		final TabItem fTabItem = new TabItem(tabFolder, SWT.NONE);
		tabManager.setFutureTabIndex(tabManager.register(
				ClientConstants.FUTURE_TITLE, fTabItem));

		FutureTabItem futureTabItem = new FutureTabItem(tabFolder, SWT.NONE);
		fTabItem.setControl(futureTabItem);
		futureTabItemControl = spring.autowire(new FutureTabItemControl(
				futureTabItem));
		controllerManager.add(futureTabItemControl);

		final TabItem mTabItem = new TabItem(tabFolder, SWT.NONE);
		tabManager.setSupplyTabIndex(tabManager.register(
				ClientConstants.SUPPLY_TAB_ITEM_TITLE, mTabItem));

		SupplyTabItem supplyTabItem = new SupplyTabItem(tabFolder, SWT.NONE);
		mTabItem.setControl(supplyTabItem);
		supplyTabItemControl = spring.autowire(new SupplyTabItemControl(
				supplyTabItem));
		controllerManager.add(supplyTabItemControl);

		org.eclipse.swt.widgets.List commandList;
		commandList = new org.eclipse.swt.widgets.List(shell, SWT.READ_ONLY
				| SWT.SIMPLE | SWT.V_SCROLL);
		fdTabFolder.bottom = new FormAttachment(100, -205);
		commandList.removeAll();
		final FormData fdCommandList = new FormData();
		fdCommandList.top = new FormAttachment(tabFolder, 5);
		fdCommandList.left = new FormAttachment(canvas, 5);
		fdCommandList.right = new FormAttachment(100, -49);
		fdCommandList.bottom = new FormAttachment(100, -145);
		commandList.setLayoutData(fdCommandList);

		final ProgressBar progressBar = new ProgressBar(shell, SWT.SMOOTH);
		final FormData fdProgressBar = new FormData();
		fdProgressBar.top = new FormAttachment(commandList, 6);
		fdProgressBar.left = new FormAttachment(canvas, 5);
		fdProgressBar.right = new FormAttachment(tabFolder, 0, SWT.RIGHT);
		progressBar.setLayoutData(fdProgressBar);
		ProgressBarControlImpl progressBarControl = new ProgressBarControlImpl(
				progressBar);
		widgetContainer.setProgressBarControl(progressBarControl);

		Text statusLabel;
		statusLabel = new Text(shell, SWT.V_SCROLL | SWT.H_SCROLL);
		fdProgressBar.bottom = new FormAttachment(statusLabel, -6);
		final FormData fdStatusLabel = new FormData();
		fdStatusLabel.right = new FormAttachment(tabFolder, 0, SWT.RIGHT);
		fdStatusLabel.left = new FormAttachment(canvas, 6);
		statusLabel.setLayoutData(fdStatusLabel);
		statusControl = new TextControl(statusLabel);

		updateButton = new Button(shell, SWT.NONE);
		fdStatusLabel.top = new FormAttachment(updateButton, -80, SWT.TOP);
		fdStatusLabel.bottom = new FormAttachment(updateButton, -6);
		updateButton.setToolTipText("Update all data");
		final FormData fdUpdateButton = new FormData();
		fdUpdateButton.right = new FormAttachment(tabFolder, -1, SWT.RIGHT);
		fdUpdateButton.bottom = new FormAttachment(100, -14);
		fdUpdateButton.top = new FormAttachment(100, -41);
		fdUpdateButton.left = new FormAttachment(100, -80);
		updateButton.setLayoutData(fdUpdateButton);
		updateButton.setEnabled(false);
		updateButton.setImage(imageLibrary.getUpdate());

		coordsLabel = new Label(shell, SWT.NONE);
		final FormData fdCoords = new FormData();
		fdCoords.top = new FormAttachment(updateButton, 0, SWT.TOP);
		fdCoords.left = new FormAttachment(canvas, 6);
		fdCoords.bottom = new FormAttachment(100, -19);
		fdCoords.right = new FormAttachment(100, -375);
		coordsLabel.setLayoutData(fdCoords);

		distanceLabel = new Label(shell, SWT.NONE);
		final FormData fdDistanceLabel = new FormData();
		fdDistanceLabel.left = new FormAttachment(coordsLabel, 6);
		fdDistanceLabel.top = new FormAttachment(statusLabel, 6);
		fdDistanceLabel.bottom = new FormAttachment(100, -24);
		distanceLabel.setLayoutData(fdDistanceLabel);
		distanceLabel.setText("Distance:");

		distanceValueLabel = new Label(shell, SWT.NONE);
		fdDistanceLabel.right = new FormAttachment(100, -312);
		final FormData fdDistance = new FormData();
		fdDistance.top = new FormAttachment(statusLabel, 6);
		fdDistance.right = new FormAttachment(updateButton, -181);
		fdDistance.left = new FormAttachment(distanceLabel, 6);
		fdDistance.bottom = new FormAttachment(100, -24);
		distanceValueLabel.setLayoutData(fdDistance);

		lines = new Label(shell, SWT.NONE);
		FormData fdLines = new FormData();
		fdLines.bottom = new FormAttachment(tabFolder, 23, SWT.BOTTOM);
		fdLines.right = new FormAttachment(commandList, 39, SWT.RIGHT);
		fdLines.top = new FormAttachment(tabFolder, 6);
		fdLines.left = new FormAttachment(commandList, 6);
		lines.setLayoutData(fdLines);
		CommandListControl commandListControl = new CommandListControlImpl(
				commandList, lines);
		widgetContainer.setCommandListControl(commandListControl);

		Label lblCp = new Label(shell, SWT.NONE);
		FormData fdLblCp = new FormData();
		fdLblCp.top = new FormAttachment(statusLabel, 6);
		fdLblCp.left = new FormAttachment(distanceValueLabel, 6);
		lblCp.setLayoutData(fdLblCp);
		lblCp.setText("CP:");
		lblCp.setToolTipText("Command Points (required to move)");

		commandPointsLabel = new Label(shell, SWT.NONE);
		FormData fdCommandPoints = new FormData();
		fdCommandPoints.top = new FormAttachment(statusLabel, 6);
		fdCommandPoints.left = new FormAttachment(lblCp, 6);
		commandPointsLabel.setLayoutData(fdCommandPoints);
		tabManager.setTabFolder(tabFolder);
		tabManager.setControllers(futureTabItemControl, mapCanvasControl);

		setButtonListeners();
		setResizeListener();
	}

	private void setResizeListener() {
		shell.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event e) {
				Point point = shell.getSize();
				account.setSize(point);
			}
		});
		shell.addListener(SWT.Move, new Listener() {
			@Override
			public void handleEvent(Event e) {
				Point point = shell.getLocation();
				account.setLocation(point);
			}
		});

	}

	private void setButtonListeners() {

		updateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (db.getSelectedGameId() == -1) {
					statusReporter
							.reportError("You must select a game before updating...");
				} else {
					actionFactory.updateAll(false);
				}
			}
		});

	}

	private void updateNation() {
		final Display display = Display.getDefault();

		if (display.isDisposed()) {
			return;
		}
		display.asyncExec(new Runnable() {
			public void run() {
				Nation nation = db.getNation();
				if (nation.isNewBattle()) {
					battleTab.setImage(imageLibrary.getFlag());
				} else {
					battleTab.setImage(null);
				}
				commandPointsLabel.setText("" + nation.getCommandPoints());
				if (nation.getCommandPoints() <= ClientConstants.COMMAND_POINT_WARN) {
					PlayerCommandPointWarner.warnUserLowCommandPoints(topShell,
							nation.getCommandPoints());
				}
			}

		});
	}

	public void enableUpdateButton() {
		final Display display = Display.getDefault();

		if (display.isDisposed()) {
			return;
		}
		display.asyncExec(new Runnable() {
			public void run() {
				if (updateButton.isDisposed()) {
					return;
				}
				updateButton.setEnabled(true);
				setCanvasCoordsVisibility(true);
			}

		});
	}

	public void setCanvasCoordsVisibility(boolean visible) {
		boolean visibleToSet = visible;
		if (!db.isLoggedIn()) {
			visibleToSet = false;
		}
		coordsLabel.setVisible(visibleToSet);
		distanceLabel.setVisible(visibleToSet);
		distanceValueLabel.setVisible(visibleToSet);
	}

	public void layout() {
		shell.layout();
	}

	public Label getLines() {
		return lines;
	}

	public void centreMap(SectorCoords coords) {
		mapCentre.setShifts(coords);
		mapImageManager.buildImage();
		mapCanvasControl.redraw();
	}

	public void scrollToSeeLocation(SectorCoords coords) {
		mapCanvasControl.scrollToSeeLocation(coords);
	}

	public void updateCoordsAndDistance(WorldView world,
			SectorCoords sectorCoords, SelectedCoords selectedCoords) {
		coordsLabel.setText(sectorCoords.toString());
		if (selectedCoords.getCoords() != null) {
			distanceValueLabel.setText(""
					+ SectorCoords.distance(world.size(), sectorCoords,
							selectedCoords.getCoords()));
		}
	}

	public void selectGame(Game game) {
		int gameId = game.getId();
		selectedUnits.clear();
		db.setSelectedGameId(gameId);
		handlerManager.fireEvent(new GameChangedEvent());
		enableUpdateButton();
		controllerManager.setTitle(shell);
		statusReporter.reportResult("Game " + gameId + " selected.");
		wavPlayer.playIntro();
		actionFactory.getVersion();
		actionFactory.setGame(gameId);
		actionFactory.updateAll(true);
		actionFactory.startupGame();
		windowDirector.openNewsWindow();
	}

	@Override
	public void centreAndScroll(SectorCoords coords) {
		centreMap(coords);
		scrollToSeeLocation(coords);
	}

	public void setCentreHomeEnabled(boolean enabled) {
		sectorTabItemControl.getUnitButtonsControl().setCentreHomeEnabled(
				enabled);
		unitTabItemControl.getUnitButtonsControl()
				.setCentreHomeEnabled(enabled);
	}

}
