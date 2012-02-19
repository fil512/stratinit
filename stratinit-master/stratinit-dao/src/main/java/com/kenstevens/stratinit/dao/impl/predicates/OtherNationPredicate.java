package com.kenstevens.stratinit.dao.impl.predicates;

import com.google.common.base.Predicate;
import com.kenstevens.stratinit.cache.NationCache;
import com.kenstevens.stratinit.model.Nation;

public class OtherNationPredicate implements Predicate<NationCache> {
	private final Nation nation;

	public OtherNationPredicate(Nation nation) {
		this.nation = nation;
	}

	@Override
	public boolean apply(NationCache otherNation) {
		return !otherNation.getNation().equals(nation);
	}

}
