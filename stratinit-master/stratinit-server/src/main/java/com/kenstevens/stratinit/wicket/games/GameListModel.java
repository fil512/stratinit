package com.kenstevens.stratinit.wicket.games;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.model.LoadableDetachableModel;


public class GameListModel extends LoadableDetachableModel<List<GameTable>> {
	private static final long serialVersionUID = 1L;

	private final GameListProvider gameListProvider;

	public GameListModel(GameListProvider gameListProvider) {
		this.gameListProvider = gameListProvider;
	}

	@Override
	protected List<GameTable> load() {
		List<GameTable> gameList = gameListProvider.getGameTableList();
		Collections.sort(gameList, gameByIdComparator);
		return gameList;
	}

	private static Comparator<GameTable> gameByIdComparator = new Comparator<GameTable>() {
		@Override
		public int compare(GameTable game1, GameTable game2) {
			return Integer.valueOf(game2.getId()).compareTo(game1.getId());
		}
	};

}
