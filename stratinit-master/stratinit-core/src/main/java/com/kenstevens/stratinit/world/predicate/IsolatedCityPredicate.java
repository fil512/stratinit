package com.kenstevens.stratinit.world.predicate;

import java.util.function.Predicate;
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
	public boolean test(Sector sector) {
		// if I am a city
		if (cityPredicate.test(sector)) {
			return false;
		}
		// There are no cities near me
		CityPredicate nearbyCity = new CityPredicate();
		return world.getNeighbours(sector, minDistanceEntreCities - 1).stream().noneMatch(nearbyCity::test);
	}
}
