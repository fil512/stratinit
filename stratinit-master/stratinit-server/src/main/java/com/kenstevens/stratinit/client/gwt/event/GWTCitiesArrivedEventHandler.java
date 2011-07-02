package com.kenstevens.stratinit.client.gwt.event;

import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.kenstevens.stratinit.client.gwt.model.GWTCity;

public abstract class GWTCitiesArrivedEventHandler implements EventHandler {
	public static final Type<GWTCitiesArrivedEventHandler> TYPE = new Type<GWTCitiesArrivedEventHandler>();

	abstract public void receiveNewCities(Map<String, GWTCity> cityMap, List<GWTCity> result);
}
