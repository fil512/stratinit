package com.kenstevens.stratinit.world.predicate;

import com.google.common.base.Function;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.model.WorldSector;

public class SectorToWorldSectorFunction implements Function<Sector, WorldSector> {
	@Override
	public WorldSector apply(Sector sector) {
		return (WorldSector) sector;
	}
}
