package com.kenstevens.stratinit.client.gwt.tab;

import com.google.gwt.core.client.GWT;
import com.kenstevens.stratinit.client.gwt.controller.GameController;
import com.kenstevens.stratinit.client.gwt.controller.GameTableController;
import com.kenstevens.stratinit.client.gwt.service.GWTGameService;
import com.kenstevens.stratinit.client.gwt.service.GWTGameServiceAsync;
import com.kenstevens.stratinit.client.gwt.status.GameActions;
import com.kenstevens.stratinit.client.gwt.status.StatusSetter;
import com.kenstevens.stratinit.client.gwt.widget.GameCanvas;
import com.kenstevens.stratinit.client.gwt.widget.GameGridPanel;
import com.kenstevens.stratinit.client.gwt.widget.GameWindow;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.VStack;

public class GWTGameManager extends GameCanvas {
	private GWTGameServiceAsync gameServiceAsync = GWT
			.create(GWTGameService.class);
	private TextItem nameField;
	private TextItem islandsField;
	private GameGridPanel gameGrid;
	private GameTableController gameTableController;
	private CheckboxItem blitzCheckBox;
	private final StatusSetter statusSetter;

	public GWTGameManager(GameActions gameActions, StatusSetter statusSetter) {
		this.statusSetter = statusSetter;

		VStack columnOne = new VStack();
		gameGrid = new GameGridPanel(statusSetter, "Games", this, false);

		IButton viewButton = new IButton("view");
		viewButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent e) {
				int gameId = gameGrid.getSelectedGameId();
				if (gameId != -1) {
					actOnGame(gameId);
				}
			}
		});
		
		IButton endButton = new IButton("end");
		endButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent e) {
				int gameId = gameGrid.getSelectedGameId();
				if (gameId != -1) {
					endGame(gameId);
				}
			}
		});
		
		IButton updateGamesButton = new IButton("Refresh");
		updateGamesButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent e) {
				gameGrid.refreshData();
			}
		});

		HStack buttonPanel = new HStack();
		buttonPanel.addMember(updateGamesButton);
		buttonPanel.addMember(viewButton);
		buttonPanel.addMember(endButton);
		columnOne.addMember(gameGrid);
		columnOne.addMember(buttonPanel);

		nameField = new TextItem("Name", "name");
		nameField.setWidth(120);
		islandsField = new TextItem("Islands", "islands");
		blitzCheckBox = new CheckboxItem("Blitz?");

		DynamicForm form = new DynamicForm();
		form.setFields(nameField, islandsField, blitzCheckBox);

		IButton addGameButton = new IButton("Create Game");
		addGameButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent e) {
				addGame();
			}
		});

		VStack columnTwo = new VStack();
		columnTwo.addMember(form);
		columnTwo.addMember(addGameButton);

		HLayout hLayout = new HLayout();
		hLayout.setAutoHeight();
		hLayout.addMember(columnOne);
		hLayout.addMember(columnTwo);
		this.addChild(hLayout);

		gameTableController = new GameTableController(gameServiceAsync,
				gameActions, statusSetter);
	}

	public void actOnGame(int gameId) {
		GameWindow gameWindow = new GameWindow(gameId);
		GameController gameController = new GameController(gameServiceAsync,
				statusSetter);
		gameController.asyncGetGameMap(gameWindow, gameId);
	}

	public void endGame(int gameId) {
		GameController gameController = new GameController(gameServiceAsync,
				statusSetter);
		gameController.asyncEndGame(gameId);
	}

	private void addGame() {
		String nameText = nameField.getValue().toString().trim();
		// TODO TEST why empty name no work
		// SC.say("Name text: ["+nameText+"]");
		// Stock code must be between 1 and 10 chars that are numbers, letters,
		// or dots.
		if (nameText.isEmpty()) {
			SC.say("'" + nameText + "' is not a valid symbol.");
			nameField.selectValue();
			return;
		}
		boolean blitz;
		try {
			blitz = blitzCheckBox.getValueAsBoolean();
		} catch (NumberFormatException e) {
			SC.say("size and islands must both be numbers.");
			return;
		}
		int islands = -1;
		if (blitz) {
			String islandsString = islandsField.getValue().toString().trim();
			try {
				islands = Integer.valueOf(islandsString);
			} catch (NumberFormatException e) {
				SC.say("islands must be a number.");
				return;
			}
		}
		gameTableController.asyncAddGame(this, nameText, islands, blitz);

		nameField.clearValue();
	}

	@Override
	public void refreshData() {
		gameGrid.refreshData();
	}
}
