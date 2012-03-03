package com.kenstevens.stratinit.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SatelliteList implements Iterable<LaunchedSatellite> {
	private List<LaunchedSatellite>list = new ArrayList<LaunchedSatellite>();

	public void clear() {
		list.clear();
	}

	public LaunchedSatellite get(int id) {
		return list.get(id);
	}
	
	public void addAll(List<LaunchedSatellite> entries) {
		if (list == null) {
			return;
		}
		for (LaunchedSatellite entry : entries) {
			list.add(entry);
		}
	}

	public int size() {
		return list.size();
	}

	@Override
	public Iterator<LaunchedSatellite> iterator() {
		return list.iterator();
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}
}
