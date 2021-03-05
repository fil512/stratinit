package com.kenstevens.stratinit.world;

import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.Sector;
import com.kenstevens.stratinit.client.model.World;
import com.kenstevens.stratinit.world.predicate.UnassignedAndIsolatedPredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class WorldCreator {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final WorldConfig worldConfig;
	private static final Random RANDOM = new Random();

	public WorldCreator(WorldConfig worldConfig) {
		this.worldConfig = worldConfig;
	}

	public WorldCreator() {
		this.worldConfig = new WorldConfig();
	}

	public World build(Game game) {
		logger.info("Creating world for {}", game);
		World world = new World(game, true);
		ElectronCloud electronCloud = new ElectronCloud(world.size(), world.getIslands());
		logger.info("Building player islands for {}", game);
		buildPlayerIslands(game, world, electronCloud);
		logger.info("Building continents for {}", game);
		buildContinents(world);
		logger.info("Finished creating world for {}", game);
		return world;
	}

	// Uninhabited islands to be discovered
	private void buildContinents(World world) {

		int lastSpace = worldConfig.getStartDistanceEntreContinents();
		int islandId = world.getIslands();
		while (world.percentWater() >= worldConfig.getPercentWater()) {
			Sector sector = null;
			int space;
			for (space = lastSpace; sector == null && space >= 1; --space) {
				sector = placeContinent(world, space);
				lastSpace = space;
			}
			if (sector == null) {
				throw new IllegalStateException("Not enough room to grow continent.");
			}
			NeutralIsland island = new NeutralIsland(islandId, world, worldConfig, sector.getCoords());
			island.growContinent(continentNumSectors());
			island.sprinkleCities();
//			System.out.println("Adding continent #"+islandId+".  Water percentage = "+world.percentWater()+"%.  Space between continents = "+space);
			++islandId;
		}
	}

	// Player start islands
	private void buildPlayerIslands(Game game, World world, ElectronCloud startElectronCloud) {
		startElectronCloud.init(game);
		ElectronCloud electronCloud = startElectronCloud.drift();
		for (int i = 0; i < world.getIslands(); ++i) {
			PlayerIsland island = new PlayerIsland(i, world, worldConfig, electronCloud.getPlayerCoord(i));
			island.grow(islandNumSectors(worldConfig.getPlayerIslandSize()));
		}
	}

	private Sector placeContinent(World world, int space) {
		UnassignedAndIsolatedPredicate unassignedAndIsolatedPredicate = new UnassignedAndIsolatedPredicate(world, space, Integer.MAX_VALUE);

		int size = world.size();
		int startx = RANDOM.nextInt(size);
		int starty = RANDOM.nextInt(size);
		int x = startx;
		int y = starty;
		Sector sector = null;
		boolean done = false;
		do {
			sector = world.getSector(x, y);
			x += 1;
			if (x == size) {
				x = 0;
				y = (y + 1) % size;
			}
			if (x == startx && y == starty) {
				done = true;
			}
		} while (!unassignedAndIsolatedPredicate.apply(sector) && !done);
		if (x == startx && y == starty) {
			return null;
		}
		return sector;
	}

	private int islandNumSectors(int baseSize) {
		int islandSize = baseSize;
		int variability = islandSize / 5;
		return islandSize + RANDOM.nextInt(variability) - variability / 2;
	}

	private int continentNumSectors() {
		return worldConfig.getMinContinentSize()+ RANDOM.nextInt(worldConfig.getMaxContinentSize() - worldConfig.getMinContinentSize());
	}
}
