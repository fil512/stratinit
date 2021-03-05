package com.kenstevens.stratinit.world.predicate;

import com.google.common.base.Predicate;
import com.kenstevens.stratinit.client.model.WorldSector;

public class TeamCityPredicate implements Predicate<WorldSector> {

	@Override
	public boolean apply(WorldSector sector) {
		return sector.isPlayerCity() && sector.onMyTeam();
	}

}
