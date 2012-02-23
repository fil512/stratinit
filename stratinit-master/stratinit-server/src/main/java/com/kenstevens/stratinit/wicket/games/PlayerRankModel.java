package com.kenstevens.stratinit.wicket.games;

import java.util.List;

import org.apache.wicket.model.LoadableDetachableModel;

import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.model.PlayerRank;

public class PlayerRankModel extends LoadableDetachableModel<List<PlayerRank>> {
	private static final long serialVersionUID = 1L;

	private final PlayerRankProvider playerRankProvider;

	public PlayerRankModel(PlayerRankProvider playerRankProvider) {
		this.playerRankProvider = playerRankProvider;
	}

	@Override
	protected List<PlayerRank> load() {
		return playerRankProvider.getPlayerRanks();
	}

}
