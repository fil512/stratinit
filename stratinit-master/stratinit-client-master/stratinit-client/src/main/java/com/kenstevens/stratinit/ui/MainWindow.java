package com.kenstevens.stratinit.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
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

import com.google.common.eventbus.Subscribe;
import com.kenstevens.stratinit.audio.WavPlayer;
import com.kenstevens.stratinit.control.CommandListControlImpl;
import com.kenstevens.stratinit.control.TextControl;
import com.kenstevens.stratinit.control.selection.MapCentre;
import com.kenstevens.stratinit.event.CommandPointsArrivedEvent;
import com.kenstevens.stratinit.event.GameChangedEvent;
import com.kenstevens.stratinit.event.NationListArrivedEvent;
import com.kenstevens.stratinit.event.StratinitEventBus;
import com.kenstevens.stratinit.main.ClientConstants;
import com.kenstevens.stratinit.model.Account;
import com.kenstevens.stratinit.model.Data;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.SelectedCoords;
import com.kenstevens.stratinit.model.SelectedUnits;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.shell.CommandListControl;
import com.kenstevens.stratinit.shell.GameManager;
import com.kenstevens.stratinit.shell.MapControl;
import com.kenstevens.stratinit.shell.Message;
import com.kenstevens.stratinit.shell.StatusReportEvent;
import com.kenstevens.stratinit.shell.StatusReporter;
import com.kenstevens.stratinit.shell.TopShell;
import com.kenstevens.stratinit.shell.WidgetContainer;
import com.kenstevens.stratinit.site.ProgressBarControlImpl;
import com.kenstevens.stratinit.site.action.ActionFactory;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.ui.image.ImageLibrary;
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
import com.kenstevens.stratinit.ui.window.WindowDirector;
import com.kenstevens.stratinit.ui.window.map.MapCanvasControl;
import com.kenstevens.stratinit.ui.window.map.MapImageManager;
import com.kenstevens.stratinit.util.Spring;

@Component
public class MainWindow implements MapControl, GameManager {

	private Label distanceValueLabel;
	private Label coordsLabel;
	private Button updateButton;

	private MapCanvasControl mapCanvasControl;
	private FutureTabItemControl futureTabItemControl;
	private TextControl statusControl;

	@Autowired
	private MapImageManager mapImageManager;

	@Autowired
	private Data db;

	@Autowired
	private StatusReporter statusReporter;
	@Autowired
	private Spring spring;
	@Autowired
	private MapCentre mapCentre;
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
	private StratinitEventBus eventBus;
	@Autowired
	private SplashWindow splashWindow;
	@Autowired
	private TopShell topShell;
	@Autowired
	private ImageLibrary imageLibrary;
	@Autowired
	private ActionFactory actionFactory;

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
	public void open(Shell shell) {
		setWidgetContainerValues();
		buildInterface(shell);

	}

	private void buildInterface(Shell shell) {
		splashWindow.pushProgressBar();
		createContents(shell);
		controllerManager.setControllers();
		splashWindow.pushProgressBar();
		addHandlers();
		splashWindow.pushProgressBar();
		supplyTabItemControl.setContents();
		splashWindow.pushProgressBar();
		controllerManager.setTitle();
		mapImageManager.buildImage();
		splashWindow.pushProgressBar();
	}

	private void setWidgetContainerValues() {
		// TODO REF these should be three separate widgets
		widgetContainer.setMapControl(this);
		widgetContainer.setTabControl(tabManager);
		widgetContainer.setGameManager(this);
	}

	@Subscribe
	public void handleNationListArrivedEvent(NationListArrivedEvent event) {
		updateNation();
	}

	@Subscribe
	public void handleCommandPointsArrivedEvent(CommandPointsArrivedEvent event) {
		updateNation();
	}

	@Subscribe
	public void handleStatusReportEvent(StatusReportEvent event) {
		Message message = event.getMessage();
		if (message == null) {
			statusControl.setMessage("");
			return;
		} else if (message.isError()) {
			statusControl.setError(message.getText());
		} else {
			statusControl.setMessage(message.getText());
		}
	}

	private void addHandlers() {
		eventBus.register(this);
	}

	/**
	 * Create contents of the window
	 * 
	 * @wbp.parser.entryPoint
	 */
	protected void createContents(Shell shell) {
		splashWindow.pushProgressBar();

		createMainMenu(shell);
		splashWindow.pushProgressBar();

		TabFolder tabFolder = createTabFolder(shell);

		Canvas canvas = createCanvas(shell, tabFolder);

		splashWindow.pushProgressBar();

		createSectorTab(tabFolder);
		splashWindow.pushProgressBar();

		createUnitTab(tabFolder);
		splashWindow.pushProgressBar();

		createCityTab(tabFolder);
		splashWindow.pushProgressBar();

		createBattleTab(tabFolder);
		splashWindow.pushProgressBar();

		createPlayerTab(tabFolder);
		splashWindow.pushProgressBar();

		createHistoryTab(tabFolder);
		splashWindow.pushProgressBar();

		createFutureTab(tabFolder);
		splashWindow.pushProgressBar();

		createSupplyTab(tabFolder);
		splashWindow.pushProgressBar();

		createBottomControls(shell, tabFolder, canvas);
		splashWindow.pushProgressBar();

		setButtonListeners();
		setResizeListener(shell);
	}

