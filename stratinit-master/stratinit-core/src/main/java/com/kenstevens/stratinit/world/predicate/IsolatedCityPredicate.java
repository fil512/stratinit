package com.kenstevens.stratinit.world.predicate;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.kenstevens.stratinit.client.model.Sector;
import com.kenstevens.stratinit.client.model.World;

public class IsolatedCityPredicate implements Predicate<Sector> {
	private final World world;
	private final int minDistanceEntreCities;
	private final CityPredicate cityPredicate;

	public IsolatedCityPredicate(World world, int minDistanceEntreCities) {
		this.world = world;
		this.minDistanceEntreCities = minDistanceEntreCities;
		cityPredicate = new CityPredicate();
	}

	@Override
	public boolean apply(Sector sector) {
		// if I am a city
		if (cityPredicate.apply(sector)) {
			return false;
		}
		// There are no cities near me
		return !Iterables.any(world.getNeighbours(sector, minDistanceEntreCities - 1), new CityPredicate());
	}
}
