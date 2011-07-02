package com.kenstevens.stratinit.server.remote.event;

import java.util.Date;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.kenstevens.stratinit.model.EventKey;
import com.kenstevens.stratinit.model.EventKeyed;
import com.kenstevens.stratinit.model.Updatable;
import com.kenstevens.stratinit.util.UpdateCalculator;
import com.kenstevens.stratinit.util.UpdateManager;

public abstract class Event {
	private Logger logger = Logger.getLogger(getClass());
	public static final int NO_PERIOD = -1;
	private final TimerTask timerTask;
	private final Date time;
	private long periodMilliseconds;
	// TODO REF remove this and point to actual Updatable object instead
	private EventKey eventKey;
	private final boolean keyUnique;

	protected abstract void execute();

	public String toString() {
		return eventKey+" period (s): "+(periodMilliseconds/1000);
	}

	public Event(Date time, boolean keyUnique) {
		timerTask = new TimerTask() {
			@Override
			public void run() {
				try {
					execute();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		};
		this.time = time;
		this.keyUnique = keyUnique;
		this.periodMilliseconds = NO_PERIOD;
	}

	public Event(boolean blitz, Date time, int periodMilliseconds, boolean keyUnique) {
		this(time, keyUnique);
		this.periodMilliseconds = UpdateCalculator.shrinkTime(blitz, periodMilliseconds);
	}

	public Event(Updatable updatable) {
		this(updatable.isBlitz(), new UpdateManager(updatable).getNextFutureUpdate(), updatable.getUpdatePeriodMilliseconds(), updatable.isKeyUnique());
		this.eventKey = new EventKey(updatable);
	}

	public Event(EventKeyed eventKeyed, Date time) {
		this(time, eventKeyed.isKeyUnique());
		this.eventKey = new EventKey(eventKeyed);
	}

	public void cancel() {
		timerTask.cancel();
	}

	public TimerTask getTask() {
		return timerTask;
	}

	public Date getTime() {
		return time;
	}

	public long getPeriodMilliseconds() {
		return periodMilliseconds;
	}

	// null means no map
	public final EventKey getEventKey() {
		return eventKey;
	}

	public boolean isKeyUnique() {
		return keyUnique;
	}

}
