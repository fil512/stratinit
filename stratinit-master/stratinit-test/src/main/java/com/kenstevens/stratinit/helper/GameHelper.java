package com.kenstevens.stratinit.helper;

import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.util.GameScheduleHelper;
import org.apache.commons.lang3.time.DateUtils;

public class GameHelper {
    public static final int GAME_SIZE = 15;
    private static final Integer GAME_ID = 2401;
    private static final String GAME_NAME = "GAME_NAME";
    public static Integer gameId = GAME_ID;
    public static String gameName = GAME_NAME;
    public static Game game;

    static {
        newGame();
    }

    public static Game newGame() {
        game = new Game(GAME_NAME);
        game.setId(GAME_ID);
        return game;
    }

    public static Game newMappedGame(int numIslands) {
        Game retval = new Game(GAME_NAME, GAME_SIZE);
        retval.setBlitz(true);
        GameScheduleHelper.setStartTimeBasedOnNow(retval, DateUtils.MILLIS_PER_DAY);
        retval.setBlitz(false);
        retval.setIslands(numIslands);
        retval.setMapped();
        return retval;
    }
}
