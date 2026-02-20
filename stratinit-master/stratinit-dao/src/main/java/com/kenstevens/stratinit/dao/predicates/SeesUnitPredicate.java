package com.kenstevens.stratinit.dao.predicates;

import java.util.function.Predicate;
import com.kenstevens.stratinit.cache.NationCache;
import com.kenstevens.stratinit.client.model.Unit;

public class SeesUnitPredicate implements Predicate<NationCache> {
	private final Unit unit;
	
	public SeesUnitPredicate(Unit unit) {
		this.unit = unit;
	}

	@Override
	public boolean test(NationCache nationCache) {
		return nationCache.getUnitSeen(unit.getId()) != null;
	}

}
