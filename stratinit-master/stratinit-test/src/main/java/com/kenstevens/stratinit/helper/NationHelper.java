package com.kenstevens.stratinit.helper;

import com.kenstevens.stratinit.model.Nation;

// FIXME search for PLAYER_ME stuff and use these helpers
public class NationHelper {
    public static final Nation nationMe = new Nation(GameHelper.game, PlayerHelper.me);
    public static final Nation nationThem = new Nation(GameHelper.game, PlayerHelper.them);
}
