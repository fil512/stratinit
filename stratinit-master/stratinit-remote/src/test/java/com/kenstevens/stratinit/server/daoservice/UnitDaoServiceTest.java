package com.kenstevens.stratinit.server.daoservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitSeen;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.server.remote.TwoPlayerBase;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

public class UnitDaoServiceTest extends TwoPlayerBase {
	@Autowired protected SectorDaoService sectorDaoService;
	private static final SectorCoords PORT = new SectorCoords(2, 2);
	private static final SectorCoords SEA = new SectorCoords(3, 2);
	private static final SectorCoords TOP = new SectorCoords(0, 0);
	private static final SectorCoords BOTTOM = new SectorCoords(0, 3);

	@Test
	public void disableUnitSeen() {
		unitDaoService.buildUnit(nationMe, SEA,
				UnitType.DESTROYER);
		sectorDaoService.captureCity(nationThem, PORT);
		Unit inf = unitDaoService.buildUnit(nationThem, PORT, UnitType.INFANTRY);
		UnitSeen unitSeen = unitDao.findUnitSeen(nationMe, inf);
		assertNotNull(unitSeen);
		unitDaoService.disable(unitSeen.getUnitSeenPK());
	}
	
	@Test
	public void updateUnit() {
		Unit dest = unitDaoService.buildUnit(nationMe, SEA,
				UnitType.DESTROYER);
		sectorDaoService.captureCity(nationMe, PORT);
		dest.setMobility(0);
		dest.setHp(1);
		unitDaoService.updateUnit(dest, new Date());
		assertEquals(dest.getUnitBase().getMobility(), dest.getMobility());
		assertEquals(1+dest.getUnitBase().getHp()*Constants.SUPPLY_HEAL_PERCENT/100, dest.getHp());
	}

	@Test
	public void updateUnitTwiceFirstTimeCantMoveSecondTimeCanMove() {
		Unit inf = unitDaoService.buildUnit(nationMe, TOP,
				UnitType.INFANTRY);
		inf.setMobility(0);
		unitDaoService.setUnitMove(inf, BOTTOM);
		unitDaoService.updateUnit(inf, new Date());
		assertEquals(inf.getUnitBase().getMobility(), inf.getMobility());
		assertEquals(TOP, inf.getCoords());
		assertEquals(BOTTOM, inf.getUnitMove().getCoords());
		unitDaoService.updateUnit(inf, new Date());
		assertEquals(inf.getUnitBase().getMobility()*2 - TOP.distanceTo(testWorld, BOTTOM), inf.getMobility());
		assertEquals(BOTTOM, inf.getCoords());
		assertNull(inf.getUnitMove());
	}

	@Test
	public void updateUnitWithMove() {
		Unit dest = unitDaoService.buildUnit(nationMe, SEA,
				UnitType.DESTROYER);
		sectorDaoService.captureCity(nationMe, PORT);
		dest.setMobility(0);
		unitDaoService.setUnitMove(dest, PORT);
		unitDaoService.updateUnit(dest, new Date());
		assertEquals(dest.getUnitBase().getMobility() - 1, dest.getMobility());
		assertEquals(PORT, dest.getCoords());
	}

	@Test
	public void updateXportWithMove() {
		Unit xport = unitDaoService.buildUnit(nationMe, SEA,
				UnitType.TRANSPORT);
		Unit inf = unitDaoService.buildUnit(nationMe, SEA,
				UnitType.INFANTRY);
		sectorDaoService.captureCity(nationMe, PORT);
		xport.setMobility(0);
		unitDaoService.setUnitMove(xport, PORT);
		unitDaoService.updateUnit(xport, new Date());
		assertEquals(xport.getUnitBase().getMobility() - 1, xport.getMobility());
		assertEquals(PORT, xport.getCoords());
		assertEquals(PORT, inf.getCoords());
	}

	@Test
	public void getCargoPassengersTest() {
		UnitType holderType = UnitType.CARGO_PLANE;
		SectorCoords coords = SEA;
		List<Unit> units = Lists.newArrayList();

		List<Unit> passengers = getPassengers(holderType, coords, units);
		assertAllButOnePassengers(holderType, units, passengers);
	}

	@Test
	public void getCargoPassengersInAirportTest() {
		UnitType holderType = UnitType.CARGO_PLANE;
		SectorCoords coords = PORT;
		sectorDaoService.captureCity(nationMe, PORT);
		setBuild(PORT, UnitType.FIGHTER);
		List<Unit> units = Lists.newArrayList();

		List<Unit> passengers = getPassengers(holderType, coords, units);
		assertAllButOnePassengers(holderType, units, passengers);

	}


	private void assertAllButOnePassengers(UnitType holderType,
			List<Unit> units, List<Unit> passengers) {
		int capacity = getCapacity(holderType);
		for (int i = 0; i < capacity; ++i) {
			assertTrue(passengers.contains(units.get(i)));
		}
		assertFalse(passengers.contains(units.get(capacity)));
		
	}

	@Test
	public void getXportPassengersTest() {
		UnitType holderType = UnitType.TRANSPORT;
		SectorCoords coords = SEA;
		List<Unit> units = Lists.newArrayList();

		List<Unit> passengers = getPassengers(holderType, coords, units);
		assertAllButOnePassengers(holderType, units, passengers);
	}

	@Test
	public void getXportPassengersInPortTest() {
		UnitType holderType = UnitType.TRANSPORT;
		SectorCoords coords = PORT;
		sectorDaoService.captureCity(nationMe, PORT);
		setBuild(PORT, UnitType.TRANSPORT);
		List<Unit> units = Lists.newArrayList();

		List<Unit> passengers = getPassengers(holderType, coords, units);
		assertAllButOnePassengers(holderType, units, passengers);
	}

	private List<Unit> getPassengers(UnitType holderType,
			SectorCoords coords, List<Unit> units) {
		int capacity = getCapacity(holderType);
		Unit holder = unitDaoService.buildUnit(nationThem, coords,
				holderType);
		for (int i = 0; i < capacity + 1; ++i) {
			Unit inf = unitDaoService.buildUnit(nationThem, coords,
				UnitType.INFANTRY);
			units.add(inf);
		}
		WorldView WORLD = sectorDaoService.getAllWorldView(nationMe);
		WorldSector worldSector = WORLD.getWorldSector(coords);

		List<Unit> passengers = unitDaoService.getPassengers(holder, worldSector);
		return passengers;
	}
}
