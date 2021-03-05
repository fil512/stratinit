package com.kenstevens.stratinit.world.predicate;

import com.google.common.base.Predicate;
import com.kenstevens.stratinit.client.model.Sector;
import com.kenstevens.stratinit.client.model.World;

public class UnassignedAndIsolatedPredicate implements Predicate<Sector> {
	private static final UnassignedPredicate UNASSIGNED_PREDICATE = new UnassignedPredicate();
	private final IsolatedPredicate isolatedPredicate;
/**
 * True if the sector is water and all land squares within minDistance are owned by islandId
 * @param world
 * @param minDistanceEntreIslands
 * @param islandId
 */
	public UnassignedAndIsolatedPredicate(World world, int minDistanceEntreIslands, int islandId) {
		isolatedPredicate = new IsolatedPredicate(world, minDistanceEntreIslands, islandId);
	}

	@Override
	public boolean apply(Sector element) {
		return UNASSIGNED_PREDICATE.apply(element) && isolatedPredicate.apply(element);
	}

}
