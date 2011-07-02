package com.kenstevens.stratinit.server.remote.request.write;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.remote.TwoPlayerBase;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

public class BuildCityTest extends TwoPlayerBase {
	private static final SectorCoords LAND = new SectorCoords(1,1);
	private static final SectorCoords PORT = new SectorCoords(2,2);

	@Test
	public void buildCityNotEnoughMob() {
		Unit eng = unitDaoService.buildUnit(nationMe, LAND,
				UnitType.ENGINEER);
		List<City> cities = sectorDao.getCities(nationMe);
		assertEquals(2, cities.size());
		int cp = nationMe.getCommandPoints();
		Result<SIUpdate> result = stratInit.buildCity(makeUnitList(eng));
		assertEquals(cp, nationMe.getCommandPoints());
		assertFalseResult(result);
		assertTrue(result.toString().contains("mobility"));
		cities = sectorDao.getCities(nationMe);
		assertEquals(2, cities.size());
	}
	@Test
	public void buildCityNotEng() {
		Unit tank = unitDaoService.buildUnit(nationMe, LAND,
				UnitType.TANK);
		tank.setMobility(9);
		List<City> cities = sectorDao.getCities(nationMe);
		assertEquals(2, cities.size());
		Result<SIUpdate> result = stratInit.buildCity(makeUnitList(tank));
		assertFalseResult(result);
		cities = sectorDao.getCities(nationMe);
		assertEquals(2, cities.size());
	}
	@Test
	public void buildCity() {
		Unit eng = unitDaoService.buildUnit(nationMe, LAND,
				UnitType.ENGINEER);
		eng.setMobility(eng.getMaxMobility());
		List<City> cities = sectorDao.getCities(nationMe);
		assertEquals(2, cities.size());
		int cp = nationMe.getCommandPoints();
		Result<SIUpdate> result = stratInit.buildCity(makeUnitList(eng));
		assertEquals(cp - Constants.COMMAND_COST_BUILD_CITY, nationMe.getCommandPoints());
		assertResult(result);
		assertEquals(0, eng.getMobility());
		assertTrue(eng.isAlive());
		cities = sectorDao.getCities(nationMe);
		assertEquals(3, cities.size());
	}
	@Test
	public void buildCityBadLoc() {
		Unit eng = unitDaoService.buildUnit(nationMe, LAND,
				UnitType.ENGINEER);
		sectorDaoService.captureCity(nationThem, PORT);
		eng.setMobility(eng.getMaxMobility());
		List<City> cities = sectorDao.getCities(nationMe);
		assertEquals(2, cities.size());
		Result<SIUpdate> result = stratInit.buildCity(makeUnitList(eng));
		assertFalseResult(result);
		cities = sectorDao.getCities(nationMe);
		assertEquals(2, cities.size());
	}
}
