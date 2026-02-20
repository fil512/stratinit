package com.kenstevens.stratinit.dao.predicates;

import java.util.function.Predicate;
import com.kenstevens.stratinit.cache.NationCache;
import com.kenstevens.stratinit.type.SectorCoords;

public class SeesSectorPredicate implements Predicate<NationCache> {
	private final SectorCoords coords;
	
	public SeesSectorPredicate(SectorCoords coords) {
		this.coords = coords;
	}

	@Override
	public boolean test(NationCache nationCache) {
		return nationCache.getSectorSeen(coords) != null;
	}

}
