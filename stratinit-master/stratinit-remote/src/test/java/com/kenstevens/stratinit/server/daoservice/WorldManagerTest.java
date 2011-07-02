package com.kenstevens.stratinit.server.daoservice;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.EventKey;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.remote.UpdateCityField;
import com.kenstevens.stratinit.server.remote.event.CityBuildEvent;
import com.kenstevens.stratinit.server.remote.event.Event;
import com.kenstevens.stratinit.server.remote.event.EventQueue;
import com.kenstevens.stratinit.server.remote.event.EventTimerMockedBase;
import com.kenstevens.stratinit.server.remote.event.UnitUpdateEvent;
import com.kenstevens.stratinit.server.remote.helper.WorldManagerHelper;
import com.kenstevens.stratinit.type.UnitType;

@SuppressWarnings("deprecation")
public class WorldManagerTest extends EventTimerMockedBase {
	@Autowired
	WorldManagerHelper worldManagerHelper;
	@Autowired
	WorldManager worldManager;
	@Autowired
	SectorDao sectorDao;
	@Autowired
	SectorDaoService sectorDaoService;
	@Autowired
	EventQueue eventQueue;
	@Autowired
	CityBuilderService cityBuilderService;
	
	private Nation nation;
	private Date now;
	
	@Before
	public void createNation() {
		nation = worldManagerHelper.createNation(testGameId);
		now = new Date();
	}

	@Test
	public void citiesAdded() {
		createNation();
		context.checking(new Expectations() {
			{
				exactly(2).of(eventTimer).schedule((Event)with(a(CityBuildEvent.class)));
				exactly(5).of(eventTimer).schedule((Event)with(a(UnitUpdateEvent.class)));
			}
		});
		worldManager.addPlayerToMap(2, nation);
		
		List<City> cities = sectorDao.getCities(nation);
		assertEquals(2, cities.size());
		
		City city0 = cities.get(0);
		Date now = new Date();
		City city1 = cities.get(1);
		assertTimeNear(now.getTime(), city0.getLastUpdated().getTime());
		assertTimeNear(now.getTime(), city1.getLastUpdated().getTime());
		context.assertIsSatisfied();
	}
	
	@Test
	public void setNextBuildAndSwitch() {
		createNation();
		context.checking(new Expectations() {
			{
				exactly(3).of(eventTimer).schedule((Event)with(a(CityBuildEvent.class)));
				exactly(5).of(eventTimer).schedule((Event)with(a(UnitUpdateEvent.class)));
				oneOf(eventTimer).cancel((EventKey)with(a(EventKey.class)));
			}
		});

		worldManager.addPlayerToMap(2, nation);
		
		List<City> cities = sectorDao.getCities(nation);
		City city = cities.get(0);
		sectorDaoService.updateCity(nation, city.getCoords(), UpdateCityField.NEXT_BUILD, null, UnitType.ENGINEER, false, null);
		sectorDaoService.updateCity(nation, city.getCoords(), UpdateCityField.SWITCH_ON_TECH_CHANGE, null, null, true, null);
		cityBuilderService.switchCityProductionIfTechPermits(city, new Date());
		context.assertIsSatisfied();
	}	

	@Test
	public void setNextBuildSatAndBuild() {
		createNation();
		context.checking(new Expectations() {
			{
				exactly(3).of(eventTimer).schedule((Event)with(a(CityBuildEvent.class)));
				exactly(6).of(eventTimer).schedule((Event)with(a(UnitUpdateEvent.class)));
				oneOf(eventTimer).cancel((EventKey)with(a(EventKey.class)));
			}
		});

		worldManager.addPlayerToMap(2, nation);
		
		List<City> cities = sectorDao.getCities(nation);
		List<Unit> units = unitDao.getUnits(nation);
		assertEquals(5, units.size());
 		City city = cities.get(0);
		sectorDao.merge(city);
		sectorDaoService.updateCity(nation, city.getCoords(), UpdateCityField.NEXT_BUILD, null, UnitType.SATELLITE, false, null);
		sectorDaoService.updateCity(nation, city.getCoords(), UpdateCityField.SWITCH_ON_TECH_CHANGE, null, null, true, null);
		nation.setTech(15.0);
		gameDao.merge(nation);
		cityBuilderService.switchCityProductionIfTechPermits(city, now);
		cityBuilderService.buildUnit(city, now);
		units = unitDao.getUnits(nation);
		assertEquals(6, units.size());
		assertEquals(UnitType.SATELLITE, units.get(5).getType());
		context.assertIsSatisfied();
	}	

	@Test
	public void buildThenSetNextBuildSat() {
		createNation();
		context.checking(new Expectations() {
			{
				exactly(3).of(eventTimer).schedule((Event)with(a(CityBuildEvent.class)));
				exactly(6).of(eventTimer).schedule((Event)with(a(UnitUpdateEvent.class)));
				oneOf(eventTimer).cancel((EventKey)with(a(EventKey.class)));
			}
		});

		worldManager.addPlayerToMap(2, nation);
		
		List<City> cities = sectorDao.getCities(nation);
		List<Unit> units = unitDao.getUnits(nation);
		assertEquals(5, units.size());
 		City city = cities.get(0);
		sectorDao.merge(city);
		sectorDaoService.updateCity(nation, city.getCoords(), UpdateCityField.NEXT_BUILD, null, UnitType.SATELLITE, false, null);
		sectorDaoService.updateCity(nation, city.getCoords(), UpdateCityField.SWITCH_ON_TECH_CHANGE, null, null, true, null);
		cityBuilderService.buildUnit(city, now);
		nation.setTech(15.0);
		gameDao.merge(nation);
		cityBuilderService.switchCityProductionIfTechPermits(city, now);
		assertEquals(UnitType.SATELLITE, city.getBuild());
		units = unitDao.getUnits(nation);
		assertEquals(6, units.size());
		assertEquals(UnitType.INFANTRY, units.get(5).getType());
		context.assertIsSatisfied();
	}	

	@Test
	public void setNextBuildInfAndBuild() {
		createNation();
		context.checking(new Expectations() {
			{
				exactly(2).of(eventTimer).schedule((Event)with(a(CityBuildEvent.class)));
				exactly(6).of(eventTimer).schedule((Event)with(a(UnitUpdateEvent.class)));
			}
		});

		worldManager.addPlayerToMap(2, nation);
		
		List<City> cities = sectorDao.getCities(nation);
		List<Unit> units = unitDao.getUnits(nation);
		assertEquals(5, units.size());
 		City city = cities.get(0);
		sectorDao.merge(city);
		sectorDaoService.updateCity(nation, city.getCoords(), UpdateCityField.NEXT_BUILD, null, UnitType.INFANTRY, false, null);
		sectorDaoService.updateCity(nation, city.getCoords(), UpdateCityField.SWITCH_ON_TECH_CHANGE, null, null, true, null);
		nation.setTech(15.0);
		gameDao.merge(nation);
		cityBuilderService.switchCityProductionIfTechPermits(city, now);
		cityBuilderService.buildUnit(city, now);
		units = unitDao.getUnits(nation);
		assertEquals(6, units.size());
		context.assertIsSatisfied();
	}	
}
