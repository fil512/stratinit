package com.kenstevens.stratinit.wicket.games;

import java.util.List;

import org.apache.wicket.model.LoadableDetachableModel;

import com.kenstevens.stratinit.model.Player;

public class PlayerListModel extends LoadableDetachableModel<List<Player>> {
	private static final long serialVersionUID = 1L;

	private final PlayerListProvider playerListProvider;

	public PlayerListModel(PlayerListProvider playerListProvider) {
		this.playerListProvider = playerListProvider;
	}

	@Override
	protected List<Player> load() {
		return playerListProvider.getPlayers();
	}

}
