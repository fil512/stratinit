package com.kenstevens.stratinit.util;

import java.util.Calendar;
import java.util.Date;

public class UpdateCalculator {
	private long periodMillis;
	public UpdateCalculator(boolean blitz, int periodMillis) {
		this.periodMillis = shrinkTime(blitz, periodMillis);
	}

	public static long shrinkTime(boolean blitz, long periodMillis) {
		long shrunkTime = periodMillis;
		if (blitz) {
			// shrink 10 days down to 2 hours
			int hoursInTenDays = 10*24;
			shrunkTime *= 2;
			shrunkTime /= hoursInTenDays;
		}
		return shrunkTime;
	}

	public int getMissedUpdates(Date lastUpdated) {
		if (lastUpdated == null) {
			return 0;
		}
		long now = new Date().getTime();
		long secondsSinceLastUpdate = millisSinceLastUpdate(now, lastUpdated);
		return (int)(secondsSinceLastUpdate / periodMillis);
	}

	private int millisSinceLastUpdate(long now, Date lastUpdated) {
		return (int)(now - lastUpdated.getTime());
	}

	private long millisSinceLastTick(long now, Date lastUpdated) {
		return millisSinceLastUpdate(now, lastUpdated) % periodMillis;
	}

	private long millisToNextTick(long now, Date lastUpdated) {
		return periodMillis - millisSinceLastTick(now, lastUpdated);
	}

	public Date nextFutureUpdate(Date lastUpdated) {
		Date date = new Date();
		if (lastUpdated == null) {
			return null;
		}
		if (lastUpdated.after(date)) {
			return lastUpdated;
		}
		long now = date.getTime();
		long millisToNextTick = millisToNextTick(now, lastUpdated);
		return millisFrom(date, millisToNextTick);
	}

	private Date millisFrom(Date date, long millis) {
		if (date == null) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MILLISECOND, (int)millis);
		return cal.getTime();
	}

	public Date getNextMissedBuildTime(Date buildStarted) {
		return millisFrom(buildStarted, periodMillis);
	}
}
