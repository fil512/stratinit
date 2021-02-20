package com.kenstevens.stratinit.dto.news;

import com.google.common.collect.Lists;

import java.util.*;

public class SINewsLogs implements Iterable<SINewsLogsDay> {
	private final SortedMap<Integer, SINewsLogsDay> newsMap = new TreeMap<Integer, SINewsLogsDay>();

	public SINewsLogsDay get(int day) {
		SINewsLogsDay retval = newsMap.get(day);
		if (retval == null) {
			retval = new SINewsLogsDay(day);
			newsMap.put(day, retval);
		}
		return retval;
	}

	public List<SINewsLogsDay> getDays() {
		ArrayList<SINewsLogsDay> retval = Lists.newArrayList(newsMap.values());
		Collections.reverse(retval);
		return retval;
	}

	@Override
	public Iterator<SINewsLogsDay> iterator() {
		return newsMap.values().iterator();
	}
}
