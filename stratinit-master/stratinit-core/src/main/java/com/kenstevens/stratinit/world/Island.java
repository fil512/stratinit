package com.kenstevens.stratinit.world;

import com.kenstevens.stratinit.client.model.Sector;
import com.kenstevens.stratinit.client.model.World;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.SectorType;
import com.kenstevens.stratinit.world.predicate.IsolatedCityPredicate;
import com.kenstevens.stratinit.world.predicate.UnassignedAndIsolatedPredicate;
import com.kenstevens.stratinit.world.predicate.UnassignedPredicate;

import java.util.*;

public abstract class Island {
	protected final int islandId;
	protected final World world;
	protected final WorldConfig worldConfig;
	protected final SectorCoords startCoord;
	protected static final Random RANDOM = new Random();
	protected final Set<Sector> sectors = new HashSet<Sector>();

	private final UnassignedPredicate unassignedPredicate;
	protected final UnassignedAndIsolatedPredicate unassignedAndIsolatedPredicate;
	protected final IsolatedCityPredicate isolatedCityPredicate;

	private final int spikiness;

	protected Island(int islandId, World world, WorldConfig worldConfig,
			SectorCoords startCoord) {
		this.world = world;
		this.worldConfig = worldConfig;
		this.startCoord = startCoord;
		this.islandId = islandId;
		this.spikiness = spikiness();

		this.unassignedPredicate = new UnassignedPredicate();
		this.unassignedAndIsolatedPredicate = new UnassignedAndIsolatedPredicate(
				world, worldConfig.getMinDistanceEntreIslands(), islandId);
		this.isolatedCityPredicate = new IsolatedCityPredicate(world,
				worldConfig.getMinDistanceEntreCities());
	}

	protected abstract int spikiness();

	protected List<Sector> randomize(List<Sector> sectors) {
		List<Sector> retval = new ArrayList<Sector>();
		if (sectors.size() <= 1) {
			return sectors;
		}
		int size = sectors.size();
		int startId = RANDOM.nextInt(size);
		int sectorId = startId;
		do {
			Sector sector = sectors.get(sectorId);
			retval.add(sector);
			sectorId = (sectorId + 1) % size;
		} while (sectorId != startId);
		return retval;
	}

	protected void addLandToIsland(Sector neighbour) {
		neighbour.setType(SectorType.LAND);
		neighbour.setIsland(islandId);
		addSectorToIsland(neighbour);
	}

	protected List<Sector> coastalSectors() {
		List<Sector> coastalSectors = new ArrayList<Sector>();

		for (Sector sector : sectors) {
			// if this island sector has at least one unassigned i.e. water
			// neighbour
			List<Sector> neighbours = world.getNeighbours(sector);
			if (neighbours.stream().anyMatch(unassignedPredicate::test)) {
				coastalSectors.add(sector);
			}
		}
		return coastalSectors;
	}

	protected void addSectorToIsland(Sector neighbour) {
		sectors.add(neighbour);
	}

	protected void growRemainingSectors(int islandSize) {
		boolean done = false;
		Sector lastSectorAdded = null;
		while (sectors.size() < islandSize && !done) {
			lastSectorAdded = growOneSector(lastSectorAdded);
			if (lastSectorAdded != null) {
				addLandToIsland(lastSectorAdded);
			} else {
				done = true;
			}
		}
	}

	private Sector growOneSector(Sector lastSectorAdded) {
		Sector nextSector;
		if (doSpike() && lastSectorAdded != null) {
			nextSector = growFrom(lastSectorAdded);
			if (nextSector != null) {
				return nextSector;
			}
		}

		for (Sector coastalSector : randomize(coastalSectors())) {
			nextSector = growFrom(coastalSector);
			if (nextSector != null) {
				return nextSector;
			}
		}
		return null;
	}

	protected Sector growFrom(Sector coastalSector) {
		List<Sector> coastalSectors = randomize(world
				.getNeighbours(coastalSector));
		return coastalSectors.stream()
				.filter(unassignedAndIsolatedPredicate::test)
				.findFirst()
				.orElse(null);
	}

	private boolean doSpike() {
		return RANDOM.nextInt(100) < spikiness;
	}
}
