package com.kenstevens.stratinit.client.gwt.event;

import com.google.gwt.event.shared.GwtEvent;
import com.kenstevens.stratinit.client.gwt.model.GWTSectorCoords;

public class GWTCoordsSelectedEvent extends GwtEvent<GWTCoordsSelectedEventHandler> {

	private final GWTSectorCoords selectedCoords;
	private final GWTSelectionSource source;

	public GWTCoordsSelectedEvent(GWTSectorCoords selectedCoords, GWTSelectionSource source) {
		this.selectedCoords = selectedCoords;
		this.source = source;
	}

	@Override
	protected void dispatch(GWTCoordsSelectedEventHandler handler) {
		handler.coordsSelected(selectedCoords, source);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<GWTCoordsSelectedEventHandler> getAssociatedType() {
		return GWTCoordsSelectedEventHandler.TYPE;
	}
}
