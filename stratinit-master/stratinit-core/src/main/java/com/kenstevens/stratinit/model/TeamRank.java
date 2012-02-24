package com.kenstevens.stratinit.model;

import com.kenstevens.stratinit.dto.SITeam;

public class TeamRank extends Rankable {
	final SITeam team;
	
	public TeamRank(SITeam team, Double rank) {
		this.team = team;
		this.setRank(rank);
	}

	public SITeam getTeam() {
		return team;
	}

	public String getName() {
		return this.team.getName();
	}
}
