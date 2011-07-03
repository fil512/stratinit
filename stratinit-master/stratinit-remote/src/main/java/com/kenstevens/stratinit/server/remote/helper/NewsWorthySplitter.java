package com.kenstevens.stratinit.server.remote.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.news.NewsWorthy;
import com.kenstevens.stratinit.util.GameScheduleHelper;

public class NewsWorthySplitter<T extends NewsWorthy> {

	public Map<Integer, List<T>> split(Game game, List<T> list) {
		Map<Integer, List<T>> newsMap = new HashMap<Integer, List<T>>();
		for (T item : list) {
			int day = GameScheduleHelper.dateToDay(game, item.getDate());
			List<T> dayItems = newsMap.get(day);
			if (dayItems == null) {
				dayItems = new ArrayList<T>();
				newsMap.put(day, dayItems);
			}
			dayItems.add(item);
		}
		return newsMap;
	}

}
