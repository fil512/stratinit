package com.kenstevens.stratinit.helper;

import com.kenstevens.stratinit.model.Player;

public class PlayerHelper {
    private static final String PLAYER_ME = "PLAYER_ME";
    private static final String PLAYER_THEM = "PLAYER_THEM";
    public Player me = new Player(PLAYER_ME);
    public Player them = new Player(PLAYER_THEM);
}
