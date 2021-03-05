package com.kenstevens.stratinit.helper;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Player;

// FIXME search for PLAYER_ME stuff and use these helpers
public class NationHelper {
    public static final Nation nationMe = new Nation(GameHelper.game, PlayerHelper.me);
    public static final Nation nationThem = new Nation(GameHelper.game, PlayerHelper.them);

    public static Nation newNation(Player player) {
        return new Nation(GameHelper.game, player);
    }
}
