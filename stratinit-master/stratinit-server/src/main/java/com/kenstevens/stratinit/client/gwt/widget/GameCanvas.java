package com.kenstevens.stratinit.client.gwt.widget;

import com.kenstevens.stratinit.client.gwt.status.GameAction;
import com.smartgwt.client.widgets.Canvas;

public abstract class GameCanvas extends Canvas implements GameAction {
	public abstract void actOnGame(int gameId);
	public abstract void refreshData();
}
