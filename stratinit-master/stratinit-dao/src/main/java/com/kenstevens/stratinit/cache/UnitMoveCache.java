package com.kenstevens.stratinit.cache;

import com.kenstevens.stratinit.client.model.UnitMove;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UnitMoveCache extends Cacheable {
	private final Map<Integer, UnitMove> unitMoveMap = new ConcurrentHashMap<>();

	public void add(UnitMove unitMove) {
		unitMoveMap.put(unitMove.getUnit().getId(), unitMove);
	}

	public UnitMove get(int unitId) {
		return unitMoveMap.get(unitId);
	}

	public Collection<UnitMove> getUnitsMove() {
		return new ArrayList<>(unitMoveMap.values());
	}

	public void remove(int id) {
		unitMoveMap.remove(id);
	}
}
