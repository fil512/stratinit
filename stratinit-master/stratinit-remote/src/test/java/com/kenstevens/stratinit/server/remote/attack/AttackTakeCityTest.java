package com.kenstevens.stratinit.server.remote.attack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.remote.TwoPlayerBase;
import com.kenstevens.stratinit.server.remote.event.EventQueue;
import com.kenstevens.stratinit.server.remote.move.cost.MoveCost;
import com.kenstevens.stratinit.server.remote.move.cost.MoveType;
import com.kenstevens.stratinit.type.CityType;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

@SuppressWarnings("unused")
public class AttackTakeCityTest extends TwoPlayerBase {
	@Autowired
	private EventQueue eventQueue;
	
	private static final SectorCoords ATTACK_FROM = new SectorCoords(1, 1);
	private static final SectorCoords LAND = new SectorCoords(2, 1);
	private static final SectorCoords CITY = new SectorCoords(2, 2);
	private static final SectorCoords BESIDE = new SectorCoords(3,2);
	
	@Test
	public void takeCityFromTransport() {
		Unit inf = unitDaoService.buildUnit(nationMe, BESIDE,
				UnitType.INFANTRY);
		Unit trans = unitDaoService.buildUnit(nationMe, BESIDE,
				UnitType.TRANSPORT);
		sectorDaoService.captureCity(nationThem, CITY);
		declareWar();
		Result<MoveCost> result = moveUnits(makeUnitList(inf), CITY);
		assertResult(result);
		assertDamaged(result, inf);
		assertTookCity(result);
	}
	
	@Test
	public void takeNeutralCityFromTransportDamagedInf() {
		Unit inf = unitDaoService.buildUnit(nationMe, BESIDE,
				UnitType.INFANTRY);
		inf.damage(1);
		Unit trans = unitDaoService.buildUnit(nationMe, BESIDE,
				UnitType.TRANSPORT);
		Result<MoveCost> result = moveUnits(
				makeUnitList(inf), CITY);
		assertResult(result);
		inf = unitDao.findUnit(inf.getId());
		assertNull(inf);
		List<Unit> units = unitDao.getUnits(nationMe);
		assertFalse(units.contains(inf));
		assertTookCity(result);
	}
	
	@Test
	public void takeNeutralCityFromTransport() {
		Unit inf = unitDaoService.buildUnit(nationMe, BESIDE,
				UnitType.INFANTRY);
		Unit trans = unitDaoService.buildUnit(nationMe, BESIDE,
				UnitType.TRANSPORT);
		Result<MoveCost> result = moveUnits(
				makeUnitList(inf), CITY);
		assertResult(result);
		assertDamaged(result, inf);
		assertTookCity(result);
	}
	
	@Test
	public void takeNeutralCity2FromTransport() {
		Unit inf = unitDaoService.buildUnit(nationMe, BESIDE,
				UnitType.INFANTRY);
		Unit inf2 = unitDaoService.buildUnit(nationMe, BESIDE,
				UnitType.INFANTRY);
		Unit trans = unitDaoService.buildUnit(nationMe, BESIDE,
				UnitType.TRANSPORT);
		Result<MoveCost> result = moveUnits(
				makeUnitList(new Unit[] {inf, inf2}), CITY);
		assertResult(result);
		assertDamaged(result, inf);
		assertTookCity(result);
	}
	
	@Test
	public void cannotTakeOccuppiedCityFromTransport() {
		Unit inf = unitDaoService.buildUnit(nationMe, BESIDE,
				UnitType.INFANTRY);
		Unit trans = unitDaoService.buildUnit(nationMe, BESIDE,
				UnitType.TRANSPORT);
		Unit einf = unitDaoService.buildUnit(nationThem, CITY,
				UnitType.TRANSPORT);
		sectorDaoService.captureCity(nationThem, CITY);
		declareWar();
		Result<MoveCost> result = moveUnits(makeUnitList(inf), CITY);
		assertFalseResult(result);
		inf = unitDao.findUnit(inf.getId());
		assertEquals(BESIDE, inf.getCoords());
		assertNoTookCity(result);
	}
	
