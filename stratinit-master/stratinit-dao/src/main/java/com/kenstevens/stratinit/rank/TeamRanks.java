package com.kenstevens.stratinit.rank;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.model.PlayerRank;
import com.kenstevens.stratinit.model.Rankable;
import com.kenstevens.stratinit.model.TeamRank;

public class TeamRanks {

	private final TeamRankMap teamRankMap = new TeamRankMap();

	public static final Comparator<Rankable> BY_RANK = new Comparator<Rankable>() {

		@Override
		public int compare(Rankable rank1, Rankable rank2) {
			if (rank2.getRank().equals(rank1.getRank())) {
				return rank1.getName().compareTo(rank2.getName());
			}
			return rank2.getRank().compareTo(rank1.getRank());
		}
		
	};


	public List<PlayerRank> getPlayerRanks() {
		Multimap<String, SITeam> teamsByPlayer = getTeamsByPlayer();
		return new PlayerRankCalculator(teamRankMap).calcPlayerRanks(teamsByPlayer);
	}

	private Multimap<String, SITeam> getTeamsByPlayer() {
		Multimap<String, SITeam> teamsByPlayer = HashMultimap.create();
		for (SITeam team : teamRankMap.getTeams()) {
			teamsByPlayer.put(team.nation1, team);
			if (team.nation2 != null) {
				teamsByPlayer.put(team.nation2, team);
			}
		}
		return teamsByPlayer;
	}




	public List<TeamRank> getTeamRanks() {
		List<TeamRank> teamRankList = Lists.newArrayList(teamRankMap.getTeamRanks());
		Collections.sort(teamRankList, BY_RANK);
		return teamRankList;
	}

	public void rankTeams(List<SITeam> teams) {
		if (teams.size() < 2) {
			return;
		}
		for (SITeam team : teams) {
			teamRankMap.recordPlayed(team);
		}
		TeamGroup teamGroup = new TeamGroup(teams);
		for (SITeam winner : teamGroup.winners()) {
			teamRankMap.recordWin(winner);
			for (SITeam loser : teamGroup.losers()) {
				teamRankMap.recordVictory(winner, loser);
			}
		}
	}

	public List<TeamRank> getTeamRanks(final String playerName) {
		List<TeamRank> teamRanks = getTeamRanks();
		if (playerName == null) {
			return teamRanks;
		}
		return Lists.newArrayList(Collections2.filter(teamRanks, new Predicate<TeamRank>() {

			@Override
			public boolean apply(TeamRank teamRank) {
				return teamRank.contains(playerName);
			}
			
		}));
	}

}
 