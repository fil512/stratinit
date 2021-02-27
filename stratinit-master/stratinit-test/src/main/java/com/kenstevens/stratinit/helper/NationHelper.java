package com.kenstevens.stratinit.helper;

import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Nation;

// FIXME search for PLAYER_ME stuff and use these helpers
public class NationHelper {
    private final GameHelper gameHelper = new GameHelper();
    public final Game game = gameHelper.game;
    private final PlayerHelper playerHelper = new PlayerHelper();
    public final Nation nationMe = new Nation(gameHelper.game, playerHelper.me);
    public final Nation nationThem = new Nation(gameHelper.game, playerHelper.them);
}
