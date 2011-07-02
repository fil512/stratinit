package com.kenstevens.stratinit.server.remote.event;

import java.util.Date;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.EventKeyed;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Relation;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitSeen;

public interface EventQueue {

	public abstract void shutdown();

	public abstract void schedule(City city);

	public abstract void schedule(UnitSeen unitSeen);

	public abstract void schedule(Game game, boolean gameStarted);

	public abstract void schedule(Unit unit);

	public abstract void schedule(Relation relation);

	public abstract boolean cancel(EventKeyed eventKeyed);

	public abstract void scheduleFlushCache(Date startTime);
}