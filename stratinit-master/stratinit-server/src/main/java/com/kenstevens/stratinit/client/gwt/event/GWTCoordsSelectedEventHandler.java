package com.kenstevens.stratinit.client.gwt.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.kenstevens.stratinit.client.gwt.model.GWTSectorCoords;

public abstract class GWTCoordsSelectedEventHandler implements EventHandler {
	public static final Type<GWTCoordsSelectedEventHandler> TYPE = new Type<GWTCoordsSelectedEventHandler>();

	public abstract void coordsSelected(GWTSectorCoords selectedCoords, GWTSelectionSource source);
}
