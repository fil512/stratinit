package com.kenstevens.stratinit.helper;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Player;

public class NationHelper {
    public static final Nation nationMe = new Nation(GameHelper.game, PlayerHelper.me);
    public static final String nationMeName = nationMe.getName();
    public static final Nation nationThem = new Nation(GameHelper.game, PlayerHelper.them);
    public static final String nationThemName = nationThem.getName();

    public static Nation newNation(Player player) {
        return new Nation(GameHelper.game, player);
    }
}
