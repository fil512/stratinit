package com.kenstevens.stratinit.world.predicate;

import com.google.common.base.Predicate;
import com.kenstevens.stratinit.model.Unit;

public class UnitSuppliesUnitPredicate implements Predicate<Unit> {
	private final Unit targetUnit;

	public UnitSuppliesUnitPredicate(Unit targetUnit) {
		this.targetUnit = targetUnit;
	}

	@Override
	public boolean apply(Unit unit) {
		return unit != null && unit.supplies(targetUnit);
	}

}
