package com.kenstevens.stratinit.rank;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.model.PlayerRank;
import com.kenstevens.stratinit.server.remote.rank.ELOCalculator;

public class PlayerRanks {
	private static final double START_RANK = 1500;
	private Map<String, Double> teamRank = Maps.newHashMap();
	private static final Comparator<PlayerRank> byRank = new Comparator<PlayerRank>() {

		@Override
		public int compare(PlayerRank playerRank1, PlayerRank playerRank2) {
			return playerRank2.getRank().compareTo(playerRank1.getRank());
		}
		
	};

	public void recordWin(SITeam winner, SITeam loser) {
		double winStartRank = getRank(winner);
		double loseStartRank = getRank(loser);
		ELOCalculator eloCalculator = new ELOCalculator(winStartRank,
				loseStartRank);
		teamRank.put(winner.getName(), eloCalculator.getWinNewELO());
		teamRank.put(loser.getName(), eloCalculator.getLoseNewELO());
	}

	public List<PlayerRank> getRanks() {
		List<PlayerRank> playerRanks = Lists.newArrayList(Collections2.transform(teamRank.entrySet(),
				new Function<Entry<String, Double>, PlayerRank>() {

					@Override
					public PlayerRank apply(Entry<String, Double> entry) {
						return new PlayerRank(entry.getKey(), entry.getValue());
					}

				}));
		Collections.sort(playerRanks, byRank);
		return playerRanks;
	}

	private double getRank(SITeam team) {
		String name = team.getName();
		if (!teamRank.containsKey(name)) {
			teamRank.put(name, START_RANK);
		}
		return teamRank.get(name);
	}

}
