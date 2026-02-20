package com.kenstevens.stratinit.world.predicate;

import java.util.function.Predicate;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.type.SectorCoords;

public class InSectorPredicate implements Predicate<Unit> {
	private final SectorCoords coords;

	public InSectorPredicate(SectorCoords coords) {
		this.coords = coords;
	}

	@Override
	public boolean test(Unit unit) {
		return unit.getCoords().equals(coords);
	}
}
