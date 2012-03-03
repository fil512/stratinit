package com.kenstevens.stratinit.cache;

import java.util.Collections;
import java.util.List;

import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.model.World;

public class WorldCache extends Cacheable {
	private final World world;
	private final List<Sector> sectors;

	public WorldCache(World world) {
		this.world = world;
		sectors = world.getSectors();
	}

	public World getWorld() {
		return world;
	}

	public List<Sector> getSectors() {
		return Collections.unmodifiableList(sectors);
	}
}
