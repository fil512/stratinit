package com.kenstevens.stratinit.world.predicate;

import java.util.function.Predicate;
import com.kenstevens.stratinit.client.model.Sector;
import com.kenstevens.stratinit.type.SectorType;

public class CityPredicate implements Predicate<Sector> {
	@Override
	public boolean test(Sector sector) {
		return sector.getType() == SectorType.START_CITY || sector.getType() == SectorType.NEUTRAL_CITY;
	}

}
