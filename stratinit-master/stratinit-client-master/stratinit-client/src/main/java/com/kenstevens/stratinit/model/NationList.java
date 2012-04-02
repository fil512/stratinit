package com.kenstevens.stratinit.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.kenstevens.stratinit.type.RelationType;

public class NationList implements Iterable<NationView> {
	private Map<Integer, NationView> idMap = new Hashtable<Integer, NationView>();
	private Map<String, NationView> nameMap = new Hashtable<String, NationView>();
	private static final Comparator<NationView> BY_CITIES = new Comparator<NationView>() {
		public int compare(NationView p1, NationView p2) {
			return Integer.valueOf(p2.getPower())
			.compareTo(p1.getPower());
		}
	};

	private void add(NationView nation) {
		NationView newNation = nation;
		if (idMap.get(nation.getNationId()) != null) {
			NationView orig = idMap.get(nation.getNationId());
			newNation = orig.copyFrom(nation);
		}
		set(newNation);
	}

	private void set(NationView nation) {
		idMap.put(nation.getNationId(), nation);
		nameMap.put(nation.getName(), nation);
	}

	public NationView getNation(int id) {
		return idMap.get(id);
	}

	public void addAll(List<NationView> entries) {
		for (NationView entry : entries) {
			add(entry);
		}
	}

	public int size() {
		return idMap.size();
	}

	@Override
	public Iterator<NationView> iterator() {
		List<NationView> list = new ArrayList<NationView>(idMap.values());

		Collections.sort(list, BY_CITIES);
		return list.iterator();
	}

	public boolean isEmpty() {
		return idMap.isEmpty();
	}

	// TODO REF remove this
	public NationView getNation(String playerName) {
		return nameMap.get(playerName);
	}

	public String[] getPlayerNames() {
		return nameMap.keySet().toArray(new String[0]);
	}

	public NationView getAlly() {
		NationView retval = null;
		for (NationView nation : this) {
			Relation myRelation = nation.getMyRelation();
			Relation theirRelation = nation.getTheirRelation();
			if (myRelation != null && myRelation.getType() == RelationType.ALLIED &&
					theirRelation != null && theirRelation.getType() == RelationType.ALLIED) {
				retval = nation;
				break;
			}
		}
		return retval;
	}
}
