package com.kenstevens.stratinit.world.predicate;

import java.util.function.Predicate;
import com.kenstevens.stratinit.client.model.Sector;
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
	public boolean test(Sector sector) {
		return sector.getIsland() != Constants.UNASSIGNED && sector.getIsland() != islandId;
	}
}
