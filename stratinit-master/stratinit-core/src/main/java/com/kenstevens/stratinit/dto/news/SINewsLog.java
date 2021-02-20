package com.kenstevens.stratinit.dto.news;

import com.kenstevens.stratinit.dto.StratInitDTO;
import com.kenstevens.stratinit.news.NewsWorthy;
import com.kenstevens.stratinit.type.NewsCategory;

public abstract class SINewsLog implements StratInitDTO {
	private static final long serialVersionUID = 1L;

	public final NewsCategory category;

	public SINewsLog(NewsWorthy newsWorthy) {
		this.category = newsWorthy.getNewsCategory();
	}

	public SINewsLog(NewsCategory category) {
		this.category = category;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((category == null) ? 0 : category.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SINewsLog other = (SINewsLog) obj;
		if (category == null) {
			return other.category == null;
		} else return category.equals(other.category);
	}
}
