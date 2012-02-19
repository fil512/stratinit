package com.kenstevens.stratinit.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.kenstevens.stratinit.client.gwt.callback.AutoErrorHandlingAsyncCallback;
import com.kenstevens.stratinit.client.gwt.model.Authorization;
import com.kenstevens.stratinit.client.gwt.model.MainStatusReporter;
import com.kenstevens.stratinit.client.gwt.service.GWTAuthorizationService;
import com.kenstevens.stratinit.client.gwt.service.GWTAuthorizationServiceAsync;
import com.kenstevens.stratinit.client.gwt.service.GWTGameService;
import com.kenstevens.stratinit.client.gwt.service.GWTGameServiceAsync;
import com.kenstevens.stratinit.client.gwt.service.GWTNone;
import com.kenstevens.stratinit.client.gwt.service.GWTResult;
import com.kenstevens.stratinit.client.gwt.status.GameActions;
import com.kenstevens.stratinit.client.gwt.status.StatusSetter;
import com.kenstevens.stratinit.client.gwt.tab.AccountSettingsTab;
import com.kenstevens.stratinit.client.gwt.tab.BuildAuditTab;
import com.kenstevens.stratinit.client.gwt.tab.GWTGameManager;
import com.kenstevens.stratinit.client.gwt.tab.GameList;
import com.kenstevens.stratinit.client.gwt.tab.PlayerListTab;
import com.kenstevens.stratinit.client.gwt.tab.PostAnnouncementTab;
import com.kenstevens.stratinit.client.gwt.widget.ExceptionWindow;
import com.kenstevens.stratinit.client.gwt.widget.GameCanvas;
import com.kenstevens.stratinit.client.gwt.widget.LoginWindowImpl;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Console implements EntryPoint {
	protected static final String PLAY_URL = "http://www.strategicinitiative.org/client/stratinit-client-1.1.jnlp";

	private TabSet topTabSet;
	private StatusSetter statusSetter;
	private GWTAuthorizationServiceAsync authorizationServiceAsync = GWT
			.create(GWTAuthorizationService.class);
	private GWTGameServiceAsync gameServiceAsync = GWT
			.create(GWTGameService.class);

	private Tab joinedGameListTab;

	private Tab gameManagerTab;

	private HStack buttonStack;
	private final GameActions gameActions = new GameActions();

	private Label statusLabel;

	private IButton logoutButton;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		statusLabel = new Label();

		MainStatusReporter.setStatusSetter(statusSetter);

		VLayout main = new VLayout(10);
		buttonStack = new HStack();

		logoutButton = new IButton("logout");

		buttonStack.addMember(logoutButton);
		main.addMember(buttonStack);
		statusLabel.setWidth(800);
		statusLabel.setHeight(20);
		main.addMember(statusLabel);
		Img img = new Img("../../images/Splash.jpg", 495, 194);
		main.addMember(img);

		topTabSet = new TabSet();
		topTabSet.setWidth(800);
		topTabSet.setHeight(500);
		addTabItem("Players", new PlayerListTab("Players", statusSetter));
		checkAuthorization();
		main.addMember(topTabSet);
		main.draw();

		addHandlers();
	}

	private void addHandlers() {
		statusSetter = new StatusSetter() {
			public void setText(String text) {
				statusLabel.setContents(text);
			}

			@Override
			public void addText(String message) {
				statusLabel.setContents(statusLabel.getContents() + "\n"
						+ message);
			}
		};

		logoutButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent e) {
				logout();
			}

		});

		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
			public void onUncaughtException(Throwable e) {
				new ExceptionWindow().open(e);
			}
		});
	}

	protected void logout() {
		AsyncCallback<GWTNone> callback = new AutoErrorHandlingAsyncCallback<GWTNone>(
				new LoginWindowImpl()) {

			public void onSuccess(GWTNone result) {
				Window.Location.reload();
			}

		};
		authorizationServiceAsync.logout(callback);
	}

	private void shutdown() {
		AsyncCallback<GWTResult<GWTNone>> callback = new AutoErrorHandlingAsyncCallback<GWTResult<GWTNone>>(
				new LoginWindowImpl()) {
			public void onSuccess(GWTResult<GWTNone> result) {
				statusSetter.setText(result.getLastMessage());
			}
		};

		gameServiceAsync.shutdown(callback);

	}

	private void checkAuthorization() {

		AsyncCallback<Authorization> callback = new AutoErrorHandlingAsyncCallback<Authorization>(
				new LoginWindowImpl()) {

			public void onSuccess(Authorization result) {
				if (result.isAdmin()) {
					statusSetter.setText("Logged in as Admin "
							+ result.getUsername());
					addAdminFunctions();
					topTabSet.selectTab(gameManagerTab);
				} else {
					statusSetter
							.setText("Logged in as " + result.getUsername());
					addUserFunctions();
					topTabSet.selectTab(joinedGameListTab);
				}
			}

		};

		authorizationServiceAsync.getAuthorization(callback);
	}

	private void addAdminFunctions() {
		gameManagerTab = addGameListItem("Manage Games", new GWTGameManager(gameActions,
				statusSetter));
		addTabItem("Build Audits", new BuildAuditTab("Build Audit",
				statusSetter));
		addTabItem("Post", new PostAnnouncementTab(statusSetter));
		IButton shutdownButton = new IButton("shutdown");
		shutdownButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent e) {
				statusSetter.setText("Shutting down...");
				shutdown();
			}
		});
		buttonStack.addMember(shutdownButton);

	}

	private void addUserFunctions() {
		GameList joinedGameList = new GameList("My Games", gameActions,
				statusSetter, true);
		joinedGameListTab = addGameListItem("My Games", joinedGameList);
		GameList unjoinedGameList = new GameList("Join Game", gameActions,
				statusSetter, false);
		addGameListItem("Join Game", unjoinedGameList);
		addTabItem("Account Settings", new AccountSettingsTab(statusSetter));

		IButton playButton = new IButton("play");
		playButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent e) {
				Window.open(PLAY_URL, "_self", "");
			}
		});
		buttonStack.addMember(playButton);
	}

	private Tab addGameListItem(final String title, final GameCanvas gameCanvas) {
		gameActions.add(gameCanvas);
		return addTabItem(title, gameCanvas);
	}

	private Tab addTabItem(final String title, final Canvas gameCanvas) {
		final Tab tab = new Tab(title);
		tab.setPane(gameCanvas);
		topTabSet.addTab(tab);
		return tab;
	}

}
