package com.kenstevens.stratinit.news;

import java.util.Date;

import com.kenstevens.stratinit.type.NewsCategory;

public interface NewsWorthy {
	public Date getDate();
	public NewsCategory getNewsCategory();
}
