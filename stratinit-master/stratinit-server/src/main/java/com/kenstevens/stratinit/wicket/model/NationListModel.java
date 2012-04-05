package com.kenstevens.stratinit.wicket.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.model.LoadableDetachableModel;

import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.wicket.provider.GameListProvider;

public class NationListModel extends LoadableDetachableModel<List<SINation>>{
	
	private static final long serialVersionUID = 1L;

	private static Comparator<SINation> nationsByPowerComparator = new Comparator<SINation>() {
		@Override
		public int compare(SINation nation1, SINation nation2) {
			return Integer.valueOf(nation2.cities).compareTo(nation1.cities);
		}
	};

	private final GameListProvider gameListProvider;

	private final int gameId;

	public NationListModel(GameListProvider gameListProvider, int gameId) {
		this.gameListProvider = gameListProvider;
		this.gameId = gameId;
	}

	@Override
	protected List<SINation> load() {
		List<SINation> nationList = gameListProvider.getNations(gameId);
		Collections.sort(nationList, nationsByPowerComparator);
		return nationList;
	}

}
