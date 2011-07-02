package com.kenstevens.stratinit.client.gwt.event;

import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.GwtEvent;
import com.kenstevens.stratinit.client.gwt.model.GWTNation;

public class GWTNationsArrivedEvent extends GwtEvent<GWTNationsArrivedEventHandler> {

	private final Map<Integer, GWTNation> nationMap;
	private final List<GWTNation> result;

	public GWTNationsArrivedEvent(Map<Integer, GWTNation> nationMap, List<GWTNation> result) {
		this.nationMap = nationMap;
		this.result = result;
	}

	@Override
	protected void dispatch(GWTNationsArrivedEventHandler handler) {
		handler.receiveNewNations(nationMap, result);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<GWTNationsArrivedEventHandler> getAssociatedType() {
		return GWTNationsArrivedEventHandler.TYPE;
	}
}
