package com.kenstevens.stratinit.world.predicate;

import java.util.function.Predicate;
import com.kenstevens.stratinit.client.model.Unit;

public class UnitDevistatesPredicate implements Predicate<Unit> {
	@Override
	public boolean test(Unit unit) {
		return unit.devastates();
	}
}
