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

@Service
public class ArrivedDataEventAccumulatorImpl implements ArrivedDataEventAccumulator {
	@Autowired
	HandlerManager handlerManager;

	private final Map<Type<? extends DataArrivedEventHandler>, DataArrivedEvent<? extends DataArrivedEventHandler>> eventMap = Maps.newHashMap();

	@Override
	public void addEvent(DataArrivedEvent<? extends DataArrivedEventHandler> arrivedEvent) {
		Type<? extends DataArrivedEventHandler> type = arrivedEvent.getAssociatedType();
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
		final ArrayList<DataArrivedEvent<? extends DataArrivedEventHandler>> events = Lists.newArrayList(eventMap.values());
		Display display = Display.getDefault();

		if (display.isDisposed())
			return;
		display.asyncExec(new Runnable() {
			public void run() {
				for (DataArrivedEvent<? extends DataArrivedEventHandler> event : events) {
					handlerManager.fireEvent(event);
				}
			}
		});
	}
}
