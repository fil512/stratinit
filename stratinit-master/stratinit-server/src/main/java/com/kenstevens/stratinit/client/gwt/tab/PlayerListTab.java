package com.kenstevens.stratinit.client.gwt.tab;

import com.kenstevens.stratinit.client.gwt.status.StatusSetter;
import com.kenstevens.stratinit.client.gwt.widget.PlayerGridPanel;
import com.smartgwt.client.widgets.Canvas;

public class PlayerListTab extends Canvas {
	public PlayerListTab(String title, StatusSetter statusSetter) {
		this.setWidth(470);
		this.setHeight(400);
		PlayerGridPanel playerGrid = new PlayerGridPanel(statusSetter, title);
		this.addChild(playerGrid);
	}
}
