package com.kenstevens.stratinit.client.gwt.event;

import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.kenstevens.stratinit.client.gwt.model.GWTNation;

public abstract class GWTNationsArrivedEventHandler implements EventHandler {
	public static final Type<GWTNationsArrivedEventHandler> TYPE = new Type<GWTNationsArrivedEventHandler>();

	abstract public void receiveNewNations(Map<Integer, GWTNation> nationMap, List<GWTNation> result);
}
