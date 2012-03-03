package com.kenstevens.stratinit.dao.impl.predicates;

import com.google.common.base.Predicate;
import com.kenstevens.stratinit.cache.NationCache;
import com.kenstevens.stratinit.type.SectorCoords;

public class SeesSectorPredicate implements Predicate<NationCache> {
	private final SectorCoords coords;
	
	public SeesSectorPredicate(SectorCoords coords) {
		this.coords = coords;
	}

	@Override
	public boolean apply(NationCache nationCache) {
		return nationCache.getSectorSeen(coords) != null;
	}

}
