package com.kenstevens.stratinit.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.kenstevens.stratinit.dto.SITeam;

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
