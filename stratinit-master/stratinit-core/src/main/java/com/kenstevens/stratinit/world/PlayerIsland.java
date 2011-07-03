package com.kenstevens.stratinit.world;

import java.util.List;

import com.google.common.collect.Iterables;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.model.World;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.SectorType;
import com.kenstevens.stratinit.world.predicate.FriendlyPredicate;

public class PlayerIsland extends Island {
	private final FriendlyPredicate friendlyPredicate;

	public PlayerIsland(int islandId, World world, WorldConfig worldConfig,
			SectorCoords startCoord) {
		super(islandId, world, worldConfig, startCoord);
		this.friendlyPredicate = new FriendlyPredicate(islandId);
	}

	@Override
	protected int spikiness() {
		return worldConfig.getSpikiness();
	}

	public void grow(int numSectors) {
		placeInlandCities();
		growRemainingSectors(numSectors);
		addPorts();
	}

	private void placeInlandCities() {
		placeStartCity();
		placeSecondCity();
	}

	private void placeSecondCity() {
		Sector secondCity = null;
		for (Sector coastalSector : randomize(coastalSectors())) {
			secondCity = growFrom(coastalSector);
			if (secondCity != null) {
				break;
			}
		}
		placeInlandCity(secondCity);
	}



	private void placeStartCity() {
		placeInlandCity(world.getSector(startCoord));
	}

	private void placeInlandCity(Sector sector) {
		if (sector == null) {
			throw new IllegalStateException("Unable to place inland city.");
		}

		sector.setType(SectorType.START_CITY);
		sector.setIsland(islandId);
		addSectorToIsland(sector);
		for (Sector neighbour : world.getNeighbours(sector)) {
			if (!friendlyPredicate.apply(neighbour)) {
				throw new IllegalStateException(
						"Not enough room to grow start island.");
			}
			addLandToIsland(neighbour);
		}
	}

	private void addPorts() {
		List<Sector> coastalSectors = coastalSectors();
		for (int i = 0; i < worldConfig.getPortsPerPlayerIsland(); ++i) {
			if (!Iterables.any(randomize(coastalSectors),
					isolatedCityPredicate)) {
				new WorldPrinter(world).print();
				throw new IllegalStateException("No room to add port #"
						+ (i + 1) + " to island #" + islandId);
			}
			Sector nextCity = Iterables.find(randomize(coastalSectors),
					isolatedCityPredicate);
			nextCity.setType(SectorType.NEUTRAL_CITY);
		}

	}
}
