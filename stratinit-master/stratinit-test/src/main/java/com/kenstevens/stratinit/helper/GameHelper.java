package com.kenstevens.stratinit.helper;

import com.kenstevens.stratinit.model.Game;

public class GameHelper {
    public static final Game game = new Game();
    private static final Integer GAME_ID = 2401;

    static {
        game.setId(GAME_ID);
    }
}
