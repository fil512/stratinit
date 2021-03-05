package com.kenstevens.stratinit.wicket.model;

import com.kenstevens.stratinit.client.model.PlayerRank;
import com.kenstevens.stratinit.rank.TeamRanker;
import com.kenstevens.stratinit.wicket.provider.GameRankerProvider;
import org.apache.wicket.model.LoadableDetachableModel;

import java.util.List;

public class PlayerRankModel extends LoadableDetachableModel<List<PlayerRank>> {
	private static final long serialVersionUID = 1L;

	private final GameRankerProvider gameRankerProvider;

	public PlayerRankModel(GameRankerProvider gameRankerProvider) {
		this.gameRankerProvider = gameRankerProvider;
	}

	@Override
	protected List<PlayerRank> load() {
		TeamRanker gameRanker = gameRankerProvider.getGameRanker();
		return gameRanker.getPlayerRanks();
	}

}
