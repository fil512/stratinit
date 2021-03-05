package com.kenstevens.stratinit.client.server.event.svc;

import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.client.server.event.*;
import com.kenstevens.stratinit.client.util.GameScheduleHelper;
import com.kenstevens.stratinit.config.IServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class EventQueue {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private EventTimer eventTimer;
    @Autowired
    private DataCache dataCache;
    @Autowired
    private EventFactory eventFactory;
    @Autowired
    private IServerConfig serverConfig;

    public void shutdown() {
        eventTimer.shutdown();
    }

    public void schedule(City city) {
        if (UnitBase.isNotUnit(city.getBuild())) {
            return;
        }
        if (!city.getParentGame().hasStarted()) {
            return;
        }
        CityBuildEvent cityBuildEvent = eventFactory.getCityBuildEvent(city);
        eventTimer.schedule(cityBuildEvent);
    }

    public void schedule(UnitSeen unitSeen) {
        if (!unitSeen.getGame().hasStarted()) {
            return;
        }

        UnitSeenEvent unitSeenEvent = eventFactory.getUnitSeenEvent(unitSeen);
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
                            + game.getGamename()
                            + " since it doesn't have a start time yet (probably not enough players).");
            return;
        }
        scheduleMapEventIfGameNotMapped(game);
        scheduleGameStartEvent(game);
    }

    private void scheduleGameStartEvent(Game game) {
        logger.info("Scheduling game " + game + " to start at " + game.getStartTime());
        GameStartEvent gameStartEvent = eventFactory.getGameStartEvent(game);
        eventTimer.schedule(gameStartEvent);
    }

    private void scheduleMapEventIfGameNotMapped(Game game) {
        if (game.isMapped()) {
            return;
        }

        Date expectedMapTime = game.getExpectedMapTime(serverConfig);
        if (expectedMapTime == null) {
            logger.error("Trying to schedule game " + game.getGamename()
                    + " which has no expected map time.");
            return;
        }

        Date now = new Date();
        if (now.before(expectedMapTime)) {
            logger.info("Scheduling game " + game + " to map at "
                    + expectedMapTime);
        } else if (game.getMapped() == null) {
            Date oldStartTime = game.getStartTime();
            Date newStartTime = new Date(now.getTime() + serverConfig.getMappedToStartedMillis());
            logger.warn("Game " + game + " should have been mapped by now.  Mapping now and changing start time from " + oldStartTime + " to " + newStartTime);
            GameScheduleHelper.setStartTime(game, newStartTime);
        }
        GameMapEvent gameMapEvent = eventFactory.getGameMapEvent(game, serverConfig);
        eventTimer.schedule(gameMapEvent);
    }

    private void scheduleStartedGameEvents(Game game) {
        logger.info("Scheduling game " + game + " to end at " + game.getEnds());
        GameEndEvent gameEndEvent = eventFactory.getGameEndEvent(game);
        eventTimer.schedule(gameEndEvent);

        TechUpdateEvent techupdateEvent = eventFactory.getTechUpdateEvent(game);
        eventTimer.schedule(techupdateEvent);
    }

    public void schedule(Unit unit) {
        if (!unit.getParentGame().hasStarted()) {
            return;
        }

        UnitUpdateEvent unitUpdateEvent = eventFactory.getUnitUpdateEvent(unit);
        eventTimer.schedule(unitUpdateEvent);
    }

    public void schedule(Relation relation) {
        if (!relation.getGame().hasStarted()) {
            return;
        }

        RelationChangeEvent relationChangeEvent = eventFactory.getRelationChangeEvent(relation);
        eventTimer.schedule(relationChangeEvent);
    }

    public boolean cancel(EventKeyed eventKeyed) {
        return eventTimer.cancel(new EventKey(eventKeyed));
    }

    public void scheduleFlushCache() {
        FlushCacheEvent flushCacheEvent = new FlushCacheEvent(dataCache);
        logger.info("Cache Flush Period set to " + flushCacheEvent.getPeriodMilliseconds() + "ms");
        eventTimer.schedule(flushCacheEvent);
    }
}