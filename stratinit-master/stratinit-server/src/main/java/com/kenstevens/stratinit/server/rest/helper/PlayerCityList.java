package com.kenstevens.stratinit.server.rest.helper;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.Nation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PlayerCityList {
	@Autowired
	private SectorDao sectorDao;
	@Autowired
	private GameDao gameDao;

	public List<SICity> getCities(Nation nation) {
		List<City> cities = sectorDao.getCities(nation);
		return citiesToSICities(nation, cities);
	}

	private List<SICity> citiesToSICities(final Nation nation, Collection<City> cities) {
		return Lists.newArrayList(Collections2.transform(cities, new Function<City, SICity>() {
			public SICity apply(City city) {
				return cityToSICity(nation, city);
			}
		}));
	}

	public SICity cityToSICity(Nation nation, City city) {
		SICity sicity = new SICity(city);
		sicity.privateData(nation, city);
		return sicity;
	}

	public List<SICity> getSeenCities(Nation nation) {
		Set<City> cities = new HashSet<City>();
		List<City> myCities = sectorDao.getSeenCities(nation);
		cities.addAll(myCities);
		Collection<Nation> allies = gameDao.getAllies(nation);
		for (Nation ally : allies) {
			List<City> allycities = sectorDao.getSeenCities(ally);
			cities.addAll(allycities);
		}
		return citiesToSICities(nation, cities);
	}
}
