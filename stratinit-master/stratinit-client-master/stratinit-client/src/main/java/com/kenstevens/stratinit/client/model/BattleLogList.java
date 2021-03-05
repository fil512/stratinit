package com.kenstevens.stratinit.client.model;

import java.util.*;

// TODO REF consolidate lists with generics (last time i tried it broke serialization)
public class BattleLogList  implements Iterable<BattleLogEntry> {
	private final List<BattleLogEntry>list = new ArrayList<BattleLogEntry>();
	private final Map<Integer, BattleLogEntry> map = new Hashtable<Integer, BattleLogEntry>();

	public BattleLogEntry get(int id) {
		return list.get(id);
	}

	private void add(BattleLogEntry entry) {
		Integer key = entry.getId();
		BattleLogEntry orig = map.get(key);
		if (orig == null) {
			list.add(entry);
			map.put(key, entry);
		}
	}
	
	public int size() {
		return list.size();
	}

	public void addAll(List<BattleLogEntry> entries) {
		if (list == null) {
			return;
		}
		for (BattleLogEntry entry : entries) {
			add(entry);
		}
		sort();
	}
	
	private void sort() {
		Comparator<BattleLogEntry> byDate = new Comparator<BattleLogEntry>() {
			public int compare(BattleLogEntry e1, BattleLogEntry e2) {
				return e2.getDate().compareTo(e1.getDate());
			}
		};
		Collections.sort(list, byDate);
	}

	@Override
	public Iterator<BattleLogEntry> iterator() {
		return list.iterator();
	}
	
	public boolean isEmpty() {
		return map.isEmpty();
	}
}
