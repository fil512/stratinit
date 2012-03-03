package com.kenstevens.stratinit.wicket.components;

import org.apache.wicket.markup.html.basic.Label;

import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.wicket.security.AuthenticatedPanel;

public class PlayerStatPanel extends AuthenticatedPanel {
	private static final long serialVersionUID = 4303067574741765294L;

	public PlayerStatPanel(String id, Player player) {
		super(id);	
		add(new Label("wins", ""+player.getWins()));
		add(new Label("played", ""+player.getPlayed()));
		add(new Label("winperc", ""+player.getWinPerc()));
	}
}
