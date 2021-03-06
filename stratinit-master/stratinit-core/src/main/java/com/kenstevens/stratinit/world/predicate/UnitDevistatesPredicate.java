package com.kenstevens.stratinit.world.predicate;

import com.google.common.base.Predicate;
import com.kenstevens.stratinit.client.model.Unit;

public class UnitDevistatesPredicate implements Predicate<Unit> {
	@Override
	public boolean apply(Unit unit) {
		return unit.devastates();
	}
}
