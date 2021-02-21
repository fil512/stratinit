package com.kenstevens.stratinit.server.event.svc;

import com.kenstevens.stratinit.model.EventKey;
import com.kenstevens.stratinit.model.Updatable;
import com.kenstevens.stratinit.server.event.Event;

public interface EventTimer {

	void shutdown();

	void schedule(Event event);

	boolean cancel(EventKey eventKey);

	boolean isShuttingDown();

	Event getScheduledEvent(Updatable updatable);

	void start();
}