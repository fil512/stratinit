package com.kenstevens.stratinit.client.gwt.widget;

import com.kenstevens.stratinit.client.gwt.datasource.JoinedGameDataSource;
import com.kenstevens.stratinit.client.gwt.datasource.UnjoinedGameDataSource;
import com.kenstevens.stratinit.client.gwt.model.GameListGridRecord;
import com.kenstevens.stratinit.client.gwt.status.GameAction;
import com.kenstevens.stratinit.client.gwt.status.StatusSetter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordDoubleClickHandler;

public class GameGridPanel extends ListGrid {
	private final GameAction gameAction;

	public GameGridPanel(StatusSetter statusSetter, final String title, GameAction gameAction, boolean joinedGames) {
		this.gameAction = gameAction;

		addRecordDoubleClickHandler(new RecordDoubleClickHandler() {
			@Override
			public void onRecordDoubleClick(RecordDoubleClickEvent event) {
				GameGridPanel.this.gameAction.actOnGame(getSelectedGameId());
			}
		});

        setWidth(430);
        setHeight(350);
        this.setTitle(title);
        if (joinedGames) {
        	setDataSource(new JoinedGameDataSource(statusSetter));
        } else {
        	setDataSource(new UnjoinedGameDataSource(statusSetter));
        }
		setAutoFetchData(true);

	}

	public int getSelectedGameId() {
		return getSelection()[0].getAttributeAsInt("id");
	}

	public void refreshData() {
		setData(new GameListGridRecord[0]);
		super.fetchData();
	}

	public int getSelectedGameSize() {
		return getSelection()[0].getAttributeAsInt("size");
	}
}
