package com.kenstevens.stratinit.rank;

import java.util.List;

import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.GameHistory;
import com.kenstevens.stratinit.model.TeamRank;

public class GameRanker {

	private final TeamHelper teamHelper;
	private final TeamRanks teamRanks = new TeamRanks();

	public GameRanker(TeamHelper teamHelper) {
		this.teamHelper = teamHelper;
	}

	public void rank(GameHistory game) {
		List<SITeam> teams = teamHelper.findTeams(game);
		rankTeams(teams);
	}
	
	public void rank(Game game) {
		List<SITeam> teams = teamHelper.findTeams(game);
		rankTeams(teams);
	}
	
	private void rankTeams(List<SITeam> teams) {
		if (teams.size() < 2) {
			return;
		}
		// two teams won.  ignore
		// FIXME test
		SITeam teamWin = teams.get(0);
		if (teamWin.score == teams.get(1).score) {
			return;
		}
		for (int i = 1; i < teams.size(); ++i) {
			teamRanks.recordWin(teamWin, teams.get(i));
		}
	}
	
	public List<TeamRank> getTeamRanks() {
		return teamRanks.getTeamRanks();
	}
}
