package com.kenstevens.stratinit.cache;

import com.kenstevens.stratinit.client.model.City;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CityCache extends Cacheable {
	private final List<City> cities = new ArrayList<City>();

	public List<City> getCities() {
		return Collections.unmodifiableList(cities);
	}

	public void add(City city) {
		cities.add(city);
	}

	public void remove(City city) {
		cities.remove(city);
	}
}
