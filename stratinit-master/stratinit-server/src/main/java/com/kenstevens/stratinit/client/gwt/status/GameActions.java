package com.kenstevens.stratinit.client.gwt.status;

import java.util.ArrayList;
import java.util.List;

public class GameActions {
	private final List<GameAction> gameActionList = new ArrayList<GameAction>();

	public void add(GameAction gameAction) {
		gameActionList.add(gameAction);
	}

	public void refreshData() {
		for (GameAction gameAction : gameActionList ) {
			gameAction.refreshData();
		}
	}
}
