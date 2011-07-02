package com.kenstevens.stratinit.dao.impl.predicates;

import static com.google.common.base.Predicates.and;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
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
		Predicate<Unit> canSeeThisSub = and(new UnitSeesSubsPredicate(), new UnitCanSeeUnitPredicate(coordMeasure, sub));
		return Iterables.any(nationCache.getUnits(), canSeeThisSub);
	}
}
