package com.kenstevens.stratinit.dto.news;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.type.NewsCategory;


public class SINewsNeutralConquest extends SINewsNation implements SINewsCountable {
	private static final long serialVersionUID = 1L;
	public int count;

	public SINewsNeutralConquest() {
	}

	public SINewsNeutralConquest(NewsCategory category, String nationName, int count) {
		super(nationName, category);
		this.count = count;
	}

	public SINewsNeutralConquest(Nation nation, int count) {
		super(nation, NewsCategory.CONQUEST);
		this.count = count;
	}


	@Override
	public void increment(int count) {
		this.count += count;
	}

	@Override
	public int getCount() {
		return count;
	}
}
