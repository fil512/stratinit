package com.kenstevens.stratinit.world.predicate;

import com.google.common.base.Predicate;
import com.kenstevens.stratinit.client.model.Sector;
import com.kenstevens.stratinit.type.Constants;

public class UnassignedPredicate implements Predicate<Sector> {
	@Override
	public boolean apply(Sector sector) {
		return sector.getIsland() == Constants.UNASSIGNED;
	}

}
