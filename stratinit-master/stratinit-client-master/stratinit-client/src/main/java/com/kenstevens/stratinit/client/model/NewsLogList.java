package com.kenstevens.stratinit.client.model;

import com.kenstevens.stratinit.dto.news.SINewsLogsDay;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NewsLogList implements Iterable<SINewsLogsDay> {
	private final List<SINewsLogsDay>list = new ArrayList<SINewsLogsDay>();

	public SINewsLogsDay get(int id) {
		return list.get(id);
	}

	public int size() {
		return list.size();
	}

	public void addAll(List<SINewsLogsDay> entries) {
		if (entries == null) {
			return;
		}

		list.clear();
		for (SINewsLogsDay entry : entries) {
			list.add(entry);
		}
	}

	@Override
	public Iterator<SINewsLogsDay> iterator() {
		return list.iterator();
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}
}
