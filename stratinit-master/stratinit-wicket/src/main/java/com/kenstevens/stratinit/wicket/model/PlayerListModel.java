package com.kenstevens.stratinit.wicket.model;

import com.kenstevens.stratinit.client.model.Player;
import com.kenstevens.stratinit.wicket.provider.PlayerListProvider;
import org.apache.wicket.model.LoadableDetachableModel;

import java.util.List;

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
