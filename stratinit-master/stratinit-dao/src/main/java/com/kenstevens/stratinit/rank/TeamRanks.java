package com.kenstevens.stratinit.rank;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.model.PlayerRank;
import com.kenstevens.stratinit.model.Rankable;
import com.kenstevens.stratinit.model.TeamRank;
import com.kenstevens.stratinit.server.remote.rank.ELOCalculator;

public class TeamRanks {
	private static final double START_RANK = 1500;
	private Map<SITeam, TeamRank> teamRankMap = Maps.newHashMap();
	private Set<String> nations = Sets.newHashSet();
	private static final Comparator<Rankable> byRank = new Comparator<Rankable>() {

		@Override
		public int compare(Rankable rank1, Rankable rank2) {
			return rank2.getRank().compareTo(rank1.getRank());
		}
		
	};

	public void recordWin(SITeam winner, SITeam loser) {
		addNations(winner);
		addNations(loser);
		Rankable winRank = getRank(winner);
		Rankable loseRank = getRank(loser);
		ELOCalculator eloCalculator = new ELOCalculator(winRank.getRank(),
				loseRank.getRank());
		winRank.setRank(eloCalculator.getWinNewELO());
		loseRank.setRank(eloCalculator.getLoseNewELO());
		winRank.won();
		loseRank.lost();
	}

	private void addNations(SITeam team) {
		nations.add(team.getNation1());
		if (team.getNation2() != null) {
			nations.add(team.getNation2());
		}
		
	}

	public List<TeamRank> getTeamRanks() {
		List<TeamRank> teamRankList = Lists.newArrayList(teamRankMap.values());
		Collections.sort(teamRankList, byRank);
		return teamRankList;
	}

	private Rankable getRank(SITeam team) {
		if (!teamRankMap.containsKey(team)) {
			teamRankMap.put(team, new TeamRank(team, START_RANK));
		}
		return teamRankMap.get(team);
	}

	public List<PlayerRank> getPlayerRanks() {
		Multimap<String, SITeam> teamsByPlayer = getTeamsByPlayer();
		return calcPlayerRanks(teamsByPlayer);
	}

	private Multimap<String, SITeam> getTeamsByPlayer() {
		Multimap<String, SITeam> teamsByPlayer = HashMultimap.create();
		for (SITeam team : teamRankMap.keySet()) {
			teamsByPlayer.put(team.nation1, team);
			if (team.nation2 != null) {
				teamsByPlayer.put(team.nation2, team);
			}
		}
		return teamsByPlayer;
	}

	private List<PlayerRank> calcPlayerRanks(
			Multimap<String, SITeam> teamsByPlayer) {
		List<PlayerRank> playerRanks = Lists.newArrayList();
		for (String nation: teamsByPlayer.keySet()) {
			playerRanks.add(getPlayerRank(nation, teamsByPlayer.get(nation)));
		}
		Collections.sort(playerRanks, byRank);
		return playerRanks;
	}

	private PlayerRank getPlayerRank(String nation, Collection<SITeam> teams) {
		int played = 0;
		int wins = 0;
		double totalRank = 0.0;
		for (SITeam team : teams) {
			TeamRank teamRank = teamRankMap.get(team);
			played += teamRank.getPlayed();
			wins += teamRank.getWins();
			totalRank += teamRank.getRank() * teamRank.getPlayed();
		}
		
		double rank = played == 0 ? 0 : totalRank / played;
		return new PlayerRank(nation, rank, played, wins);
	}



}
 