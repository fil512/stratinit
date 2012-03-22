package com.kenstevens.stratinit.dao.impl.predicates;

import com.google.common.base.Predicate;
import com.kenstevens.stratinit.model.City;

public class BaseCityPredicate implements Predicate<City> {
	@Override
	public boolean apply(City city) {
		return city.isBase();
	}
}
