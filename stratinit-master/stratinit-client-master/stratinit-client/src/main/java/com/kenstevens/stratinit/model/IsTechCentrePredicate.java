package com.kenstevens.stratinit.model;

import com.google.common.base.Predicate;
import com.kenstevens.stratinit.type.CityType;

public class IsTechCentrePredicate implements Predicate<CityView> {

	@Override
	public boolean apply(CityView city) {
		return city.getType() == CityType.TECH;
	}

}
