package com.kenstevens.stratinit.dto.news;

import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.type.NewsCategory;


public class SINewsNeutralConquest extends SINewsNation {
	private static final long serialVersionUID = 1L;
	public int count;

	public SINewsNeutralConquest(Nation nation, int count) {
		super(nation, NewsCategory.CONQUEST);
		this.count = count;
	}

	public SINewsNeutralConquest(String nationName, int count) {
		super(nationName, NewsCategory.CONQUEST);
		this.count = count;
	}
}
