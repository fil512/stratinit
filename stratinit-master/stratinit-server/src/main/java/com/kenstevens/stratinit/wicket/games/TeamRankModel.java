package com.kenstevens.stratinit.wicket.games;

import java.util.List;

import org.apache.wicket.model.LoadableDetachableModel;

import com.kenstevens.stratinit.model.TeamRank;
import com.kenstevens.stratinit.rank.TeamRanker;

public class TeamRankModel extends LoadableDetachableModel<List<TeamRank>> {
	private static final long serialVersionUID = 1L;

	private final GameRankerProvider gameRankerProvider;

	private final String playerName;

	public TeamRankModel(GameRankerProvider gameRankerProvider, String playerName) {
		this.gameRankerProvider = gameRankerProvider;
		this.playerName = playerName;
	}

	@Override
	protected List<TeamRank> load() {
		TeamRanker gameRanker = gameRankerProvider.getGameRanker();
		return gameRanker.getTeamRanks(playerName);
	}

}
