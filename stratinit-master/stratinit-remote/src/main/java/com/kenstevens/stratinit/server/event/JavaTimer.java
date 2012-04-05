package com.kenstevens.stratinit.server.event;

import java.util.Date;
import java.util.TimerTask;

public interface JavaTimer {

	public abstract void cancel();

	public abstract void schedule(TimerTask task, Date time, long periodMillis);

	public abstract void schedule(TimerTask task, Date time);

	public abstract void cancel(Event event);

}