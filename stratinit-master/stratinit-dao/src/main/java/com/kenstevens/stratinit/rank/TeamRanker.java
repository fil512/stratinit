package com.kenstevens.stratinit.rank;

import java.util.List;

import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.dto.SITeamRank;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.GameHistory;
import com.kenstevens.stratinit.model.PlayerRank;

public class TeamRanker {

	private final TeamHelper teamHelper;
	private final TeamRanks teamRanks = new TeamRanks();

	public TeamRanker(TeamHelper teamHelper) {
		this.teamHelper = teamHelper;
	}

	public void rank(GameHistory game) {
		List<SITeam> teams = teamHelper.findTeams(game);
		teamRanks.rankTeams(teams);
	}
	
	public void rank(Game game) {
		List<SITeam> teams = teamHelper.findTeams(game);
		teamRanks.rankTeams(teams);
	}
	

	
	public List<SITeamRank> getTeamRanks() {
		return teamRanks.getTeamRanks();
	}

	public List<PlayerRank> getPlayerRanks() {
		return teamRanks.getPlayerRanks();		
	}

	public List<SITeamRank> getTeamRanks(String playerName) {
		return teamRanks.getTeamRanks(playerName);
	}
}
