package com.kenstevens.stratinit.event;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.swt.widgets.Display;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.HandlerManager;

@Deprecated
@Service
public class OldArrivedDataEventAccumulatorImpl implements OldArrivedDataEventAccumulator {
	@Autowired
	HandlerManager handlerManager;

	private final Map<Type<? extends OldDataArrivedEventHandler>, OldDataArrivedEvent<? extends OldDataArrivedEventHandler>> eventMap = Maps.newHashMap();

	@Override
	public void addEvent(OldDataArrivedEvent<? extends OldDataArrivedEventHandler> arrivedEvent) {
		Type<? extends OldDataArrivedEventHandler> type = arrivedEvent.getAssociatedType();
		eventMap.put(type, arrivedEvent);
	}

	@Override
	public synchronized void clear() {
		eventMap.clear();
	}

	@Override
	public synchronized void fireEvents() {
		if (eventMap.isEmpty()) {
			return;
		}
		// Thread-safe copy
		final ArrayList<OldDataArrivedEvent<? extends OldDataArrivedEventHandler>> events = Lists.newArrayList(eventMap.values());
		Display display = Display.getDefault();

		if (display.isDisposed())
			return;
		display.asyncExec(new Runnable() {
			public void run() {
				for (OldDataArrivedEvent<? extends OldDataArrivedEventHandler> event : events) {
					handlerManager.fireEvent(event);
				}
			}
		});
	}
}
