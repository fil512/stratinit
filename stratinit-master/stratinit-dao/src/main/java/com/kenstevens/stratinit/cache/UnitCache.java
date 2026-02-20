package com.kenstevens.stratinit.cache;

import com.kenstevens.stratinit.client.model.Unit;

import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnitCache extends Cacheable {
	private final List<Unit> units = new ArrayList<Unit>();

	@Nonnull
	public List<Unit> getUnits() {
		return Collections.unmodifiableList(units);
	}

	public void add(Unit unit) {
		units.add(unit);
	}

	public void remove(Unit unit) {
		units.remove(unit);
	}
}
