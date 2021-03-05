package com.kenstevens.stratinit.world.predicate;

import com.google.common.base.Predicate;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.model.WorldSector;

public class SectorSuppliesUnitPredicate implements Predicate<WorldSector> {
	private final Unit targetUnit;
	
	public SectorSuppliesUnitPredicate(Unit targetUnit) {
		this.targetUnit = targetUnit;
	}

	@Override
	public boolean apply(WorldSector sector) {
		return sector.supplies(targetUnit);
	}

}
