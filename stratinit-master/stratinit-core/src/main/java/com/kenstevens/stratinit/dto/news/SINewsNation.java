package com.kenstevens.stratinit.dto.news;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.news.NewsWorthy;
import com.kenstevens.stratinit.type.NewsCategory;

public abstract class SINewsNation extends SINewsLog {
	private static final long serialVersionUID = 1L;

	public final String nationName;

	public SINewsNation(Nation nation, NewsWorthy newsWorthy) {
		super(newsWorthy);
		this.nationName = nation.getName();
	}

	public SINewsNation(Nation nation, NewsCategory category) {
		super(category);
		this.nationName = nation.getName();
	}

	public SINewsNation(String nationName, NewsCategory category) {
		super(category);
		this.nationName = nationName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((nationName == null) ? 0 : nationName.hashCode());
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
		SINewsNation other = (SINewsNation) obj;
		if (nationName == null) {
			return other.nationName == null;
		} else return nationName.equals(other.nationName);
	}


}
