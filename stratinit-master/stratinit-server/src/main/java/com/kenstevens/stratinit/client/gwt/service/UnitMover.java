package com.kenstevens.stratinit.client.gwt.service;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.kenstevens.stratinit.client.gwt.model.GWTSectorCoords;
import com.kenstevens.stratinit.client.gwt.model.GWTUnit;
import com.kenstevens.stratinit.client.gwt.model.GWTUpdate;
import com.kenstevens.stratinit.client.gwt.model.StatusReporter;

public class UnitMover {



	private static GWTGameServiceAsync gameServiceAsync = GWT.create(GWTGameService.class);


	public void moveUnits(final List<GWTUnit> selectedUnits, final GWTSectorCoords coords) {
		AsyncCallback<GWTResult<GWTUpdate>> callback = new MoveUnitsAsyncCallback(coords, selectedUnits);
		StatusReporter.addText("Moving selected units to "
				+ coords);

		gameServiceAsync.moveUnits(selectedUnits, coords, callback);
	}

}
