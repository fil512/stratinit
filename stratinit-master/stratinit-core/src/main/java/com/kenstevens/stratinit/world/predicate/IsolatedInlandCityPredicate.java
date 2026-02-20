package com.kenstevens.stratinit.world.predicate;

import java.util.function.Predicate;
import com.kenstevens.stratinit.client.model.Sector;
import com.kenstevens.stratinit.client.model.World;

public class IsolatedInlandCityPredicate implements Predicate<Sector> {
	private final World world;
	private final IsolatedCityPredicate isolatedCityPredicate;

	public IsolatedInlandCityPredicate(World world, IsolatedCityPredicate isolatedCityPredicate) {
		this.world = world;
		this.isolatedCityPredicate = isolatedCityPredicate;
	}

	@Override
	public boolean test(Sector sector) {
		WaterPredicate waterPredicate = new WaterPredicate();
		return isolatedCityPredicate.test(sector) && world.getNeighbours(sector, 1).stream().noneMatch(waterPredicate::test);
	}
}
