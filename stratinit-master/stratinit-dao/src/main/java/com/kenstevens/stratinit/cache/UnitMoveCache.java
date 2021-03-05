package com.kenstevens.stratinit.cache;

import com.google.common.collect.Maps;
import com.kenstevens.stratinit.client.model.UnitMove;

import java.util.Collection;
import java.util.Map;

public class UnitMoveCache extends Cacheable {
	private final Map<Integer, UnitMove> unitMoveMap = Maps.newHashMap();

	public void add(UnitMove unitMove) {
		unitMoveMap.put(unitMove.getUnit().getId(), unitMove);
	}

	public UnitMove get(int unitId) {
		return unitMoveMap.get(unitId);
	}

	public Collection<UnitMove> getUnitsMove() {
		return unitMoveMap.values();
	}

	public void remove(int id) {
		unitMoveMap.remove(id);
	}
}
