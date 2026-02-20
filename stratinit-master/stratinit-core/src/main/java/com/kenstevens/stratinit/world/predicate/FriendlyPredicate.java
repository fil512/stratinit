package com.kenstevens.stratinit.world.predicate;

import java.util.function.Predicate;
import com.kenstevens.stratinit.client.model.Sector;

public class FriendlyPredicate implements Predicate<Sector> {
	private final int islandId;
	private static final UnassignedPredicate UNASSIGNED_PREDICATE = new UnassignedPredicate();

	public FriendlyPredicate(int islandId) {
		this.islandId = islandId;
	}

	@Override
	public boolean test(Sector sector) {
		return UNASSIGNED_PREDICATE.test(sector) || sector.getIsland() == islandId;
	}
}
