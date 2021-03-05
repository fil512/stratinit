package com.kenstevens.stratinit.world.predicate;

import com.google.common.base.Predicate;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.CoordMeasure;
import com.kenstevens.stratinit.type.SectorCoords;

public class UnitCanCounterFireUnitPredicate implements Predicate<Unit> {
	private final CoordMeasure coordMeasure;
	private final Unit targetUnit;
	private final SectorCoords excludeCoords;

	public UnitCanCounterFireUnitPredicate(CoordMeasure coordMeasure, Unit targetUnit, SectorCoords excludeCoords) {
		this.coordMeasure = coordMeasure;
		this.targetUnit = targetUnit;
		this.excludeCoords = excludeCoords;
	}

	@Override
	public boolean apply(Unit unit) {
		if (unit.getCoords().equals(excludeCoords)) {
			return false;
		}
		return unit.getCoords().distanceTo(coordMeasure, targetUnit.getCoords()) <= Constants.SHIP_COUNTERFIRE_RANGE;
	}

}
