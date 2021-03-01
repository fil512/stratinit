package com.kenstevens.stratinit.helper;

import com.kenstevens.stratinit.model.Game;

public class GameHelper {
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
}
