package com.kenstevens.stratinit.client.gwt.widget;

import com.kenstevens.stratinit.client.gwt.datasource.PlayerDataSource;
import com.kenstevens.stratinit.client.gwt.status.StatusSetter;
import com.smartgwt.client.widgets.grid.ListGrid;

public class PlayerGridPanel extends ListGrid {

	public PlayerGridPanel(StatusSetter statusSetter, final String title) {
		setWidth(730);
		setHeight(400);
		setTitle(title);
		PlayerDataSource playerDataSource = new PlayerDataSource(statusSetter);
		setDataSource(playerDataSource);
		setAutoFetchData(true);
		setCanDragSelectText(true);
	}
}
