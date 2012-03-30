package com.kenstevens.stratinit.wicket.game;

import java.util.List;

import org.apache.wicket.model.LoadableDetachableModel;

import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.wicket.player.PlayerListProvider;

public class PlayerListModel extends LoadableDetachableModel<List<Player>> {
	private static final long serialVersionUID = 1L;

	private final PlayerListProvider playerListProvider;

	private final boolean admin;

	public PlayerListModel(PlayerListProvider playerListProvider, boolean admin) {
		this.playerListProvider = playerListProvider;
		this.admin = admin;
	}

	@Override
	protected List<Player> load() {
		if (isAdmin()) {
			return playerListProvider.getPlayersByDate();
		} else {
			return playerListProvider.getPlayersByWon();
		}
	}

	public boolean isAdmin() {
		return admin;
	}

}
