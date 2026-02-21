package com.kenstevens.stratinit.server.service;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.model.EventKey;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.remote.CityFieldToUpdateEnum;
import com.kenstevens.stratinit.server.event.CityBuildEvent;
import com.kenstevens.stratinit.server.event.UnitUpdateEvent;
import com.kenstevens.stratinit.server.rest.event.EventTimerMockedBase;
import com.kenstevens.stratinit.server.rest.helper.WorldManagerHelper;
import com.kenstevens.stratinit.server.svc.CityBuilderService;
import com.kenstevens.stratinit.server.svc.WorldManager;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class WorldManagerTest extends EventTimerMockedBase {
	@Autowired
	WorldManagerHelper worldManagerHelper;
	@Autowired
	WorldManager worldManager;
	@Autowired
	SectorDao sectorDao;
	@Autowired
	SectorService sectorService;
	@Autowired
	CityService cityService;
	@Autowired
	CityBuilderService cityBuilderService;

	private Nation nation;
	private Date now;

	@BeforeEach
	public void init() {
		super.init();
		nation = worldManagerHelper.createNation(testGameId);
		now = new Date();
	}

	@Test
	public void citiesAdded() {
		worldManager.addPlayerToMap(2, nation);
		List<City> cities = cityDao.getCities(nation);
		assertEquals(2, cities.size());

		City city0 = cities.get(0);
		City city1 = cities.get(1);
		assertTimeNear(now.getTime(), city0.getLastUpdated().getTime());
		assertTimeNear(now.getTime(), city1.getLastUpdated().getTime());

		verify(eventTimer, times(2)).schedule(any(CityBuildEvent.class));
		verify(eventTimer, times(5)).schedule(any(UnitUpdateEvent.class));
		verify(eventTimer, times(0)).cancel(any(EventKey.class));
	}

	@Test
	public void setNextBuildAndSwitch() {
		worldManager.addPlayerToMap(2, nation);

		List<City> cities = cityDao.getCities(nation);
		City city = cities.get(0);
		cityService.updateCity(nation, city.getCoords(), CityFieldToUpdateEnum.NEXT_BUILD, null, UnitType.ENGINEER, false, null);
		cityService.updateCity(nation, city.getCoords(), CityFieldToUpdateEnum.SWITCH_ON_TECH_CHANGE, null, null, true, null);
		cityBuilderService.switchCityProductionIfTechPermits(city, new Date());

		verify(eventTimer, times(3)).schedule(any(CityBuildEvent.class));
		verify(eventTimer, times(5)).schedule(any(UnitUpdateEvent.class));
		verify(eventTimer).cancel(any(EventKey.class));
	}

	@Test
	public void setNextBuildSatAndBuild() {
		assertEquals(0, cityDao.getCities(nation).size());

		worldManager.addPlayerToMap(2, nation);

		List<City> cities = cityDao.getCities(nation);
		List<Unit> units = unitDao.getUnits(nation);
		assertEquals(5, units.size());
		City city = cities.get(0);
		cityDao.markCacheModified(city);
		cityService.updateCity(nation, city.getCoords(), CityFieldToUpdateEnum.NEXT_BUILD, null, UnitType.SATELLITE, false, null);
		cityService.updateCity(nation, city.getCoords(), CityFieldToUpdateEnum.SWITCH_ON_TECH_CHANGE, null, null, true, null);
		nation.setTech(15.0);
		nationDao.markCacheModified(nation);
		cityBuilderService.switchCityProductionIfTechPermits(city, now);
		cityBuilderService.buildUnit(city, now);
		units = unitDao.getUnits(nation);
		assertEquals(6, units.size());
		assertEquals(UnitType.SATELLITE, units.get(5).getType());

		verify(eventTimer, times(3)).schedule(any(CityBuildEvent.class));
		verify(eventTimer, times(6)).schedule(any(UnitUpdateEvent.class));
		verify(eventTimer).cancel(any(EventKey.class));
	}

	@Test
	public void buildThenSetNextBuildSat() {
		assertEquals(0, cityDao.getCities(nation).size());

		worldManager.addPlayerToMap(2, nation);

		List<City> cities = cityDao.getCities(nation);
		List<Unit> units = unitDao.getUnits(nation);
		assertEquals(5, units.size());
		City city = cities.get(0);
		cityDao.markCacheModified(city);
		cityService.updateCity(nation, city.getCoords(), CityFieldToUpdateEnum.NEXT_BUILD, null, UnitType.SATELLITE, false, null);
		cityService.updateCity(nation, city.getCoords(), CityFieldToUpdateEnum.SWITCH_ON_TECH_CHANGE, null, null, true, null);
		cityBuilderService.buildUnit(city, now);
		nation.setTech(15.0);
		nationDao.markCacheModified(nation);
		cityBuilderService.switchCityProductionIfTechPermits(city, now);
		assertEquals(UnitType.SATELLITE, city.getBuild());
		units = unitDao.getUnits(nation);
		assertEquals(6, units.size());
		assertEquals(UnitType.INFANTRY, units.get(5).getType());

		verify(eventTimer, times(3)).schedule(any(CityBuildEvent.class));
		verify(eventTimer, times(6)).schedule(any(UnitUpdateEvent.class));
		verify(eventTimer).cancel(any(EventKey.class));
	}

	@Test
	public void setNextBuildInfAndBuild() {
		worldManager.addPlayerToMap(2, nation);

		List<City> cities = cityDao.getCities(nation);
		List<Unit> units = unitDao.getUnits(nation);
		assertEquals(5, units.size());
		City city = cities.get(0);
		cityDao.markCacheModified(city);
		cityService.updateCity(nation, city.getCoords(), CityFieldToUpdateEnum.NEXT_BUILD, null, UnitType.INFANTRY, false, null);
		cityService.updateCity(nation, city.getCoords(), CityFieldToUpdateEnum.SWITCH_ON_TECH_CHANGE, null, null, true, null);
		nation.setTech(15.0);
		nationDao.markCacheModified(nation);
		cityBuilderService.switchCityProductionIfTechPermits(city, now);
		cityBuilderService.buildUnit(city, now);
		units = unitDao.getUnits(nation);
		assertEquals(6, units.size());

		verify(eventTimer, times(2)).schedule(any(CityBuildEvent.class));
		verify(eventTimer, times(6)).schedule(any(UnitUpdateEvent.class));
	}
}
