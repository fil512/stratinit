package com.kenstevens.stratinit.world.predicate;

import com.google.common.base.Predicate;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitBase;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.CoordMeasure;
import com.kenstevens.stratinit.type.UnitType;

public class UnitCanInterdictUnitPredicate implements Predicate<Unit> {
	private final CoordMeasure coordMeasure;
	private final Unit targetUnit;

	public UnitCanInterdictUnitPredicate(CoordMeasure coordMeasure, Unit targetUnit) {
		this.coordMeasure = coordMeasure;
		this.targetUnit = targetUnit;
	}

	@Override
	public boolean apply(Unit unit) {
		int sight = unit.getSight();
		if (targetUnit.isSubmarine()) {
			sight = unit.getSubSight();
		} else if (unit.getType() == UnitType.NAVAL_BOMBER) {
			sight = Constants.INTERCEPTION_RADIUS;
		} else if (unit.getType() == UnitType.CARRIER) {
			sight = UnitBase.getUnitBase(UnitType.CARRIER).getSight();
		}
		return unit.getCoords().distanceTo(coordMeasure, targetUnit.getCoords()) <= sight;
	}

}
