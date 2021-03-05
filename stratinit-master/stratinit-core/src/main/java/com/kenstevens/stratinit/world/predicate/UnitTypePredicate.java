package com.kenstevens.stratinit.world.predicate;

import com.google.common.base.Predicate;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.type.UnitType;

public class UnitTypePredicate implements Predicate<Unit> {
	private final UnitType unitType;

	public UnitTypePredicate(UnitType unitType) {
		this.unitType = unitType;
	}

	@Override
	public boolean apply(Unit unit) {
		return unit.getType() == unitType;
	}
}
