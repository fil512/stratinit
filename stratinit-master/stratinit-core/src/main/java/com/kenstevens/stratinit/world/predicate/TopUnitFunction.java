package com.kenstevens.stratinit.world.predicate;

import com.google.common.base.Function;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.model.WorldSector;

public class TopUnitFunction implements Function<WorldSector, Unit> {
	@Override
	public Unit apply(WorldSector worldSector) {
		return worldSector.getTopUnit();
	}
}
