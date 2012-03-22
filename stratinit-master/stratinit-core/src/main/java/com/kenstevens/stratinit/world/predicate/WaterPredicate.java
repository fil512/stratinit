package com.kenstevens.stratinit.world.predicate;

import com.google.common.base.Predicate;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.type.SectorType;

public class WaterPredicate implements Predicate<Sector> {
	@Override
	public boolean apply(Sector sector) {
		return sector.getType() == SectorType.WATER;
	}

}
