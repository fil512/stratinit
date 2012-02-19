package com.kenstevens.stratinit.server.remote.event;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.model.EventKey;
import com.kenstevens.stratinit.model.Updatable;

@Service
public class EventTimerImpl implements EventTimer {
	private final Log logger = LogFactory.getLog(getClass());

	private final JavaTimer javaTimer = new JavaTimerImpl();
	private final Map<EventKey, Event> eventMap = new HashMap<EventKey, Event>();
	private boolean shuttingDown = false;

	public void shutdown() {
		shuttingDown = true;
		javaTimer.cancel();
		logger.info("Event Queue shut down.");
	}

	public void schedule(Event event) {
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

	@Override
	public boolean isShuttingDown() {
		return shuttingDown;
	}

	@Override
	public Event getScheduledEvent(Updatable updatable) {
		return eventMap.get(new EventKey(updatable));
	}
}
