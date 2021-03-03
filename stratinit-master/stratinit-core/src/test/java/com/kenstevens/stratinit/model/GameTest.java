package com.kenstevens.stratinit.model;

import com.kenstevens.stratinit.util.GameScheduleHelper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameTest {
	@Test
	public void endsAfterStarts() {
        Game game = new Game("test");
        game.setDuration(30);
        GameScheduleHelper.setStartTimeBasedOnNow(game, 1);
        assertTrue(game.getEnds().after(game.getStartTime()));
    }
}
