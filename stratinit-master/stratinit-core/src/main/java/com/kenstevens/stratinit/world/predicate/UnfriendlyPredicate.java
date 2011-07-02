package com.kenstevens.stratinit.world.predicate;

import com.google.common.base.Predicate;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.type.Constants;

public class UnfriendlyPredicate implements Predicate<Sector> {
	private final int islandId;

	/**
	 * true if sector is land and is not owned by islandId (i.e. me)
	 * @param islandId
	 */
	public UnfriendlyPredicate(int islandId) {
		this.islandId = islandId;
	}

	@Override
	public boolean apply(Sector sector) {
		return sector.getIsland() != Constants.UNASSIGNED && sector.getIsland() != islandId;
	}
}
