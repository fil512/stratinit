package com.kenstevens.stratinit.dto.news;

import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.type.NewsCategory;


public class SINewsOpponentConquest extends SINewsOpponents {
	private static final long serialVersionUID = 1L;
	public int count;

	public SINewsOpponentConquest(Nation nation, Nation opponent, int count) {
		super(nation, opponent, NewsCategory.CONQUEST);
		this.count = count;
	}

	public SINewsOpponentConquest(String nationName, String opponentName,
			int count) {
		super(nationName, opponentName, NewsCategory.CONQUEST);
		this.count = count;
	}

	public SINewsOpponentConquest reverse() {
		return new SINewsOpponentConquest(opponentName, nationName, count);
	}
}
