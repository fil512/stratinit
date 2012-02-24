package com.kenstevens.stratinit.rank;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.model.TeamRank;
import com.kenstevens.stratinit.server.remote.rank.ELOCalculator;

public class TeamRanks {
	private static final double START_RANK = 1500;
	private Map<SITeam, TeamRank> teamRank = Maps.newHashMap();
	private static final Comparator<TeamRank> byRank = new Comparator<TeamRank>() {

		@Override
		public int compare(TeamRank teamRank1, TeamRank teamRank2) {
			return teamRank2.getRank().compareTo(teamRank1.getRank());
		}
		
	};

	public void recordWin(SITeam winner, SITeam loser) {
		TeamRank winRank = getRank(winner);
		TeamRank loseRank = getRank(loser);
		ELOCalculator eloCalculator = new ELOCalculator(winRank.getRank(),
				loseRank.getRank());
		winRank.setRank(eloCalculator.getWinNewELO());
		loseRank.setRank(eloCalculator.getLoseNewELO());
		winRank.incrementGameCount();
		loseRank.incrementGameCount();
	}

	public List<TeamRank> getTeamRanks() {
		List<TeamRank> teamRanks = Lists.newArrayList(teamRank.values());
		Collections.sort(teamRanks, byRank);
		return teamRanks;
	}

	private TeamRank getRank(SITeam team) {
		if (!teamRank.containsKey(team)) {
			teamRank.put(team, new TeamRank(team, START_RANK));
		}
		return teamRank.get(team);
	}

}
