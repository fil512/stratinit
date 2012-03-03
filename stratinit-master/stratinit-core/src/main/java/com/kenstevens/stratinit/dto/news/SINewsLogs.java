package com.kenstevens.stratinit.dto.news;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.common.collect.Lists;

public class SINewsLogs implements Iterable<SINewsLogsDay> {
	private SortedMap<Integer, SINewsLogsDay> newsMap = new TreeMap<Integer, SINewsLogsDay>();

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
