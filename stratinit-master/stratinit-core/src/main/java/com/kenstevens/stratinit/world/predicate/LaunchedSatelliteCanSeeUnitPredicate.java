package com.kenstevens.stratinit.world.predicate;

import java.util.function.Predicate;
import com.kenstevens.stratinit.client.model.LaunchedSatellite;
import com.kenstevens.stratinit.client.model.Unit;
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
	public boolean test(LaunchedSatellite launchedSatellite) {
		return launchedSatellite.getCoords().distanceTo(coordMeasure, targetUnit.getCoords()) <= Constants.SATELLITE_SIGHT;
	}
}
