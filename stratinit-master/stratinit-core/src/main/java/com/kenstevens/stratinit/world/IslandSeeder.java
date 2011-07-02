package com.kenstevens.stratinit.world;

import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.model.World;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.world.predicate.UnassignedAndIsolatedPredicate;

public class IslandSeeder {
	private Logger logger = Logger.getLogger(getClass());
	protected static final Random RANDOM = new Random();
	private static final int MAX_ATTEMPTS = 64;
	private final SectorCoords startCoord;
	private final World world;
	private final UnassignedAndIsolatedPredicate unassignedAndIsolatedPredicate;

	public IslandSeeder(World world, SectorCoords startCoord,
			UnassignedAndIsolatedPredicate unassignedAndIsolatedPredicate) {
		this.world = world;
		this.startCoord = startCoord;
		this.unassignedAndIsolatedPredicate = unassignedAndIsolatedPredicate;
	}

	List<Sector> seedIsland() {
		List<Sector> startSectors = Lists.newArrayList();
		startSectors.add(world.getSector(startCoord));
		int extraVolcanos = RANDOM.nextInt(4);
		for (int i = 0; i < extraVolcanos; ++i) {
			Sector sector = findNearbyVolcano(startCoord);
			if (sector != null) {
				startSectors.add(sector);
			}
		}
		return startSectors;
	}

	private Sector findNearbyVolcano(SectorCoords startCoord) {
		for (int i = 0; i < MAX_ATTEMPTS; ++i) {
			int distance = 3 + RANDOM.nextInt(3);
			double radians = RANDOM.nextDouble() * 2 * Math.PI;
			int dx = (int) (0.5 + distance * Math.cos(radians));
			int dy = (int) (0.5 + distance * Math.sin(radians));
			SectorCoords candidate = startCoord.shift(world.size(),
					new SectorCoords(dx, dy));
			Sector sector = world.getSector(candidate);
			if (unassignedAndIsolatedPredicate.apply(sector)) {
				return sector;
			}
		}
		logger.error("Unable to situate nearby volcano.");
		return null;
	}
}
