package com.kenstevens.stratinit.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.eclipse.swt.widgets.Display;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Service
public class ArrivedDataEventAccumulator {
	private final Map<Class<? extends DataArrivedEvent>, DataArrivedEvent> eventMap = Maps.newHashMap();
	@Autowired
	StratinitEventBus eventBus;

	public void addEvent(DataArrivedEvent arrivedEvent) {
		eventMap.put(arrivedEvent.getClass(), arrivedEvent);
	}

	public synchronized void clear() {
		eventMap.clear();
	}

	public synchronized void fireEvents() {
		if (eventMap.isEmpty()) {
			return;
		}
		// Thread-safe copy
		final ArrayList<DataArrivedEvent> events = Lists.newArrayList(eventMap.values());
		Display display = Display.getDefault();

		if (display.isDisposed())
			return;
		display.asyncExec(() -> {
			for (DataArrivedEvent event : events) {
				eventBus.post(event);
			}
		});
	}
}
