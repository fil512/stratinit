package com.kenstevens.stratinit.world.predicate;

import java.util.function.Predicate;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.model.WorldSector;

public class SectorSuppliesUnitPredicate implements Predicate<WorldSector> {
	private final Unit targetUnit;
	
	public SectorSuppliesUnitPredicate(Unit targetUnit) {
		this.targetUnit = targetUnit;
	}

	@Override
	public boolean test(WorldSector sector) {
		return sector.supplies(targetUnit);
	}

}
