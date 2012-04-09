package com.kenstevens.stratinit.server.event;

import com.kenstevens.stratinit.model.EventKey;
import com.kenstevens.stratinit.model.Updatable;

public interface EventTimer {

	void shutdown();

	void schedule(Event event);

	boolean cancel(EventKey eventKey);

	boolean isShuttingDown();

	Event getScheduledEvent(Updatable updatable);
}