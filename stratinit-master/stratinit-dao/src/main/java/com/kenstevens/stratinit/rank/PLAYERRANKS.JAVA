package com.kenstevens.stratinit.rank;

import java.util.Map;
import java.util.SortedMap;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.server.remote.rank.ELOCalculator;

public class PlayerRanks {
	private static final double START_RANK = 1500;
	private Map<String, Double> teamRank = Maps.newTreeMap();
	
	public void recordWin(SITeam winner, SITeam loser) {
		double winStartRank = getRank(winner);
		double loseStartRank = getRank(loser);
		ELOCalculator eloCalculator = new ELOCalculator(winStartRank, loseStartRank);
		teamRank.put(winner.getName(), eloCalculator.getWinNewELO());
		teamRank.put(loser.getName(), eloCalculator.getLoseNewELO());
	}
	
	public SortedMap<String, Double> getRanks() {
		Ordering<String> valueComparator = Ordering.natural().reverse().onResultOf(Functions.forMap(teamRank));
		return ImmutableSortedMap.copyOf(teamRank, valueComparator);
	}

	private double getRank(SITeam team) {
		String name = team.getName();
		if (!teamRank.containsKey(name)) {
			teamRank.put(name, START_RANK);
		}
		return teamRank.get(name);
	}

}
