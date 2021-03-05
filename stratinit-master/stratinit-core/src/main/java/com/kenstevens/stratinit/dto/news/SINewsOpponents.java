package com.kenstevens.stratinit.dto.news;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.news.NewsWorthy;
import com.kenstevens.stratinit.type.NewsCategory;


public class SINewsOpponents extends SINewsNation {
	private static final long serialVersionUID = 1L;
	public final String opponentName;

	public SINewsOpponents(Nation nation, Nation opponent, NewsWorthy newsWorthy) {
		super(nation, newsWorthy);
		this.opponentName = opponent.getName();
	}

	public SINewsOpponents(Nation nation, Nation opponent, NewsCategory category) {
		super(nation, category);
		this.opponentName = opponent.getName();
	}

	public SINewsOpponents(String nationName, String opponentName, NewsCategory category) {
		super(nationName, category);
		this.opponentName = opponentName;
	}

	public SINewsOpponents(SINewsOpponents siNewsOpponents) {
		super(siNewsOpponents.nationName, siNewsOpponents.category);
		this.opponentName = siNewsOpponents.opponentName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((opponentName == null) ? 0 : opponentName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SINewsOpponents other = (SINewsOpponents) obj;
		if (opponentName == null) {
			return other.opponentName == null;
		} else return opponentName.equals(other.opponentName);
	}
}
