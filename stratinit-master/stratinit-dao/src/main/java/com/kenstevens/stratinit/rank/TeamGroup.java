package com.kenstevens.stratinit.rank;

import java.util.List;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.dto.SITeam;

public class TeamGroup {

	private final List<SITeam> teams;
	private final List<SITeam> winners = Lists.newArrayList();
	private final List<SITeam> losers = Lists.newArrayList();

	public TeamGroup(List<SITeam> teams) {
		this.teams = teams;
		divide();
	}

	private void divide() {
		int winnerScore = teams.get(0).score;
		for (SITeam team : teams) {
			if (team.getScore() >= winnerScore) {
				winners.add(team);
			} else {
				losers.add(team);
			}
		}
	}

	public List<SITeam> winners() {
		return winners;
	}

	public List<SITeam> losers() {
		return losers;
	}
}