	private void createBottomControls(Shell shell, TabFolder tabFolder,
			Canvas canvas) {
		org.eclipse.swt.widgets.List commandList = createCommandList(shell,
				tabFolder, canvas);

		final ProgressBar progressBar = new ProgressBar(shell, SWT.SMOOTH);
		final FormData fdProgressBar = new FormData();
		fdProgressBar.top = new FormAttachment(commandList, 6);
		fdProgressBar.left = new FormAttachment(canvas, 5);
		fdProgressBar.right = new FormAttachment(tabFolder, 0, SWT.RIGHT);
		progressBar.setLayoutData(fdProgressBar);
		ProgressBarControlImpl progressBarControl = new ProgressBarControlImpl(
				progressBar);
		widgetContainer.setProgressBarControl(progressBarControl);

		updateButton = new Button(shell, SWT.NONE);
		updateButton.setToolTipText("Update all data");
		final FormData fdUpdateButton = new FormData();
		fdUpdateButton.right = new FormAttachment(tabFolder, -1, SWT.RIGHT);
		fdUpdateButton.bottom = new FormAttachment(100, -14);
		fdUpdateButton.top = new FormAttachment(100, -41);
		fdUpdateButton.left = new FormAttachment(100, -80);
		updateButton.setLayoutData(fdUpdateButton);
		updateButton.setEnabled(false);
		updateButton.setImage(imageLibrary.getUpdate());

		Text statusLabel;
		statusLabel = new Text(shell, SWT.V_SCROLL | SWT.H_SCROLL);
		fdProgressBar.bottom = new FormAttachment(statusLabel, -6);
		final FormData fdStatusLabel = new FormData();
		fdStatusLabel.right = new FormAttachment(tabFolder, 0, SWT.RIGHT);
		fdStatusLabel.left = new FormAttachment(canvas, 6);
		fdStatusLabel.top = new FormAttachment(updateButton, -80, SWT.TOP);
		fdStatusLabel.bottom = new FormAttachment(updateButton, -6);
		statusLabel.setLayoutData(fdStatusLabel);
		statusControl = new TextControl(statusLabel);

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
	}

	private org.eclipse.swt.widgets.List createCommandList(Shell shell,
			TabFolder tabFolder, Canvas canvas) {
		org.eclipse.swt.widgets.List commandList;
		commandList = new org.eclipse.swt.widgets.List(shell, SWT.READ_ONLY
				| SWT.SIMPLE | SWT.V_SCROLL);

		commandList.removeAll();
		final FormData fdCommandList = new FormData();
		fdCommandList.top = new FormAttachment(tabFolder, 5);
		fdCommandList.left = new FormAttachment(canvas, 5);
		fdCommandList.right = new FormAttachment(100, -49);
		fdCommandList.bottom = new FormAttachment(100, -145);
		commandList.setLayoutData(fdCommandList);
		return commandList;
	}

	private void createSupplyTab(TabFolder tabFolder) {
		final TabItem mTabItem = new TabItem(tabFolder, SWT.NONE);
		tabManager.setSupplyTabIndex(tabManager.register(
				ClientConstants.SUPPLY_TAB_ITEM_TITLE, mTabItem));

		SupplyTabItem supplyTabItem = new SupplyTabItem(tabFolder, SWT.NONE);
		mTabItem.setControl(supplyTabItem);
		supplyTabItemControl = spring.autowire(new SupplyTabItemControl(
				supplyTabItem));
		controllerManager.add(supplyTabItemControl);
	}

	private void createFutureTab(TabFolder tabFolder) {
		final TabItem fTabItem = new TabItem(tabFolder, SWT.NONE);
		tabManager.setFutureTabIndex(tabManager.register(
				ClientConstants.FUTURE_TITLE, fTabItem));

		FutureTabItem futureTabItem = new FutureTabItem(tabFolder, SWT.NONE);
		fTabItem.setControl(futureTabItem);
		futureTabItemControl = spring.autowire(new FutureTabItemControl(
				futureTabItem));
		controllerManager.add(futureTabItemControl);
	}

	private void createHistoryTab(TabFolder tabFolder) {
		final TabItem hTabItem = new TabItem(tabFolder, SWT.NONE);
		tabManager.register(ClientConstants.HISTORY_TITLE, hTabItem);

		HistoryTabItem historyTabItem = new HistoryTabItem(tabFolder, SWT.NONE);
		hTabItem.setControl(historyTabItem);
		HistoryTabItemControl historyTabItemControl = spring
				.autowire(new HistoryTabItemControl(historyTabItem));
		controllerManager.add(historyTabItemControl);
	}

