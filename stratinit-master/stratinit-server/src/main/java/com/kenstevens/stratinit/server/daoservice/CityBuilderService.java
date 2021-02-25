package com.kenstevens.stratinit.server.daoservice;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.type.UnitType;

import java.util.Date;

public interface CityBuilderService {

	void buildUnit(City city, Date nextMissedBuildTime);

	void cityCapturedBuildChange(City city);

	boolean switchCityProductionIfTechPermits(City city, Date now);

	Result<City> updateBuild(Nation nationMe, City city, UnitType build);

	Result<City> updateNextBuild(City city, UnitType nextBuild);

}
