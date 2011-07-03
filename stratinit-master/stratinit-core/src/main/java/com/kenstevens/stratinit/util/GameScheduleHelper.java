package com.kenstevens.stratinit.util;

import java.util.Date;

import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.type.Constants;

public final class GameScheduleHelper {
	private static final int DAY_MILLIS = 24 * 60 * 60 * 1000;


	private GameScheduleHelper() {}

	public static void setStartTimeBasedOnNow(Game game) {
		Date now = new Date();
		Date startTime = now;
		if (!game.isBlitz()) {
			startTime = new Date(now.getTime() + Constants.getScheduledToStartedMillis());
		}
    	game.setStartTime(startTime);
    	setEnds(game, startTime);
    	game.setLastUpdated(startTime);
    }


	public static void setEnds(Game game, Date startTime) {
	if (startTime == null) {
		return;
	}
	game.setEnds(new Date(startTime.getTime() + UpdateCalculator.shrinkTime(game.isBlitz(), (long)game.getDuration() * 24 * 60 * 60 * 1000)));
}


	public static void setStartTime(Game game, Date startTime) {
    	game.setStartTime(startTime);
    	setEnds(game, startTime);
	}


	public static int dateToDay(Game game, Date date) {
		if (game == null || game.getMapped() == null || date == null) {
			return 0;
		}
		long millis = date.getTime() - game.getMapped().getTime();
		if (millis < 0) {
			millis = 0;
		}
		return 1 + (int)(millis / DAY_MILLIS);
	}
}
