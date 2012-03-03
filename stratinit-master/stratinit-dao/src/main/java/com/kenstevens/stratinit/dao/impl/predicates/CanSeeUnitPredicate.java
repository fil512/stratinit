package com.kenstevens.stratinit.dao.impl.predicates;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.kenstevens.stratinit.cache.NationCache;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.type.CoordMeasure;
import com.kenstevens.stratinit.world.predicate.LaunchedSatelliteCanSeeUnitPredicate;
import com.kenstevens.stratinit.world.predicate.UnitCanSeeUnitPredicate;

public class CanSeeUnitPredicate implements Predicate<NationCache> {
	private final CoordMeasure coordMeasure;
	private final Unit targetUnit;

	public CanSeeUnitPredicate(CoordMeasure coordMeasure, Unit targetUnit) {
		this.coordMeasure = coordMeasure;
		this.targetUnit = targetUnit;
	}

	@Override
	public boolean apply(NationCache nationCache) {
		return targetSeenBySatellite(nationCache) || targetSeenByUnit(nationCache);
	}

	private boolean targetSeenBySatellite(NationCache nationCache) {
		LaunchedSatelliteCanSeeUnitPredicate launchedSatelliteCanSeeUnitPredicate = new LaunchedSatelliteCanSeeUnitPredicate(coordMeasure, targetUnit);
		return Iterables.any(nationCache.getLaunchedSatellites(), launchedSatelliteCanSeeUnitPredicate);
	}

	private boolean targetSeenByUnit(NationCache nationCache) {
		UnitCanSeeUnitPredicate unitCanSeeUnitPredicate = new UnitCanSeeUnitPredicate(coordMeasure, targetUnit);
		return Iterables.any(nationCache.getUnits(), unitCanSeeUnitPredicate);
	}
}
