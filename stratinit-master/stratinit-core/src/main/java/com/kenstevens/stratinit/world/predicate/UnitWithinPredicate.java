package com.kenstevens.stratinit.world.predicate;

import com.google.common.base.Predicate;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.type.CoordMeasure;
import com.kenstevens.stratinit.type.SectorCoords;

public class UnitWithinPredicate implements Predicate<Unit> {
	private final CoordMeasure coordMeasure;
	private final SectorCoords coords;
	private final int distance;

	public UnitWithinPredicate(CoordMeasure coordMeasure, SectorCoords coords, int distance) {
		this.coordMeasure = coordMeasure;
		this.coords = coords;
		this.distance = distance;
	}

	@Override
	public boolean apply(Unit unit) {
		return unit.getCoords().distanceTo(coordMeasure, coords) <= distance;
	}
}
