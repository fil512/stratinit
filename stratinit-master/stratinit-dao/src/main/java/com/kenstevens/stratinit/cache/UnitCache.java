package com.kenstevens.stratinit.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.kenstevens.stratinit.model.Unit;

import javax.annotation.Nonnull;

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
