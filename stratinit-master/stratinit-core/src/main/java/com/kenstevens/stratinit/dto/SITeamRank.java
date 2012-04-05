package com.kenstevens.stratinit.dto;

import com.kenstevens.stratinit.model.Rankable;

public class SITeamRank extends Rankable {
	final SITeam team;
	
	public SITeamRank(SITeam team, Double rank) {
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
