package com.kenstevens.stratinit.client.gwt.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.kenstevens.stratinit.client.gwt.model.GWTSector;

public abstract class GWTSectorsArrivedEventHandler implements EventHandler {
	public static final Type<GWTSectorsArrivedEventHandler> TYPE = new Type<GWTSectorsArrivedEventHandler>();

	abstract public void receiveNewSectors(GWTSector[][] sectors);
}
