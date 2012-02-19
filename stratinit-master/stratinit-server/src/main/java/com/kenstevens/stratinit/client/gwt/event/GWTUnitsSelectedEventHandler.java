package com.kenstevens.stratinit.client.gwt.event;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.kenstevens.stratinit.client.gwt.model.GWTUnit;

public abstract class GWTUnitsSelectedEventHandler implements EventHandler {
	public static final Type<GWTUnitsSelectedEventHandler> TYPE = new Type<GWTUnitsSelectedEventHandler>();

	public abstract void unitsSelected(List<GWTUnit> selectedUnits, GWTSelectionSource source);
}
