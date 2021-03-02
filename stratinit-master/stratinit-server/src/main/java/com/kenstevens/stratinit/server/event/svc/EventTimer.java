package com.kenstevens.stratinit.server.event.svc;

import com.google.common.annotations.VisibleForTesting;
import com.kenstevens.stratinit.model.EventKey;
import com.kenstevens.stratinit.model.Updatable;
import com.kenstevens.stratinit.server.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

@Service
public class EventTimer {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Map<EventKey, Event> eventMap = new HashMap<EventKey, Event>();
    private final Queue<Event> queue = new LinkedList<>();
    @Autowired
    private JavaTimer javaTimer;
    private boolean shuttingDown = false;
    private boolean started = false;

    public void shutdown() {
        shuttingDown = true;
        javaTimer.cancel();
        logger.info("Event Queue shut down.");
    }

    public void schedule(Event event) {
        if (!started) {
            logger.info("Not started yet.  Queuing event {}", event);
            queue(event);
            return;
        }
        if (shuttingDown || event.getTime() == null) {
            return;
        }
        EventKey eventKey = event.getEventKey();
        if (event.isKeyUnique()) {
            // Only one event allowed per key
            if (eventKey != null && eventMap.get(eventKey) != null) {
                cancel(eventKey);
            }
        }
        long periodMillis = event.getPeriodMilliseconds();
        if (periodMillis != Event.NO_PERIOD) {
            javaTimer.schedule(event.getTask(), event.getTime(), periodMillis);
        } else {
            javaTimer.schedule(event.getTask(), event.getTime());
        }
        if (eventKey != null) {
            eventMap.put(eventKey, event);
        }
    }

    public boolean cancel(EventKey eventKey) {
        Event event = eventMap.get(eventKey);
        if (event != null) {
            javaTimer.cancel(event);
            eventMap.remove(eventKey);
            return true;
        }
        return false;
    }

    public boolean isShuttingDown() {
        return shuttingDown;
    }

    public Event getScheduledEvent(Updatable updatable) {
        return eventMap.get(new EventKey(updatable));
    }

    public void start() {
        started = true;
        shuttingDown = false;
        queue.forEach(this::schedule);
        queue.clear();
    }

    @VisibleForTesting
    public void clearForUnitTest() {
        eventMap.clear();
        queue.clear();
    }

    public boolean isStarted() {
        return started;
    }

    private void queue(Event event) {
        queue.add(event);
    }
}