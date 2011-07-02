package com.kenstevens.stratinit.server.gwtrequest.translate;

import com.kenstevens.stratinit.client.gwt.model.GWTPlayer;
import com.kenstevens.stratinit.model.Player;

public final class GWTPlayerTranslate {
	private GWTPlayerTranslate() {}

	public static GWTPlayer translate(Player player) {
		GWTPlayer retval = new GWTPlayer();
		retval.setUsername(player.getUsername());
		retval.setEmail(player.getEmail());
		retval.setEmailGameMail(player.isEmailGameMail());
		retval.setEnabled(player.isEnabled());
		retval.setWins(player.getWins());
		retval.setPlayed(player.getPlayed());
		return retval;
	}
}
