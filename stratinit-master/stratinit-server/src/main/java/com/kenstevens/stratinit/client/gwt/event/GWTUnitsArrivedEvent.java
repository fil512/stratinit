package com.kenstevens.stratinit.client.gwt.event;

import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.GwtEvent;
import com.kenstevens.stratinit.client.gwt.model.GWTUnit;

public class GWTUnitsArrivedEvent extends GwtEvent<GWTUnitsArrivedEventHandler> {

	private final Map<Integer, GWTUnit> unitMap;
	private final List<GWTUnit> result;

	public GWTUnitsArrivedEvent(Map<Integer, GWTUnit> unitMap,
			List<GWTUnit> result) {
				this.unitMap = unitMap;
				this.result = result;
	}

	@Override
	protected void dispatch(GWTUnitsArrivedEventHandler handler) {
		handler.receiveNewUnits(unitMap, result);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<GWTUnitsArrivedEventHandler> getAssociatedType() {
		return GWTUnitsArrivedEventHandler.TYPE;
	}
}
