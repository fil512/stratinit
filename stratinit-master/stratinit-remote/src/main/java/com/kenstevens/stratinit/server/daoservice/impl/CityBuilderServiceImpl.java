package com.kenstevens.stratinit.server.daoservice.impl;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitBase;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.CityBuilderService;
import com.kenstevens.stratinit.server.daoservice.MessageDaoService;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.server.remote.event.EventQueue;
import com.kenstevens.stratinit.type.CityType;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import com.kenstevens.stratinit.util.BuildHelper;

@Service
public class CityBuilderServiceImpl implements CityBuilderService {
	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private EventQueue eventQueue;
	@Autowired
	private SectorDaoService sectorDaoService;
	@Autowired
	private UnitDaoService unitDaoService;
	@Autowired
	private SectorDao sectorDao;
	@Autowired
	private MessageDaoService messageDaoService;

	@Override
	public void cityCapturedBuildChange(City city) {
		eventQueue.cancel(city);
		city.setBuild(UnitType.BASE, new Date());
		city.setNextBuild(null);
	}
	
	@Override
	public Result<City> updateBuild(Nation nation, City city, UnitType build) {
		boolean changed = setBuild(city, build, new Date());
		Result<City> retval = new Result<City>(true);

		if (!changed) {
			return retval;
		}

		retval.addMessage("Canceled " + city.getBuild() + " build");

		if (build != UnitType.BASE) {
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
		if (build != UnitType.BASE) {
			eventQueue.schedule(city);
		}
		
		if (city.getType() != origCityType) {
			sectorDaoService.cityChanged(city);
		}
		return true;
	}
	
	@Override
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
		int cityCount = sectorDao.getNumberOfCities(nation);
		int power = unitDaoService.getPower(nation);
		return BuildHelper.powerLimitReached(cityCount, power);
	}
	
	@Override
	public void buildUnit(City city, Date buildTime) {
		UnitType unitType = city.getBuild();
		if (unitType == UnitType.BASE) {
			city.setLastUpdated(buildTime);
			sectorDao.merge(city);
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
			sectorDao.clearCityMove(city);
		}
		city.setLastUpdated(buildTime);
		sectorDao.merge(city);
		return;
	}
	
	@Override
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
