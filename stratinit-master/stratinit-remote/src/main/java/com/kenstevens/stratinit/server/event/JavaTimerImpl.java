package com.kenstevens.stratinit.server.event;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class JavaTimerImpl implements JavaTimer {
	
	private final Timer timer = new Timer();

	@Override
	public void cancel() {
		timer.cancel();
	}

	@Override
	public void schedule(TimerTask task, Date time, long periodMillis) {
		timer.schedule(task, time, periodMillis);
	}

	@Override
	public void schedule(TimerTask task, Date time) {
		timer.schedule(task, time);
	}

	@Override
	public void cancel(Event event) {
		event.cancel();
	}

}
