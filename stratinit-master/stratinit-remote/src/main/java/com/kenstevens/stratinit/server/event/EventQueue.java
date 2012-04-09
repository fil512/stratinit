package com.kenstevens.stratinit.server.event;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.EventKeyed;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Relation;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitSeen;

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