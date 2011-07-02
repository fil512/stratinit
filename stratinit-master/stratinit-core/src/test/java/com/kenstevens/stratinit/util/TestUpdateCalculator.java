package com.kenstevens.stratinit.util;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class TestUpdateCalculator {
	private UpdateCalculator updateCalculator;
	private Date now;
	private Date lastUpdated;

	@Before
	public void setLastUpdate() {
		// 10:00
		now = new Date();
		// 7:30
		lastUpdated = minutesBefore(now, 60 * 2 + 30);
		int oneHourMillis = 60*60*1000;
		updateCalculator = new UpdateCalculator(false, unshrinkTime(false, oneHourMillis));
	}

	private static int unshrinkTime(boolean blitz, int periodMillis) {
		int retval = periodMillis;
		if (blitz) {
			// shrink 10 days down to 2 hours
			int hoursInTenDays = 10*24;
			retval *= hoursInTenDays;
			retval /= 2;
		}
		return retval;
	}

	private Date minutesBefore(Date date, int minutes) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, -1 * minutes);
		Date retval = cal.getTime();
		return retval;
	}

	@Test
	public void testMissedUpdates() {
		assertEquals(2, updateCalculator.getMissedUpdates(lastUpdated));
	}

	@Test
	public void nextFutureUpdate() {
		Date then = updateCalculator.nextFutureUpdate(lastUpdated);

		long diffMinutes = diffMinutes(now, then);
		assertEquals(30, diffMinutes);
	}

	private long diffMinutes(Date before, Date after) {
		long diffSeconds = (after.getTime() - before.getTime()) / 1000;
		long diffMinutes = diffSeconds / 60;
		return diffMinutes;
	}

	@Test
	public void getNextMissedBuildTime() {
		Date nextBuildTime = updateCalculator
				.getNextMissedBuildTime(lastUpdated);
		long diffMinutes = diffMinutes(nextBuildTime, now);
		assertEquals(90, diffMinutes);
	}

}
