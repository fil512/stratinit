package com.kenstevens.stratinit.server.event.svc;

import com.kenstevens.stratinit.model.*;

public interface EventQueue {

	void shutdown();

	void schedule(City city);

	void schedule(UnitSeen unitSeen);

	void schedule(Game game, boolean gameStarted);

	void schedule(Unit unit);

	void schedule(Relation relation);

	boolean cancel(EventKeyed eventKeyed);

	void scheduleFlushCache();
}