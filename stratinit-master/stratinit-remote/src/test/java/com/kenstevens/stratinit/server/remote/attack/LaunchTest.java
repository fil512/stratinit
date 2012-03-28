package com.kenstevens.stratinit.server.remote.attack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.dto.SIRelation;
import com.kenstevens.stratinit.dto.SISector;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.model.SectorSeen;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.remote.TwoPlayerBase;
import com.kenstevens.stratinit.server.remote.move.cost.MoveCost;
import com.kenstevens.stratinit.server.remote.move.cost.MoveType;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.SectorType;
import com.kenstevens.stratinit.type.UnitType;

public class LaunchTest extends TwoPlayerBase {
	private static final SectorCoords CITY = new SectorCoords(4, 1);
	private static final SectorCoords TWEENCITY = new SectorCoords(8,5);
	private static final SectorCoords ECITY = new SectorCoords(8, 4);
	private static final SectorCoords ECITYW = new SectorCoords(7, 4);
	private static final SectorCoords BYCITY = new SectorCoords(8-Constants.SATELLITE_SIGHT, 6);
	private static final SectorCoords OUTCITY = new SectorCoords(8-Constants.SATELLITE_SIGHT-1, 6);

	@Test
	public void icbm3KillsCitiesAndUnits() {
		Unit icbm = unitDaoService.buildUnit(nationMe, CITY,
				UnitType.ICBM_3);

		assertNoDevastation();
		Result<MoveCost> result = moveUnits(makeUnitList(icbm), ECITY);
		assertResult(result);
		assertDevastation();
		assertEquals(MoveType.LAUNCH_ICBM, result.getValue().getMoveType());
	}

	@Test
	public void icbm2KillsCitiesAndUnits() {
		Unit icbm = unitDaoService.buildUnit(nationMe, CITY,
				UnitType.ICBM_2);

		assertNoDevastation();
		Result<MoveCost> result = moveUnits(makeUnitList(icbm), TWEENCITY);
		assertResult(result);
		assertDevastation();
	}

	private void assertNoDevastation() {
		List<City> cities = sectorDao.getCities(nationThem);
		assertEquals(2, cities.size());
		List<Unit> units = unitDao.getUnits(nationThem);
		assertEquals(5, units.size());
	}

	private void assertDevastation() {
		List<City> cities;
		List<Unit> units;
		cities = sectorDao.getCities(nationThem);
		assertEquals(0, cities.size());
		units = unitDao.getUnits(nationThem);
		assertEquals(0, units.size());
		Sector sector = testWorld.getSector(ECITY);
		assertEquals(SectorType.WASTELAND, sector.getType());
	}
	
	@Test
	public void satSeesSectors() {
		Unit sat = unitDaoService.buildUnit(nationMe, CITY,
				UnitType.SATELLITE);
		List<SISector> sseen = stratInit.getSectors().getValue();
		List<SICity> cseen = stratInit.getSeenCities().getValue();
		List<SIUnit> useen = stratInit.getSeenUnits().getValue();
		Result<MoveCost> result = moveUnits(makeUnitList(sat), ECITY);
		assertResult(result);
		List<SISector> sseen2 = stratInit.getSectors().getValue();
		List<SICity> cseen2 = stratInit.getSeenCities().getValue();
		List<SIUnit> useen2 = stratInit.getSeenUnits().getValue();
		assertTrue(sseen2.size() > sseen.size());
		assertTrue(cseen2.size() > cseen.size());
		assertTrue(useen2.size() > useen.size());
		assertEquals(MoveType.LAUNCH_SATELLITE, result.getValue().getMoveType());
	}
	
	@Test
	public void satSeesUnits() {
		Unit sat = unitDaoService.buildUnit(nationMe, CITY,
				UnitType.SATELLITE);
		List<SIUnit> unitsSeen = stratInit.getSeenUnits().getValue();
		assertEquals(0, unitsSeen.size());
		Result<MoveCost> result = moveUnits(makeUnitList(sat), BYCITY);
		assertResult(result);
		unitsSeen = stratInit.getSeenUnits().getValue();
		assertEquals(5, unitsSeen.size());
	}
	
	@Test
	public void satSeesMovedUnits() {
		Unit sat = unitDaoService.buildUnit(nationMe, CITY,
				UnitType.SATELLITE);
		Unit inf = unitDaoService.buildUnit(nationThem, ECITY, UnitType.INFANTRY);
		List<SIUnit> unitsSeen = stratInit.getSeenUnits().getValue();
		assertEquals(0, unitsSeen.size());
		Result<MoveCost> result = moveUnits(makeUnitList(sat), OUTCITY);
		assertResult(result);
		unitsSeen = stratInit.getSeenUnits().getValue();
		assertEquals(0, unitsSeen.size());
		result = moveUnits(nationThem, makeUnitList(inf), ECITYW);
		assertResult(result);
		inf = unitDao.findUnit(inf.getId());
		assertEquals(ECITYW, inf.getCoords());
		Collection<SectorSeen> sectorsSeen = sectorDao.getSectorsSeen(nationMe);
		assertTrue(contains(sectorsSeen, ECITYW));
		unitsSeen = stratInit.getSeenUnits().getValue();
		assertEquals(1, unitsSeen.size());
	}
	private boolean contains(Collection<SectorSeen> sectorsSeen,
			SectorCoords coords) {
		for (SectorSeen sectorSeen : sectorsSeen) {
			if (sectorSeen.getCoords().equals(coords)) {
				return true;
			}
		}
		return false;

	}

	@Test
	public void allyNoSeesSectors() {
		List<SISector> sseen = stratInit.getSectors().getValue();
		List<SICity> cseen = stratInit.getSeenCities().getValue();
		List<SIUnit> useen = stratInit.getSeenUnits().getValue();
		Result<SIRelation> result = stratInit.setRelation(nationThemId, RelationType.ALLIED);
		assertResult(result);
		List<SISector> sseen2 = stratInit.getSectors().getValue();
		List<SICity> cseen2 = stratInit.getSeenCities().getValue();
		List<SIUnit> useen2 = stratInit.getSeenUnits().getValue();
		assertFalse(sseen2.size() > sseen.size());
		assertFalse(cseen2.size() > cseen.size());
		assertFalse(useen2.size() > useen.size());
	}

	@Test
	public void alliedSeesSectors() {
		List<SISector> sseen = stratInit.getSectors().getValue();
		List<SICity> cseen = stratInit.getSeenCities().getValue();
		List<SIUnit> useen = stratInit.getSeenUnits().getValue();
		setAuthentication(PLAYER_THEM_NAME);
		Result<SIRelation> result = stratInit.setRelation(nationMeId, RelationType.ALLIED);
		assertResult(result);
		setAuthentication(PLAYER_ME_NAME);
		result = stratInit.setRelation(nationThemId, RelationType.ALLIED);
		assertResult(result);
		List<SISector> sseen2 = stratInit.getSectors().getValue();
		List<SICity> cseen2 = stratInit.getSeenCities().getValue();
		List<SIUnit> useen2 = stratInit.getSeenUnits().getValue();
		assertTrue(sseen2.size() > sseen.size());
		assertTrue(cseen2.size() > cseen.size());
		assertTrue(useen2.size() > useen.size());
	}

}
