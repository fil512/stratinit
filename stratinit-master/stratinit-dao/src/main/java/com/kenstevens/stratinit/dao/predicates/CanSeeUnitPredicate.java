package com.kenstevens.stratinit.dao.predicates;

import java.util.function.Predicate;
import com.kenstevens.stratinit.cache.NationCache;
import com.kenstevens.stratinit.client.model.Unit;
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
	public boolean test(NationCache nationCache) {
		return targetSeenBySatellite(nationCache) || targetSeenByUnit(nationCache);
	}

	private boolean targetSeenBySatellite(NationCache nationCache) {
		LaunchedSatelliteCanSeeUnitPredicate predicate = new LaunchedSatelliteCanSeeUnitPredicate(coordMeasure, targetUnit);
		return nationCache.getLaunchedSatellites().stream().anyMatch(predicate::test);
	}

	private boolean targetSeenByUnit(NationCache nationCache) {
		UnitCanSeeUnitPredicate predicate = new UnitCanSeeUnitPredicate(coordMeasure, targetUnit);
		return nationCache.getUnits().stream().anyMatch(predicate::test);
	}
}
