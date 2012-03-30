package com.kenstevens.stratinit.wicket.rank;

import java.util.List;

import org.apache.wicket.model.LoadableDetachableModel;

import com.kenstevens.stratinit.model.PlayerRank;
import com.kenstevens.stratinit.rank.TeamRanker;
import com.kenstevens.stratinit.wicket.game.GameRankerProvider;

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
