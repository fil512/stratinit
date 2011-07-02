package com.kenstevens.stratinit.client.gwt.tab;

import com.google.gwt.core.client.GWT;
import com.kenstevens.stratinit.client.gwt.controller.GameTableController;
import com.kenstevens.stratinit.client.gwt.service.GWTGameService;
import com.kenstevens.stratinit.client.gwt.service.GWTGameServiceAsync;
import com.kenstevens.stratinit.client.gwt.status.GameActions;
import com.kenstevens.stratinit.client.gwt.status.StatusSetter;
import com.kenstevens.stratinit.client.gwt.widget.GameCanvas;
import com.kenstevens.stratinit.client.gwt.widget.GameGridPanel;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VStack;

public class GameList extends GameCanvas {
	private GWTGameServiceAsync gameServiceAsync = GWT
			.create(GWTGameService.class);
	private GameGridPanel gameGrid;
	private GameTableController gameTableController;
	private final boolean joinedGames;
	private final StatusSetter statusSetter;

	public GameList(String title, GameActions gameActions,
			StatusSetter statusSetter, boolean joinedGames) {
		this.statusSetter = statusSetter;
		this.joinedGames = joinedGames;

		VStack vStack = new VStack();
		this.setWidth(470);
		this.setHeight(400);
		gameGrid = new GameGridPanel(statusSetter, title, this, joinedGames);
		vStack.addMember(gameGrid);
		if (joinedGames) {
			IButton enterButton = new IButton("enter");
			enterButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent e) {
					int gameId = gameGrid.getSelectedGameId();
					if (gameId != -1) {
						enterGame(gameId);
					}
				}
			});
			vStack.addMember(enterButton);
		} else {
			IButton joinButton = new IButton("join");
			joinButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent e) {
					int gameId = gameGrid.getSelectedGameId();
					if (gameId != -1) {
						joinGame(gameId);
					}
				}
			});
			vStack.addMember(joinButton);
		}
		gameTableController = new GameTableController(gameServiceAsync,
				gameActions, statusSetter);
		this.addChild(vStack);
	}

	protected void enterGame(int gameId) {
		int gameSize = gameGrid.getSelectedGameSize();
		if (gameSize == 0) {
			statusSetter.setText("Game "+gameGrid.getSelectedGameId()+" is not open yet.");
			return;
		}
		GameOpener gameOpener = new GameOpener(gameSize);
		gameOpener.enterGame(gameId);
	}

	public void actOnGame(int gameId) {
		if (joinedGames) {
			enterGame(gameId);
		} else {
			joinGame(gameId);
		}
	}

	private void joinGame(int gameId) {
		gameTableController.asyncJoinGame(gameId);
	}


	@Override
	public void refreshData() {
		gameGrid.refreshData();
	}
}
