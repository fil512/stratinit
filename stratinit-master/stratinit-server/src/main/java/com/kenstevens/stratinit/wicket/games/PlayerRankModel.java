package com.kenstevens.stratinit.wicket.games;

import java.util.List;

import org.apache.wicket.model.LoadableDetachableModel;

import com.kenstevens.stratinit.model.PlayerRank;
import com.kenstevens.stratinit.rank.GameRanker;

public class PlayerRankModel extends LoadableDetachableModel<List<PlayerRank>> {
	private static final long serialVersionUID = 1L;

	private final GameRankerProvider gameRankerProvider;

	public PlayerRankModel(GameRankerProvider gameRankerProvider) {
		this.gameRankerProvider = gameRankerProvider;
	}

	@Override
	protected List<PlayerRank> load() {
		GameRanker gameRanker = gameRankerProvider.getGameRanker();
		return gameRanker.getPlayerRanks();
	}

}
