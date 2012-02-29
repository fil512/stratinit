package com.kenstevens.stratinit.model;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.kenstevens.stratinit.util.GameScheduleHelper;

public class GameTest {
	@Test
	public void endsAfterStarts() {
		Game game = new Game("test");
		game.setDuration(30);
		GameScheduleHelper.setStartTimeBasedOnNow(game);
		assertTrue(game.getEnds().after(game.getStartTime()));
	}
}
