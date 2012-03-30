package com.kenstevens.stratinit.wicket.unit;

import java.util.List;

import org.apache.wicket.model.LoadableDetachableModel;

public class PlayerUnitsModel extends LoadableDetachableModel<List<PlayerUnitCount>> {
	private static final long serialVersionUID = 1L;

	private final PlayerUnitsProvider playerUnitsProvider;

	private final int gameId;

	private final String username;

	public PlayerUnitsModel(PlayerUnitsProvider playerUnitsProvider, int gameId, String username) {
		this.playerUnitsProvider = playerUnitsProvider;
		this.gameId = gameId;
		this.username = username;
	}

	@Override
	protected List<PlayerUnitCount> load() {
		return playerUnitsProvider.getUnitsByNation(gameId, username);
	}

}