	private void createPlayerTab(TabFolder tabFolder) {
		final TabItem pTabItem = new TabItem(tabFolder, SWT.NONE);
		tabManager.setPlayerTabIndex(tabManager.register(
				ClientConstants.PLAYER_TAB_ITEM_TITLE, pTabItem));

		PlayerTabItem playerTabItem = new PlayerTabItem(tabFolder, SWT.NONE);
		playerTabItem.getRefreshButton().setImage(imageLibrary.getUpdate());
		pTabItem.setControl(playerTabItem);
		PlayerTabItemControl playerTabItemControl = spring
				.autowire(new PlayerTabItemControl(playerTabItem));
		controllerManager.add(playerTabItemControl);
	}

	private void createBattleTab(TabFolder tabFolder) {
		battleTab = new TabItem(tabFolder, SWT.NONE);
		tabManager.setBattleTabIndex(tabManager.register(
				ClientConstants.BATTLE_TAB_ITEM_TITLE, battleTab));

		BattleTabItem battleTabItem = new BattleTabItem(tabFolder, SWT.NONE);
		battleTab.setControl(battleTabItem);
		BattleTabItemControl battleTabItemControl = spring
				.autowire(new BattleTabItemControl(battleTabItem));
		controllerManager.add(battleTabItemControl);
	}

	private void createCityTab(TabFolder tabFolder) {
		final TabItem cTabItem = new TabItem(tabFolder, SWT.NONE);
		tabManager.setCityTabIndex(tabManager.register(
				ClientConstants.CITY_TITLE, cTabItem));

		CityTabItem cityTabItem = new CityTabItem(tabFolder, SWT.NONE);
		cTabItem.setControl(cityTabItem);
		CityTabItemControl cityTabItemControl = spring
				.autowire(new CityTabItemControl(cityTabItem));
		controllerManager.add(cityTabItemControl);
	}

	private void createUnitTab(TabFolder tabFolder) {
		final TabItem uTabItem = new TabItem(tabFolder, SWT.NONE);
		tabManager.setUnitTabIndex(tabManager.register(
				ClientConstants.UNIT_TAB_TITLE, uTabItem));
		UnitTabItem unitTabItem = new UnitTabItem(tabFolder, SWT.NONE);
		uTabItem.setControl(unitTabItem);
		unitTabItemControl = spring
				.autowire(new UnitTabItemControl(unitTabItem));
		controllerManager.add(unitTabItemControl);
	}

	private void createSectorTab(TabFolder tabFolder) {
		final TabItem sTabItem = new TabItem(tabFolder, SWT.NONE);
		tabManager.register(ClientConstants.SECTOR_TAB_ITEM_TITLE, sTabItem);
		sectorTabItem = new SectorTabItem(tabFolder, SWT.NONE);
		sTabItem.setControl(sectorTabItem);
		sectorTabItemControl = spring.autowire(new SectorTabItemControl(
				sectorTabItem));
		controllerManager.add(sectorTabItemControl);
	}

	private Canvas createCanvas(Shell shell, TabFolder tabFolder) {
		Canvas canvas = new Canvas(shell, SWT.NO_BACKGROUND
				| SWT.NO_REDRAW_RESIZE | SWT.V_SCROLL | SWT.H_SCROLL);

		final FormData fdCanvas = new FormData();
		fdCanvas.right = new FormAttachment(tabFolder, -5, SWT.LEFT);
		fdCanvas.bottom = new FormAttachment(100, -5);
		fdCanvas.top = new FormAttachment(0, 5);
		fdCanvas.left = new FormAttachment(0, 5);
		canvas.setLayoutData(fdCanvas);

		mapCanvasControl = spring.autowire(new MapCanvasControl(canvas));
		controllerManager.add(mapCanvasControl);

		return canvas;
	}

	private TabFolder createTabFolder(Shell shell) {
		TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		final FormData fdTabFolder = new FormData();
		fdTabFolder.top = new FormAttachment(0, 5);
		fdTabFolder.left = new FormAttachment(100, -421);
		fdTabFolder.right = new FormAttachment(100, -5);
		fdTabFolder.bottom = new FormAttachment(100, -205);
		tabFolder.setLayoutData(fdTabFolder);
		return tabFolder;
	}

	private void createMainMenu(Shell shell) {
		MainMenu mainMenu = new MainMenu(shell, SWT.NONE);
		mainMenu.setLayoutData(new FormData());
		spring.autowire(new MainMenuControl(mainMenu));
	}

	private void setResizeListener(final Shell shell) {
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

	// TODO REF need this?
	public void layout() {
		topShell.getShell().layout();
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

	public void selectGame(Game game, boolean noAlliances) {
		int gameId = game.getId();
		selectedUnits.clear();
		db.setSelectedGameId(gameId);
		eventBus.post(new GameChangedEvent());
		enableUpdateButton();
		controllerManager.setTitle();
		statusReporter.reportResult("Game " + gameId + " selected.");
		wavPlayer.playIntro();
		actionFactory.getVersion();
		actionFactory.setGame(gameId, noAlliances);
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
