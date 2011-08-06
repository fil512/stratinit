package com.kenstevens.stratinit.model;

import com.google.common.base.Predicate;
import com.kenstevens.stratinit.type.CityType;
import com.kenstevens.stratinit.type.UnitType;

public class IsResearchCentrePredicate implements Predicate<CityView> {

	@Override
	public boolean apply(CityView city) {
		return city.getBuild() == UnitType.RESEARCH;
	}

}