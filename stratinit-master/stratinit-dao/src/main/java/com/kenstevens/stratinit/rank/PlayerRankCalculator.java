package com.kenstevens.stratinit.rank;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.kenstevens.stratinit.client.model.PlayerRank;
import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.dto.SITeamRank;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PlayerRankCalculator {

	private final TeamRankMap teamRankMap;

	public PlayerRankCalculator(TeamRankMap teamRankMap) {
		this.teamRankMap = teamRankMap;
	}

	public List<PlayerRank> calcPlayerRanks(
			Multimap<String, SITeam> teamsByPlayer) {
		List<PlayerRank> playerRanks = Lists.newArrayList();
		for (String nation: teamsByPlayer.keySet()) {
			playerRanks.add(getPlayerRank(nation, teamsByPlayer.get(nation)));
		}
		Collections.sort(playerRanks, TeamRanks.BY_RANK);
		return playerRanks;
	}
	
	private PlayerRank getPlayerRank(String nation, Collection<SITeam> teams) {
		int victories = 0;
		int opponents = 0;
		int wins = 0;
		int played = 0;
		double totalRank = 0.0;
		for (SITeam team : teams) {
			SITeamRank sITeamRank = teamRankMap.get(team);
			victories += sITeamRank.getVictories();
			opponents += sITeamRank.getOpponents();
			wins += sITeamRank.getWins();
			played += sITeamRank.getPlayed();
			totalRank += sITeamRank.getRank() * sITeamRank.getVictories();
		}
		
		double rank = victories == 0 ? 0 : totalRank / victories;
		return new PlayerRank(nation, rank, victories, opponents, wins, played);
	}
	

}
