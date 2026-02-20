package com.kenstevens.stratinit.server.rest.svc;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.dao.CityDao;
import com.kenstevens.stratinit.dao.RelationDao;
import com.kenstevens.stratinit.dto.SICityUpdate;
import com.kenstevens.stratinit.remote.CityFieldToUpdateEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CitySvc {
	@Autowired
	private CityDao cityDao;
	@Autowired
	private RelationDao relationDao;

	public List<SICityUpdate> getCities(Nation nation) {
		List<City> cities = cityDao.getCities(nation);
		return citiesToSICities(nation, cities);
	}

	private List<SICityUpdate> citiesToSICities(final Nation nation, Collection<City> cities) {
		return cities.stream()
				.map(city -> cityToSICity(nation, city, null))
				.collect(Collectors.toList());
	}

	public SICityUpdate cityToSICity(Nation nation, City city, CityFieldToUpdateEnum field) {
		SICityUpdate sicity = new SICityUpdate(city);
		sicity.privateData(nation, city);
		return sicity;
	}

	public List<SICityUpdate> getSeenCities(Nation nation) {
		Set<City> cities = new HashSet<City>();
		List<City> myCities = cityDao.getSeenCities(nation);
		cities.addAll(myCities);
		Collection<Nation> allies = relationDao.getAllies(nation);
		for (Nation ally : allies) {
			List<City> allycities = cityDao.getSeenCities(ally);
			cities.addAll(allycities);
		}
		return citiesToSICities(nation, cities);
	}
}
