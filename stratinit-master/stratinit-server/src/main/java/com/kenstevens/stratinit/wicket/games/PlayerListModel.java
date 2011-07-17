package com.kenstevens.stratinit.wicket.games;

import java.util.List;

import org.apache.wicket.model.LoadableDetachableModel;

import com.kenstevens.stratinit.model.Player;

public class PlayerListModel extends LoadableDetachableModel<List<Player>> {
	private static final long serialVersionUID = 1L;

	private final LeaderBoardPage leaderBoardPage;

	public PlayerListModel(LeaderBoardPage leaderBoardPage) {
		this.leaderBoardPage = leaderBoardPage;
	}

	@Override
	protected List<Player> load() {
		return leaderBoardPage.getPlayers();
	}

}
