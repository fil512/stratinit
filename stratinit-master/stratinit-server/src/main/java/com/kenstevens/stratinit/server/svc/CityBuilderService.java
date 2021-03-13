package com.kenstevens.stratinit.server.svc;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.model.UnitBase;
import com.kenstevens.stratinit.client.util.BuildHelper;
import com.kenstevens.stratinit.dao.CityDao;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.CityDaoService;
import com.kenstevens.stratinit.server.daoservice.MessageDaoService;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.server.event.svc.EventQueue;
import com.kenstevens.stratinit.type.CityType;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CityBuilderService {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private EventQueue eventQueue;
	@Autowired
	private CityDaoService cityDaoService;
	@Autowired
	private UnitDaoService unitDaoService;
	@Autowired
	private CityDao cityDao;
	@Autowired
	private MessageDaoService messageDaoService;

	public void cityCapturedBuildChange(City city) {
		eventQueue.cancel(city);
		city.setBuild(UnitType.BASE, new Date());
		city.setNextBuild(null);
	}

	public Result<City> updateBuild(Nation nation, City city, UnitType build) {
		boolean changed = setBuild(city, build, new Date());
		Result<City> retval = new Result<City>(true);

		if (!changed) {
			return retval;
		}

		retval.addMessage("Canceled " + city.getBuild() + " build");

		if (UnitBase.isUnit(build)) {
			retval.addMessage("Set build in " + city.getCoords() + " to "
					+ build);

			if (powerLimitReached(city.getNation()).getValue()) {
				retval
						.addMessage("WARNING: Build limit reached.  Will not build any units until you have more cities or lost some units.");
			}
		}

		return retval;
	}

	private boolean setBuild(City city, UnitType build, Date lastUpdated) {
		if (city == null || build == null || city.getBuild() == build) {
			return false;
		}

		CityType origCityType = city.getType();

		eventQueue.cancel(city);
		city.setBuild(build, lastUpdated);
		if (UnitBase.isUnit(build)) {
			eventQueue.schedule(city);
		}

		if (city.getType() != origCityType) {
			cityDaoService.cityChanged(city);
		}
		return true;
	}

	public Result<City> updateNextBuild(City city, UnitType nextBuild) {
		Result<City> retval = new Result<City>(true);

		if (city.getNextBuild() == nextBuild) {
			return retval;
		}
		if (nextBuild == null) {
			retval.addMessage("Cancelling next build in " + city.getCoords());
		} else {
			retval.addMessage("Setting next build in " + city.getCoords()
					+ " to " + nextBuild);
		}
		city.setNextBuild(nextBuild);

		return retval;
	}

	private Result<Boolean> powerLimitReached(Nation nation) {
		int cityCount = cityDao.getNumberOfCities(nation);
		int power = unitDaoService.getPower(nation);
		return BuildHelper.powerLimitReached(cityCount, power);
	}

	public void buildUnit(City city, Date buildTime) {
		UnitType unitType = city.getBuild();
		if (UnitBase.isNotUnit(unitType)) {
			city.setLastUpdated(buildTime);
			cityDao.markCacheModified(city);
			return;
		}
		Nation nation = city.getNation();
		SectorCoords coords = city.getCoords();
		Result<Boolean> powerLimitReached = powerLimitReached(nation);
		String message = nation
				+ " "
				+ unitType
				+ " in "
				+ coords + "\n"
				+ powerLimitReached.toString();
		if (powerLimitReached.getValue()) {
			logger.debug("Skipping build unit " + message);
			messageDaoService
					.notify(
							nation,
							"skipping " + unitType,
							"Skipping build unit " + message + ".  Maximum allowable power reached.\n\nKill your units or take some cities to increase your power limit.\n"
					);
		} else {
			logger.debug("Building unit " + message);
			Unit unit = unitDaoService.buildUnit(nation, coords, unitType, buildTime);
			if (city.getCityMove() != null) {
				unitDaoService.executeCityMove(unit, city);
			}
		}

		if (switchCityProductionIfTechPermits(city, buildTime)) {
			cityDao.clearCityMove(city);
		}
		city.setLastUpdated(buildTime);
		cityDao.markCacheModified(city);
	}

	public boolean switchCityProductionIfTechPermits(City city, Date buildTime) {
		boolean retval = false;
		Nation nation = city.getNation();

		UnitType nextBuild = city.getNextBuild();
		if (nextBuild != null
				&& city.getBuild() != nextBuild
				&& nation.getTech() >= UnitBase.getUnitBase(nextBuild)
				.getTech()) {
			if (setBuild(city, nextBuild, buildTime)) {
				city.setNextBuild(null);
				city.setSwitchOnTechChange(false);
				retval = true;
			}
		}

		return retval;
	}

}

