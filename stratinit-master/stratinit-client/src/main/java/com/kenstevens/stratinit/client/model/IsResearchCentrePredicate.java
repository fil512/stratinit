package com.kenstevens.stratinit.client.model;

import com.google.common.base.Predicate;
import com.kenstevens.stratinit.type.UnitType;

public class IsResearchCentrePredicate implements Predicate<CityView> {

	@Override
	public boolean apply(CityView city) {
		return city.getBuild() == UnitType.RESEARCH;
	}

}
