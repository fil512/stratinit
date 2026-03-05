package com.kenstevens.stratinit.cache;

import com.kenstevens.stratinit.client.model.Unit;

import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class UnitCache extends Cacheable {
	private final Map<Integer, Unit> unitMap = new ConcurrentSkipListMap<>();

	@Nonnull
	public List<Unit> getUnits() {
		return new ArrayList<>(unitMap.values());
	}

	public void add(Unit unit) {
		unitMap.put(unit.getId(), unit);
	}

	public void remove(Unit unit) {
		unitMap.remove(unit.getId());
	}
}
