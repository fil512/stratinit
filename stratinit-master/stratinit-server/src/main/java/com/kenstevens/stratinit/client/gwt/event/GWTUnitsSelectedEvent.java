package com.kenstevens.stratinit.client.gwt.event;

import java.util.List;

import com.google.gwt.event.shared.GwtEvent;
import com.kenstevens.stratinit.client.gwt.model.GWTUnit;

public class GWTUnitsSelectedEvent extends GwtEvent<GWTUnitsSelectedEventHandler> {

	private final GWTSelectionSource source;
	private final List<GWTUnit> selectedUnits;

	public GWTUnitsSelectedEvent(List<GWTUnit> selectedUnits, GWTSelectionSource source) {
		this.selectedUnits = selectedUnits;
		this.source = source;
	}

	@Override
	protected void dispatch(GWTUnitsSelectedEventHandler handler) {
		handler.unitsSelected(selectedUnits, source);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<GWTUnitsSelectedEventHandler> getAssociatedType() {
		return GWTUnitsSelectedEventHandler.TYPE;
	}
}
