package com.kenstevens.stratinit.helper;

import com.kenstevens.stratinit.client.model.Player;

public class PlayerHelper {
    private static final String PLAYER_ME = "PLAYER_ME";
    private static final String PLAYER_THEM = "PLAYER_THEM";
    public static final Player me = new Player(PLAYER_ME);
    public static final Player them = new Player(PLAYER_THEM);

    public static Player newPlayer(int i) {
        return new Player(PLAYER_THEM + i);
    }
}
