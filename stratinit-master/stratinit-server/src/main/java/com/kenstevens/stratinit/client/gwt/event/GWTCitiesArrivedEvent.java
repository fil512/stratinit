package com.kenstevens.stratinit.client.gwt.event;

import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.GwtEvent;
import com.kenstevens.stratinit.client.gwt.model.GWTCity;

public class GWTCitiesArrivedEvent extends GwtEvent<GWTCitiesArrivedEventHandler> {

	private final Map<String, GWTCity> cityMap;
	private final List<GWTCity> result;

	public GWTCitiesArrivedEvent(Map<String, GWTCity> cityMap, List<GWTCity> result) {
		this.cityMap = cityMap;
		this.result = result;
	}

	@Override
	protected void dispatch(GWTCitiesArrivedEventHandler handler) {
		handler.receiveNewCities(cityMap, result);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<GWTCitiesArrivedEventHandler> getAssociatedType() {
		return GWTCitiesArrivedEventHandler.TYPE;
	}
}
