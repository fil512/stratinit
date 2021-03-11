package com.kenstevens.stratinit.helper;

import com.kenstevens.stratinit.client.model.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class PlayerHelper {
    public static final String PLAYER_ME = "PLAYER_ME";
    public static final String PLAYER_THEM = "PLAYER_THEM";
    public static final Player me = new Player(PLAYER_ME);
    public static final Player them = new Player(PLAYER_THEM);
    private static final AtomicInteger playerIndex = new AtomicInteger();

    public static Player newPlayer(int i) {
        Player player = new Player(PLAYER_THEM + i);
        player.setEmail("foo@foo.com");
        return player;
    }

    public static Player newPlayer() {
        return newPlayer(playerIndex.incrementAndGet());
    }
}
