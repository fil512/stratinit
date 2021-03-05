package com.kenstevens.stratinit.world.predicate;

import com.google.common.base.Predicate;
import com.kenstevens.stratinit.client.model.WorldSector;

public class PortPredicate implements Predicate<WorldSector> {
	@Override
	public boolean apply(WorldSector worldSector) {
		return worldSector.isPort();
	}
}
