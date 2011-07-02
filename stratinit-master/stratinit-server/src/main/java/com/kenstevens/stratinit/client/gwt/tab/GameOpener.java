package com.kenstevens.stratinit.client.gwt.tab;

import java.util.Map;

import com.kenstevens.stratinit.client.gwt.event.GWTSelectionSource;
import com.kenstevens.stratinit.client.gwt.model.GWTUnit;
import com.kenstevens.stratinit.client.gwt.model.GWTUpdate;
import com.kenstevens.stratinit.client.gwt.service.GWTDataManager;

public class GameOpener {

	private final int size;

	public GameOpener(int size) {
		this.size = size;
	}

	public void enterGame(int gameId) {
		GWTDataManager.init(size);
		WorldDisplayer worldDisplayer = new WorldDisplayer(gameId, size);
		worldDisplayer.show();
		DataUpdater dataUpdater = new DataUpdater();
		dataUpdater.asyncEnterGame(gameId, worldDisplayer);
	}

	public void init(GWTUpdate result) {
		GWTDataManager.init(size);
		GWTDataManager.update(result);
		selectFirstUnit();
	}

	private void selectFirstUnit() {
		Map<Integer, GWTUnit> unitMap = GWTDataManager.getUnitMap();
		if (unitMap.isEmpty()) {
			return;
		}
		GWTUnit unit = unitMap.values().iterator().next();
		GWTDataManager.setSelectedUnit(unit.id, GWTSelectionSource.UNIT_TAB);

		// TODO Auto-generated method stub

	}

}
