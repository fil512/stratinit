package com.kenstevens.stratinit.model;

import com.kenstevens.stratinit.dto.SITeam;

public class TeamRank extends Rankable {
	final SITeam team;
	
	public TeamRank(SITeam team, Double rank) {
		super(team.getName());
		this.team = team;
		this.setRank(rank);
	}

	public SITeam getTeam() {
		return team;
	}

	public boolean contains(String playerName) {
		return team.contains(playerName);
	}
}
