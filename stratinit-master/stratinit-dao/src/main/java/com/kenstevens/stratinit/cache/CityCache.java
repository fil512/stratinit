package com.kenstevens.stratinit.cache;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.type.SectorCoords;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CityCache extends Cacheable {
	private final Map<SectorCoords, City> cityMap = new ConcurrentHashMap<>();

	public List<City> getCities() {
		return new ArrayList<>(cityMap.values());
	}

	public void add(City city) {
		cityMap.put(city.getCoords(), city);
	}

	public void remove(City city) {
		cityMap.remove(city.getCoords());
	}
}
