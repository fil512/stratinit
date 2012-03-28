package com.kenstevens.stratinit.cache;

import com.google.common.base.Function;
import com.kenstevens.stratinit.model.Nation;

public class NationCacheToNationFunction implements
		Function<NationCache, Nation> {

	@Override
	public Nation apply(NationCache nationCache) {
		return nationCache.getNation();
	}

}
