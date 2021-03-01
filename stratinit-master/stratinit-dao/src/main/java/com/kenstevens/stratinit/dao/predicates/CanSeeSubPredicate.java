package com.kenstevens.stratinit.dao.predicates;

import com.google.common.base.Predicate;
import com.kenstevens.stratinit.cache.NationCache;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.type.CoordMeasure;
import com.kenstevens.stratinit.world.predicate.UnitCanSeeUnitPredicate;
import com.kenstevens.stratinit.world.predicate.UnitSeesSubsPredicate;

public class CanSeeSubPredicate implements Predicate<NationCache> {
	private final CoordMeasure coordMeasure;
	private final Unit sub;

	public CanSeeSubPredicate(CoordMeasure coordMeasure, Unit sub) {
		this.coordMeasure = coordMeasure;
		this.sub = sub;
	}

	@Override
	public boolean apply(NationCache nationCache) {
		UnitSeesSubsPredicate unitSeesSubsPredicate = new UnitSeesSubsPredicate();
		UnitCanSeeUnitPredicate unitCanSeeUnitPredicate = new UnitCanSeeUnitPredicate(coordMeasure, sub);
		java.util.function.Predicate<Unit> canSeeThisSub = unitSeesSubsPredicate.and(unitCanSeeUnitPredicate);
		return nationCache.getUnits().stream().anyMatch(canSeeThisSub);
	}
}
