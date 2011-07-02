package com.kenstevens.stratinit.dao.impl.predicates;

import com.google.common.base.Predicate;
import com.kenstevens.stratinit.cache.NationCache;
import com.kenstevens.stratinit.model.Unit;

public class SeesUnitPredicate implements Predicate<NationCache> {
	private final Unit unit;
	
	public SeesUnitPredicate(Unit unit) {
		this.unit = unit;
	}

	@Override
	public boolean apply(NationCache nationCache) {
		return nationCache.getUnitSeen(unit.getId()) != null;
	}

}
