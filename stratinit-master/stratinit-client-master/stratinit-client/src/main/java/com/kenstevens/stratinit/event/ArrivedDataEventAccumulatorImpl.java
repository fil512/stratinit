package com.kenstevens.stratinit.event;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.swt.widgets.Display;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Service
public class ArrivedDataEventAccumulatorImpl implements ArrivedDataEventAccumulator {
	@Autowired
	StratinitEventBus eventBus;

	private final Map<Class<? extends DataArrivedEvent>, DataArrivedEvent> eventMap = Maps.newHashMap();

	@Override
	public void addEvent(DataArrivedEvent arrivedEvent) {
		eventMap.put(arrivedEvent.getClass(), arrivedEvent);
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
		final ArrayList<DataArrivedEvent> events = Lists.newArrayList(eventMap.values());
		Display display = Display.getDefault();

		if (display.isDisposed())
			return;
		display.asyncExec(new Runnable() {
			public void run() {
				for (DataArrivedEvent event : events) {
					eventBus.post(event);
				}
			}
		});
	}
}
