package com.kenstevens.stratinit.dto.news;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.type.NewsCategory;


public class SINewsNeutralConquest extends SINewsNation implements SINewsCountable {
	private static final long serialVersionUID = 1L;
	public int count;

	@JsonCreator
	public SINewsNeutralConquest(@JsonProperty("category") NewsCategory category, @JsonProperty("nationName") String nationName, @JsonProperty("count") int count) {
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
