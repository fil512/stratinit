package com.kenstevens.stratinit.server.event;

import java.util.Date;
import java.util.TimerTask;

public interface JavaTimer {

	void cancel();

	void schedule(TimerTask task, Date time, long periodMillis);

	void schedule(TimerTask task, Date time);

	void cancel(Event event);

}