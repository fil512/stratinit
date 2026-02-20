package com.kenstevens.stratinit.world.predicate;

import java.util.function.Predicate;
import com.kenstevens.stratinit.client.model.Sector;
import com.kenstevens.stratinit.client.model.World;

public class IsolatedPredicate implements Predicate<Sector> {
	private final World world;
	private final int distance;
	private final Predicate<Sector> unfriendlyPredicate;
/**
 * true if sector has no land sectors owned other players (i.e. owner is not islandId) within distance squares
 * @param world
 * @param distance
 * @param islandId
 */
	public IsolatedPredicate(World world, int distance, int islandId) {
		this.world = world;
		this.distance = distance;
		this.unfriendlyPredicate = new UnfriendlyPredicate(islandId);
	}

	@Override
	public boolean test(Sector element) {
		return world.getNeighbours(element, distance).stream().noneMatch(unfriendlyPredicate::test);
	}
}
