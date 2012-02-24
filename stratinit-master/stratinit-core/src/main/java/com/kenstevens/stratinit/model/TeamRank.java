package com.kenstevens.stratinit.model;

import com.kenstevens.stratinit.dto.SITeam;

public class TeamRank {
	private final SITeam team;
	private Double rank;
	private int gameCount = 0;

	public TeamRank(SITeam team, Double rank) {
		this.team = team;
		this.setRank(rank);
	}

	public SITeam getTeam() {
		return team;
	}

	public Double getRank() {
		return rank;
	}

	public void setRank(Double rank) {
		this.rank = rank;
	}

	public int getGameCount() {
		return gameCount;
	}

	public void setGameCount(int gameCount) {
		this.gameCount = gameCount;
	}

	public void incrementGameCount() {
		++this.gameCount;
	}

	public String getName() {
		return this.team.getName();
	}

}
