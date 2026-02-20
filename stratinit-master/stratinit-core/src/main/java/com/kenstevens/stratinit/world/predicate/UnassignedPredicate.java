package com.kenstevens.stratinit.world.predicate;

import java.util.function.Predicate;
import com.kenstevens.stratinit.client.model.Sector;
import com.kenstevens.stratinit.type.Constants;

public class UnassignedPredicate implements Predicate<Sector> {
	@Override
	public boolean test(Sector sector) {
		return sector.getIsland() == Constants.UNASSIGNED;
	}

}
