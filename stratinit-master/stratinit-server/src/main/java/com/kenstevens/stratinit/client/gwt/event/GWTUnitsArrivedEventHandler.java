package com.kenstevens.stratinit.client.gwt.event;

import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.kenstevens.stratinit.client.gwt.model.GWTUnit;

public abstract class GWTUnitsArrivedEventHandler implements EventHandler {
	public static final Type<GWTUnitsArrivedEventHandler> TYPE = new Type<GWTUnitsArrivedEventHandler>();

	abstract public void receiveNewUnits(Map<Integer, GWTUnit> unitMap, List<GWTUnit> result);
}
