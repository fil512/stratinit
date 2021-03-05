package com.kenstevens.stratinit.client.model;

import com.kenstevens.stratinit.dto.SITeam;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TeamList implements Iterable<SITeam> {
	private final List<SITeam>list = new ArrayList<SITeam>();

	public SITeam get(int id) {
		return list.get(id);
	}

	public int size() {
		return list.size();
	}

	public void addAll(List<SITeam> entries) {
		if (entries == null) {
			return;
		}

		list.clear();
		for (SITeam entry : entries) {
			list.add(entry);
		}
	}

	@Override
	public Iterator<SITeam> iterator() {
		return list.iterator();
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}
}
