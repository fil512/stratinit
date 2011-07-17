package com.kenstevens.stratinit.wicket.games;

import java.util.List;

import org.apache.wicket.model.LoadableDetachableModel;


public class GameListModel extends LoadableDetachableModel<List<GameTable>> {
	private static final long serialVersionUID = 1L;

	private final GamesPage gamesPage;

	public GameListModel(GamesPage gamesPage) {
		this.gamesPage = gamesPage;
	}

	@Override
	protected List<GameTable> load() {
		return gamesPage.getGames();
	}

}
