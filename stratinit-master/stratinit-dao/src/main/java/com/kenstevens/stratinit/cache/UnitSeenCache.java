package com.kenstevens.stratinit.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitSeen;

public class UnitSeenCache extends Cacheable {
	private final Map<Integer, UnitSeen> unitSeenMap = new HashMap<Integer, UnitSeen>();

	public void add(UnitSeen unitSeen) {
		unitSeenMap.put(unitSeen.getUnit().getId(), unitSeen);
	}

	public UnitSeen get(int unitId) {
		return unitSeenMap.get(unitId);
	}

	public Collection<Unit> getUnitSeenUnits() {
		return Collections2.transform(unitSeenMap.values(), new Function<UnitSeen, Unit>() {
			@Override
			public Unit apply(UnitSeen unitSeen) {
				return unitSeen.getUnit();
			}
		});
	}

	public Collection<UnitSeen> getUnitsSeen() {
		return unitSeenMap.values();
	}

	public void remove(Unit unit) {
		unitSeenMap.remove(unit.getId());
	}
}
