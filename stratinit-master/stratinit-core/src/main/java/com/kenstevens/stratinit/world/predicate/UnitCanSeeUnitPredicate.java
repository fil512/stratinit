package com.kenstevens.stratinit.world.predicate;

import com.google.common.base.Predicate;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.type.CoordMeasure;

public class UnitCanSeeUnitPredicate implements Predicate<Unit> {
	private final CoordMeasure coordMeasure;
	private final Unit targetUnit;

	public UnitCanSeeUnitPredicate(CoordMeasure coordMeasure, Unit targetUnit) {
		this.coordMeasure = coordMeasure;
		this.targetUnit = targetUnit;
	}

	@Override
	public boolean apply(Unit unit) {
		int sight = unit.getSight();
		if (targetUnit.isSubmarine()) {
			sight = unit.getSubSight();
		}
		return unit.getCoords().distanceTo(coordMeasure, targetUnit.getCoords()) <= sight;
	}

}
