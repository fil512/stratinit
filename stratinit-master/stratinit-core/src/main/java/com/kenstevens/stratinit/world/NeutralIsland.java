package com.kenstevens.stratinit.world;

import com.google.common.collect.Iterables;
import com.kenstevens.stratinit.client.model.Sector;
import com.kenstevens.stratinit.client.model.World;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.SectorType;
import com.kenstevens.stratinit.world.predicate.IsolatedInlandCityPredicate;

import java.util.ArrayList;
import java.util.List;

public class NeutralIsland extends Island {

	private final IsolatedInlandCityPredicate isolatedInlandCityPredicate;

	public NeutralIsland(int islandId, World world, WorldConfig worldConfig,
			SectorCoords startCoord) {
		super(islandId, world, worldConfig, startCoord);
		this.isolatedInlandCityPredicate = new IsolatedInlandCityPredicate(
				world, isolatedCityPredicate);

	}

	@Override
	protected int spikiness() {
		return 40 + RANDOM.nextInt(40);
	}

	public void growContinent(int numSectors) {
		IslandSeeder islandSeeder = new IslandSeeder(world, startCoord, unassignedAndIsolatedPredicate);
		List<Sector> startSectors = islandSeeder.seedIsland();
		for (Sector sector : startSectors) {
			addLandToIsland(sector);
		}
		growRemainingSectors(numSectors);
	}

	public void sprinkleCities() {
		int percCities = worldConfig.getMinPercentCities()
				+ RANDOM.nextInt(1 + worldConfig.getMaxPercentCities()
						- worldConfig.getMinPercentCities());
		int numCities = percCities * sectors.size() / 100;
		
		if (numCities == 0) {
			numCities = 1;
		}

		for (int i = 0; i < numCities; ++i) {
			if (!Iterables.any(randomize(sectors()), isolatedCityPredicate)) {
				return;
			}
			// First city should prefer inland
			Sector nextCity = pickSectorToPlaceCity(i == 0);
			nextCity.setType(SectorType.NEUTRAL_CITY);
		}
	}

	private Sector pickSectorToPlaceCity(boolean preferInland) {
		Sector retval = null;
		if (preferInland
				&& Iterables.any(randomize(sectors()),
						isolatedInlandCityPredicate)) {
			retval = Iterables.find(randomize(sectors()),
					isolatedInlandCityPredicate);
		} else {
			retval = Iterables
					.find(randomize(sectors()), isolatedCityPredicate);
		}
		return retval;
	}

	private List<Sector> sectors() {
		return new ArrayList<Sector>(sectors);
	}
}