	@Test
	public void cannotTakeCityWithPlane() {
		Unit plane1 = unitDaoService.buildUnit(nationMe, BESIDE,
				UnitType.FIGHTER);
		Unit plane2 = unitDaoService.buildUnit(nationMe, BESIDE,
				UnitType.FIGHTER);
		Unit plane3 = unitDaoService.buildUnit(nationMe, BESIDE,
				UnitType.FIGHTER);
		Unit zeppelin = unitDaoService.buildUnit(nationThem, CITY,
				UnitType.ZEPPELIN);
		sectorDaoService.captureCity(nationThem, CITY);
		declareWar();
		Result<MoveCost> result = moveUnits(
				makeUnitList(new Unit[] {plane1, plane2, plane3}), CITY);
		assertResult(result);
		assertNoTookCity(result);
	}


	@Test
	public void toLandFromTransport() {
		Unit inf = unitDaoService.buildUnit(nationMe, BESIDE,
				UnitType.INFANTRY);
		Unit trans = unitDaoService.buildUnit(nationMe, BESIDE,
				UnitType.TRANSPORT);
		sectorDaoService.captureCity(nationThem, CITY);
		Result<MoveCost> result = moveUnits(
				makeUnitList(inf), LAND);
		assertResult(result);
		inf = unitDao.findUnit(inf.getId());
		assertEquals(LAND, inf.getCoords());
	}
	private void assertTookCity(Result<MoveCost> result) {
		List<City> cities = sectorDao.getCities(nationMe);
		Sector citySector = testWorld.getSector(CITY);
		City city = sectorDao.getCity(citySector);
		assertTrue(result.toString(), cities.contains(city));
		assertEquals(MoveType.TAKE_CITY, result.getValue().getMoveType());
	}

	private void assertNoTookCity(Result<MoveCost> result) {
		List<City> cities = sectorDao.getCities(nationMe);
		Sector citySector = testWorld.getSector(CITY);
		City city = sectorDao.getCity(citySector);
		assertFalse(result.toString(), cities.contains(city));
		assertFalse(MoveType.TAKE_CITY.equals(result.getValue().getMoveType()));
	}

	@Test
	public void takeCity() {
		Unit inf = unitDaoService.buildUnit(nationMe, ATTACK_FROM,
				UnitType.INFANTRY);
		sectorDaoService.captureCity(nationThem, CITY);
		declareWar();
		Result<MoveCost> result = moveUnits(
				makeUnitList(inf), CITY);
		assertResult(result);
		assertDamaged(result, inf);
		assertTookCity(result);
		Sector citySector = testWorld.getSector(CITY);
		City city = sectorDao.getCity(citySector);
		assertEquals(CityType.BASE, city.getType());
		assertEquals(UnitType.BASE, city.getBuild());
		assertFalse(eventQueue.cancel(city));
	}

	@Test
	public void takeCityNeutral() {
		Unit inf = unitDaoService.buildUnit(nationMe, ATTACK_FROM,
				UnitType.INFANTRY);
		Result<MoveCost> result = moveUnits(
				makeUnitList(inf), CITY);
		assertResult(result);
		assertDamaged(result, inf);
		assertTookCity(result);
		Sector citySector = testWorld.getSector(CITY);
		City city = sectorDao.getCity(citySector);
		assertEquals(CityType.BASE, city.getType());
		assertEquals(UnitType.BASE, city.getBuild());
		assertFalse(eventQueue.cancel(city));
	}

	@Test
	public void takeCityThenMoveIn() {
		Unit inf = unitDaoService.buildUnit(nationMe, ATTACK_FROM,
				UnitType.INFANTRY);
		Unit inf2 = unitDaoService.buildUnit(nationMe, ATTACK_FROM,
				UnitType.INFANTRY);
		sectorDaoService.captureCity(nationThem, CITY);
		declareWar();
		Result<MoveCost> result = moveUnits(
				makeUnitList(inf), CITY);
		assertResult(result);
		assertTookCity(result);
		result = moveUnits(
				makeUnitList(inf2), CITY);
		assertResult(result);
		inf2 = unitDao.findUnit(inf2.getId());
		assertEquals(CITY, inf2.getCoords());
	}
	
	@Test
	public void planeAndInfTakeCity() {
		Unit plane = unitDaoService.buildUnit(nationMe, ATTACK_FROM,
				UnitType.FIGHTER);
		Unit inf = unitDaoService.buildUnit(nationMe, ATTACK_FROM,
				UnitType.INFANTRY);
		sectorDaoService.captureCity(nationThem, CITY);
		declareWar();
		Result<MoveCost> result = moveUnits(
				makeUnitList(new Unit[] {plane, inf}), CITY);
		assertResult(result);
		assertDamaged(result, inf);
		assertTookCity(result);
		assertEquals(ATTACK_FROM, plane.getCoords());
	}

}
