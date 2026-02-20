package com.kenstevens.stratinit.dao.predicates;

import java.util.function.Predicate;
import com.kenstevens.stratinit.cache.NationCache;
import com.kenstevens.stratinit.client.model.Nation;

public class OtherNationPredicate implements Predicate<NationCache> {
	private final Nation nation;

	public OtherNationPredicate(Nation nation) {
		this.nation = nation;
	}

	@Override
	public boolean test(NationCache otherNation) {
		return !otherNation.getNation().equals(nation);
	}

}
