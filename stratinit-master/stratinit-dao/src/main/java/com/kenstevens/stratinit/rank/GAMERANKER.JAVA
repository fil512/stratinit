package com.kenstevens.stratinit.rank;

import java.util.List;
import java.util.Map.Entry;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.GameHistory;
import com.kenstevens.stratinit.model.PlayerRank;

public class GameRanker {

	private final TeamHelper teamHelper;
	private final PlayerRanks playerRanks = new PlayerRanks();

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
			playerRanks.recordWin(teamWin, teams.get(i));
		}
	}
	
	public List<PlayerRank> getRanks() {
		return Lists.newArrayList(Collections2.transform(playerRanks.getRanks().entrySet(), new Function<Entry<String, Double>, PlayerRank>() {

			@Override
			public PlayerRank apply(Entry<String, Double> entry) {
				return new PlayerRank(entry.getKey(), entry.getValue());
			}

			
		}));
	}
}
