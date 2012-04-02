package com.kenstevens.stratinit.wicket.login;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import com.kenstevens.stratinit.model.Player;

public class PlayerStatPanel extends Panel {
	private static final long serialVersionUID = 4303067574741765294L;

	public PlayerStatPanel(String id, Player player) {
		super(id);	
		add(new Label("wins", ""+player.getWins()));
		add(new Label("played", ""+player.getPlayed()));
		add(new Label("winperc", ""+player.getWinPerc()));
	}
}
