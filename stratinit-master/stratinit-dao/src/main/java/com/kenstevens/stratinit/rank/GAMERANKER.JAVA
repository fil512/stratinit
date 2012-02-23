package com.kenstevens.stratinit.rank;

import java.util.List;
import java.util.SortedMap;

import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.model.Game;

public class GameRanker {

	private final TeamHelper teamHelper;
	private final PlayerRanks playerRanks = new PlayerRanks();

	public GameRanker(TeamHelper teamHelper) {
		this.teamHelper = teamHelper;
	}

	public void rank(Game game) {
		List<SITeam> teams = teamHelper.findTeams(game);
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
			playerRanks.recordWin(teamWin, teams.get(i));
		}
	}
	
	public SortedMap<String, Double> getRanks() {
		return playerRanks.getRanks();
	}
}
