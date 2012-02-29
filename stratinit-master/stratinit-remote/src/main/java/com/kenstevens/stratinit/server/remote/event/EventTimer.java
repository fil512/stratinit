package com.kenstevens.stratinit.server.remote.event;

import com.kenstevens.stratinit.model.EventKey;
import com.kenstevens.stratinit.model.Updatable;

public interface EventTimer {

	public abstract void shutdown();

	public abstract void schedule(Event event);

	public abstract boolean cancel(EventKey eventKey);

	public abstract boolean isShuttingDown();

	public abstract Event getScheduledEvent(Updatable updatable);
}