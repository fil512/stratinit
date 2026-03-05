package com.kenstevens.stratinit.cache;

import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.model.UnitSeen;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class UnitSeenCache extends Cacheable {
	private final Map<Integer, UnitSeen> unitSeenMap = new ConcurrentHashMap<>();

	public void add(UnitSeen unitSeen) {
		unitSeenMap.put(unitSeen.getUnit().getId(), unitSeen);
	}

	public UnitSeen get(int unitId) {
		return unitSeenMap.get(unitId);
	}

	public Collection<Unit> getUnitSeenUnits() {
		return new ArrayList<>(unitSeenMap.values()).stream()
				.map(UnitSeen::getUnit)
				.collect(Collectors.toList());
	}

	public Collection<UnitSeen> getUnitsSeen() {
		return new ArrayList<>(unitSeenMap.values());
	}

	public void remove(Unit unit) {
		unitSeenMap.remove(unit.getId());
	}
}
