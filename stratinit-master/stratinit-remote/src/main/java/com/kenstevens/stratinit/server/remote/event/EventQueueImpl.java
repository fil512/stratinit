package com.kenstevens.stratinit.server.remote.event;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.main.Spring;
import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.EventKey;
import com.kenstevens.stratinit.model.EventKeyed;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Relation;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitBase;
import com.kenstevens.stratinit.model.UnitSeen;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.util.GameScheduleHelper;

@Service
public class EventQueueImpl implements EventQueue {
	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private Spring spring;
	@Autowired
	private EventTimer eventTimer;
	@Autowired
	private DataCache dataCache;

	public EventQueueImpl() {
	}

	public void shutdown() {
		eventTimer.shutdown();
	}

	public void schedule(City city) {
		if (UnitBase.isNotUnit(city.getBuild())) {
			return;
		}
		if (!city.getGame().hasStarted()) {
			return;
		}
		CityBuildEvent cityBuildEvent = spring.autowire(new CityBuildEvent( city ));
		eventTimer.schedule(cityBuildEvent);
	}

	public void schedule(UnitSeen unitSeen) {
		if (!unitSeen.getGame().hasStarted()) {
			return;
		}
		UnitSeenEvent unitSeenEvent = spring.autowire(new UnitSeenEvent( unitSeen ));
		eventTimer.schedule(unitSeenEvent);
	}

	public void schedule(Game game, boolean gameHasStarted) {
		if (gameHasStarted) {
			scheduleStartedGameEvents(game);
		} else {
			scheduleEventsForGameThatHasntStartedYet(game);
		}
	}

	private void scheduleEventsForGameThatHasntStartedYet(Game game) {
		Date startTime = game.getStartTime();
		if (startTime == null) {
			logger
					.info("Skipping scheduling game "
							+ game.getName()
							+ " since it doesn't have a start time yet (probably not enough players).");
			return;
		}
		scheduleMapEventIfGameNotMapped(game);
		scheduleGameStartEvent(game);
	}

	private void scheduleGameStartEvent(Game game) {
		logger.info("Scheduling game " + game + " to start at " + game.getStartTime());
		GameStartEvent gameStartEvent = spring.autowire(new GameStartEvent( game ));
		eventTimer.schedule(gameStartEvent);
	}

	private void scheduleMapEventIfGameNotMapped(Game game) {
		if (game.isMapped()) {
			return;
		}

		Date expectedMapTime = game.getExpectedMapTime();
		if (expectedMapTime == null) {
			logger.error("Trying to schedule game " + game.getName()
					+ " which has no expected map time.");
			return;
		}

		Date now = new Date();
		if (now.before(expectedMapTime)) {
			logger.info("Scheduling game " + game + " to map at "
					+ expectedMapTime);
		} else if (game.getMapped() == null) {
			Date oldStartTime = game.getStartTime();
			Date newStartTime = new Date(now.getTime() + Constants.getMappedToStartedMillis());
			logger.warn("Game "+game+" should have been mapped by now.  Mapping now and changing start time from "+oldStartTime+" to "+newStartTime);
			GameScheduleHelper.setStartTime(game, newStartTime);
		}
		GameMapEvent gameMapEvent = spring.autowire(new GameMapEvent( game ));
		eventTimer.schedule(gameMapEvent);
	}

	private void scheduleStartedGameEvents(Game game) {
		logger.info("Scheduling game " + game + " to end at " + game.getEnds());
		GameEndEvent gameEndEvent = spring.autowire(new GameEndEvent( game ));
		eventTimer.schedule(gameEndEvent);

		TechUpdateEvent techupdateEvent = spring.autowire(new TechUpdateEvent( game ));
		eventTimer.schedule(techupdateEvent);
	}

	public void schedule(Unit unit) {
		if (!unit.getGame().hasStarted()) {
			return;
		}

		UnitUpdateEvent unitUpdateEvent = spring.autowire(new UnitUpdateEvent( unit ));
		eventTimer.schedule(unitUpdateEvent);
	}

	public void schedule(Relation relation) {
		if (!relation.getGame().hasStarted()) {
			return;
		}

		RelationChangeEvent relationChangeEvent = spring.autowire(new RelationChangeEvent( relation ));
		eventTimer.schedule(relationChangeEvent);
	}

	public boolean cancel(EventKeyed eventKeyed) {
		return eventTimer.cancel(new EventKey(eventKeyed));
	}

	public void scheduleFlushCache() {
		FlushCacheEvent flushCacheEvent = new FlushCacheEvent(dataCache);
		logger.info("Cache Flush Period set to "+flushCacheEvent.getPeriodMilliseconds()+"ms");
		eventTimer.schedule(flushCacheEvent);
	}
}
