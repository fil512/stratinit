package com.kenstevens.stratinit.world.predicate;

import com.google.common.base.Predicate;
import com.kenstevens.stratinit.model.LaunchedSatellite;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.CoordMeasure;

public class LaunchedSatelliteCanSeeUnitPredicate implements Predicate<LaunchedSatellite> {
	private final CoordMeasure coordMeasure;
	private final Unit targetUnit;
	
	public LaunchedSatelliteCanSeeUnitPredicate(CoordMeasure coordMeasure, Unit targetUnit) {
		this.coordMeasure = coordMeasure;
		this.targetUnit = targetUnit;
	}

	@Override
	public boolean apply(LaunchedSatellite launchedSatellite) {
		return launchedSatellite.getCoords().distanceTo(coordMeasure, targetUnit.getCoords()) <= Constants.SATELLITE_SIGHT;
	}
}
