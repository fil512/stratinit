package com.kenstevens.stratinit.world.predicate;

import java.util.function.Predicate;
import com.kenstevens.stratinit.client.model.Unit;

public class UnitSuppliesUnitPredicate implements Predicate<Unit> {
	private final Unit targetUnit;

	public UnitSuppliesUnitPredicate(Unit targetUnit) {
		this.targetUnit = targetUnit;
	}

	@Override
	public boolean test(Unit unit) {
		return unit != null && unit.supplies(targetUnit);
	}

}
