package com.kenstevens.stratinit.world.predicate;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.model.World;

public class IsolatedInlandCityPredicate implements Predicate<Sector> {
	private final World world;
	private final IsolatedCityPredicate isolatedCityPredicate;

	public IsolatedInlandCityPredicate(World world, IsolatedCityPredicate isolatedCityPredicate) {
		this.world = world;
		this.isolatedCityPredicate = isolatedCityPredicate;
	}

	@Override
	public boolean apply(Sector sector) {
		return isolatedCityPredicate.apply(sector) && !Iterables.any(world.getNeighbours(sector, 1), new WaterPredicate());
	}
}
