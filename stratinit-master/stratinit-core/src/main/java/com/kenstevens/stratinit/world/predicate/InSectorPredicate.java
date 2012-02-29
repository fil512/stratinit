package com.kenstevens.stratinit.world.predicate;

import com.google.common.base.Predicate;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.type.SectorCoords;

public class InSectorPredicate implements Predicate<Unit> {
	private final SectorCoords coords;

	public InSectorPredicate(SectorCoords coords) {
		this.coords = coords;
	}

	@Override
	public boolean apply(Unit unit) {
		return unit.getCoords().equals(coords);
	}
}
