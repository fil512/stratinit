package com.kenstevens.stratinit.client.gwt.event;

import com.google.gwt.event.shared.GwtEvent;
import com.kenstevens.stratinit.client.gwt.model.GWTSector;

public class GWTSectorsArrivedEvent extends GwtEvent<GWTSectorsArrivedEventHandler> {


	private final GWTSector[][] sectors;

	public GWTSectorsArrivedEvent(GWTSector[][] origSectors) {
		this.sectors = origSectors;
	}

	@Override
	protected void dispatch(GWTSectorsArrivedEventHandler handler) {
		handler.receiveNewSectors(sectors);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<GWTSectorsArrivedEventHandler> getAssociatedType() {
		return GWTSectorsArrivedEventHandler.TYPE;
	}
}
