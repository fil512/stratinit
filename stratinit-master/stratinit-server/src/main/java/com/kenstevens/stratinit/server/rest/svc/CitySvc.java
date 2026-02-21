package com.kenstevens.stratinit.server.rest.svc;

import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.dao.CityDao;
import com.kenstevens.stratinit.dao.NationDao;
import com.kenstevens.stratinit.dao.RelationDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.dto.SICityUpdate;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.remote.CityFieldToUpdateEnum;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.CityDaoService;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CitySvc {
	@Autowired
	private CityDao cityDao;
	@Autowired
	private RelationDao relationDao;
	@Autowired
	private NationDao nationDao;
	@Autowired
	private UnitDao unitDao;
	@Autowired
	private CityDaoService cityDaoService;
	@Autowired
	private UnitDaoService unitDaoService;
	@Autowired
	private PlayerWorldViewUpdate playerWorldViewUpdate;

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

	public Result<SIUpdate> cedeCity(Nation nation, SICityUpdate sicity, int nationId, Game game) {
		SectorCoords coords = sicity.coords;
		City city = cityDao.getCity(nation, coords);
		if (city == null) {
			return new Result<>("There is no city at " + coords, false);
		}
		if (!city.getNation().equals(nation)) {
			return new Result<>("You do not own the city at " + coords, false);
		}
		int gameId = nation.getGameId();
		Nation target = nationDao.getNation(gameId, nationId);

		Relation relation = relationDao.findRelation(nation, target);
		Relation reverse = relationDao.findRelation(target, nation);
		if (relation.getType() != RelationType.ALLIED || reverse.getType() != RelationType.ALLIED) {
			return new Result<>("You may only cede cities to an ally.", false);
		}

		Result<None> result = Result.trueInstance();
		Collection<Unit> units = unitDao.getUnits(game, coords);
		for (Unit unit : units) {
			result.or(unitDaoService.cedeUnit(unit, target));
		}
		result.or(cityDaoService.cedeCity(city, target));
		SIUpdate siupdate = playerWorldViewUpdate.getWorldViewUpdate(nation);
		return new Result<>(result.getMessages(), true, siupdate, result.getBattleLogs(), result.isSuccess());
	}
}
