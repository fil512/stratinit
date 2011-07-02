package com.kenstevens.stratinit.client.gwt.service;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.kenstevens.stratinit.client.gwt.event.GWTSelectionSource;
import com.kenstevens.stratinit.client.gwt.model.GWTSectorCoords;
import com.kenstevens.stratinit.client.gwt.model.GWTUnit;
import com.kenstevens.stratinit.client.gwt.model.GWTUpdate;
import com.kenstevens.stratinit.client.gwt.model.StatusReporter;

public final class MoveUnitsAsyncCallback implements
		AsyncCallback<GWTResult<GWTUpdate>> {
	private final GWTSectorCoords coords;
	private final List<GWTUnit> selectedUnits;

	public MoveUnitsAsyncCallback(GWTSectorCoords coords,
			List<GWTUnit> selectedUnits) {
		this.coords = coords;
		this.selectedUnits = selectedUnits;
	}

	public void onFailure(Throwable e) {
		StatusReporter.addText("Failed to move units to " + coords + ": "
				+ e.getMessage());
	}

	public void onSuccess(GWTResult<GWTUpdate> result) {
		if (result.isSuccess()) {
			StatusReporter.addText("Units moved to " + coords + ".");
			GWTDataManager.update(result.getValue());
			List<Integer> toSelect = new ArrayList<Integer>();
			for (GWTUnit unit : selectedUnits) {
				GWTUnit selectedUnit = GWTDataManager.getUnit(unit.id);
				if (selectedUnit != null && selectedUnit.mobility > 0) {
					toSelect.add(selectedUnit.id);
				}
			}
			if (!toSelect.isEmpty()) {
				GWTDataManager
						.setSelectedCoords(coords, GWTSelectionSource.MAP);
				GWTDataManager.setSelectedUnits(toSelect,
						GWTSelectionSource.MAP);
			}
		} else {
			StatusReporter.addText("Unable to move units to " + coords + ": "
					+ result.getLastMessage());
		}
	}
}
