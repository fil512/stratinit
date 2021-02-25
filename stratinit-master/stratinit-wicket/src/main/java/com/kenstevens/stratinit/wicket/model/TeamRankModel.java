package com.kenstevens.stratinit.wicket.model;

import com.kenstevens.stratinit.dto.SITeamRank;
import com.kenstevens.stratinit.rank.TeamRanker;
import com.kenstevens.stratinit.wicket.provider.GameRankerProvider;
import org.apache.wicket.model.LoadableDetachableModel;

import java.util.List;

public class TeamRankModel extends LoadableDetachableModel<List<SITeamRank>> {
	private static final long serialVersionUID = 1L;

	private final GameRankerProvider gameRankerProvider;

	private final String playerName;

	public TeamRankModel(GameRankerProvider gameRankerProvider, String playerName) {
		this.gameRankerProvider = gameRankerProvider;
		this.playerName = playerName;
	}

	@Override
	protected List<SITeamRank> load() {
		TeamRanker gameRanker = gameRankerProvider.getGameRanker();
		return gameRanker.getTeamRanks(playerName);
	}

}
