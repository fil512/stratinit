package com.kenstevens.stratinit.model;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kenstevens.stratinit.type.SectorCoords;

public class CityList implements Iterable<CityView> {
	private Map<SectorCoords, CityView> cityMap = Maps.newHashMap();

	public CityView get(SectorCoords coords) {
		return cityMap.get(coords);
	}

	public void add(CityView city) {
		CityView newCity = city;
		if (cityMap.get(city.getCoords()) != null) {
			CityView orig = cityMap.get(city.getCoords());
			newCity = orig.copyFrom(city);
			newCity.setDeleted(false);
		}
		cityMap.put(newCity.getCoords(), newCity);
	}

	public void addAll(List<CityView> cities) {
		markDeleted();
		for (CityView city : cities) {
			add(city);
		}
		deleteMarked();
	}

	// TODO REF too much copy paste with unitview and possibly others
	private void deleteMarked() {
		List<CityView> citiesToDelete = Lists.newArrayList();
		for (CityView city : this) {
			if (city.isDeleted()) {
				citiesToDelete.add(city);
			}
		}
		for (CityView city : citiesToDelete) {
			cityMap.remove(city.getCoords());
		}
	}

	public void markDeleted() {
		for (CityView city : this) {
			city.setDeleted(true);
		}
	}

	public int size() {
		return cityMap.size();
	}

	@Override
	public Iterator<CityView> iterator() {
		return cityMap.values().iterator();
	}

	public boolean isEmpty() {
		return cityMap.isEmpty();
	}

	public int countMyTechCentres() {
		return Collections2.filter(cityMap.values(),
				new IsTechCentrePredicate()).size()
				+ Collections2.filter(cityMap.values(),
						new IsResearchCentrePredicate()).size();
	}

	public List<CityView> getCities() {
		// Safe copy to avoid concurrent modification exceptions
		return Lists.newArrayList(cityMap.values());
	}
}
