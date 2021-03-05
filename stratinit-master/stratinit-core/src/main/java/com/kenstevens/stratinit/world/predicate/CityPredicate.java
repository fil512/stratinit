package com.kenstevens.stratinit.world.predicate;

import com.google.common.base.Predicate;
import com.kenstevens.stratinit.client.model.Sector;
import com.kenstevens.stratinit.type.SectorType;

public class CityPredicate implements Predicate<Sector> {
	@Override
	public boolean apply(Sector sector) {
		return sector.getType() == SectorType.START_CITY || sector.getType() == SectorType.NEUTRAL_CITY;
	}

}
