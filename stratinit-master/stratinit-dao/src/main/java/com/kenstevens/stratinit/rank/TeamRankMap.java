package com.kenstevens.stratinit.rank;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.kenstevens.stratinit.client.model.Rankable;
import com.kenstevens.stratinit.client.server.rest.rank.ELOCalculator;
import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.dto.SITeamRank;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class TeamRankMap {
	public static final double START_RANK = 1500;
	private final Set<String> nations = Sets.newHashSet();
	private final Map<SITeam, SITeamRank> teamRankMap = Maps.newHashMap();


	public void recordWin(SITeam team) {
		Rankable teamRank = getRank(team);
		teamRank.won();
	}

	public void recordVictory(SITeam winner, SITeam loser) {
		Rankable winRank = getRank(winner);
		Rankable loseRank = getRank(loser);
		winRank.victorious();
		loseRank.defeated();
		ELOCalculator eloCalculator = new ELOCalculator(winRank.getRank(),
				loseRank.getRank());
		winRank.setRank(eloCalculator.getWinNewELO());
		loseRank.setRank(eloCalculator.getLoseNewELO());
	}

	private void addNations(SITeam team) {
		nations.add(team.getNation1());
		if (team.getNation2() != null) {
			nations.add(team.getNation2());
		}
		
	}


	private Rankable getRank(SITeam team) {
		if (!teamRankMap.containsKey(team)) {
			teamRankMap.put(team, new SITeamRank(team, START_RANK));
		}
		return teamRankMap.get(team);
	}

	public Set<SITeam> getTeams() {
		return teamRankMap.keySet();
	}

	public SITeamRank get(SITeam team) {
		return teamRankMap.get(team);
	}

	public Collection<SITeamRank> getTeamRanks() {
		return teamRankMap.values();
	}

	public void recordPlayed(SITeam team) {
		addNations(team);
		Rankable teamRank = getRank(team);
		teamRank.played();
	}
}
