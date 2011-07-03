package com.kenstevens.stratinit.world.predicate;

import com.google.common.base.Function;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitSeen;

public class UnitSeenToUnitFunction implements
		Function<UnitSeen, Unit> {

	@Override
	public Unit apply(UnitSeen unitSeen) {
		return unitSeen.getUnit();
	}

}
