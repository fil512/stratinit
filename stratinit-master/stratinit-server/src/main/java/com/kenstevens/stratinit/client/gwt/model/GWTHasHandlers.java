package com.kenstevens.stratinit.client.gwt.model;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public class GWTHasHandlers implements HasHandlers {
	protected HandlerManager manager = null;

	protected final <H extends EventHandler> HandlerRegistration addEventHandler(
			GwtEvent.Type<H> type, final H handler) {
		return ensureHandlers().addHandler(type, handler);
	}

	HandlerManager ensureHandlers() {
		if (manager == null) {
			manager = new HandlerManager(this);
		}
		return manager;
	}

	public void fireEvent(final GwtEvent<?> event) {
		if (manager != null) {
			manager.fireEvent(event);
		}
	}
}
