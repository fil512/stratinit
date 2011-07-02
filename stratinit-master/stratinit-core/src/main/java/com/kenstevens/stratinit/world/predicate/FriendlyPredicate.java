package com.kenstevens.stratinit.world.predicate;

import com.google.common.base.Predicate;
import com.kenstevens.stratinit.model.Sector;

public class FriendlyPredicate implements Predicate<Sector> {
	private final int islandId;
	private static final UnassignedPredicate UNASSIGNED_PREDICATE = new UnassignedPredicate();

	public FriendlyPredicate(int islandId) {
		this.islandId = islandId;
	}

	@Override
	public boolean apply(Sector sector) {
		return UNASSIGNED_PREDICATE.apply(sector) || sector.getIsland() == islandId;
	}
}
