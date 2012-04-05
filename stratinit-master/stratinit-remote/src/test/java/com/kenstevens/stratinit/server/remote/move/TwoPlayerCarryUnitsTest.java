package com.kenstevens.stratinit.server.remote.move;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.model.MoveCost;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.remote.TwoPlayerBase;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

public class TwoPlayerCarryUnitsTest extends TwoPlayerBase {
	@Autowired
	protected SectorDaoService sectorDaoServiceImpl;
	protected static final SectorCoords SEA1 = new SectorCoords(4, 0);
	protected static final SectorCoords SEA2 = new SectorCoords(4, 1);
	public static final SectorCoords PORT = new SectorCoords(2, 2);

	private List<Unit> attackHolder(UnitType holderType,
			SectorCoords defCoords) {
		int capacity = getCapacity(holderType);
		Unit holder = unitDaoService.buildUnit(nationThem, defCoords,
				holderType);
		List<Unit> units = Lists.newArrayList();

		for (int i = 0; i < capacity + 1; ++i) {
			Unit inf = unitDaoService.buildUnit(nationThem, defCoords,
					UnitType.INFANTRY);
			units.add(inf);
		}
		holder.setHp(1);
		Unit fighter = unitDaoService.buildUnit(nationMe, SEA2,
				UnitType.FIGHTER);
		fighter.setHp(20); // flak no kill me
		declareWar();
		Result<MoveCost> result = moveUnits(makeUnitList(fighter), defCoords);
		assertResult(result);
		return units;
	}

	@Test
	public void cargoKillPassengers() {
		List<Unit> units = attackHolder(UnitType.CARGO_PLANE, SEA1);
		assertAllButOneDead(UnitType.CARGO_PLANE, units);
	}
	
	@Test
	public void cargoNoKillPassengersInAirport() {
		sectorDaoService.captureCity(nationThem, PORT);
		setBuild(PORT, UnitType.FIGHTER);
		List<Unit> units = attackHolder(UnitType.CARGO_PLANE, PORT);
		assertAllAlive(UnitType.CARGO_PLANE, units);
	}

	private void assertAllAlive(UnitType unitType, List<Unit> units) {
		int capacity = getCapacity(unitType);

		for (int i = 0; i < capacity; ++i) {
			assertTrue(units.get(i).isAlive());
		}
		assertTrue(units.get(capacity).isAlive());
	}
	

	private void assertAllButOneDead(UnitType unitType, List<Unit> units) {
		int capacity = getCapacity(unitType);

		for (int i = 0; i < capacity; ++i) {
			assertFalse(units.get(i).isAlive());
		}
		assertTrue(units.get(capacity).isAlive());
	}


	@Test
	public void heliKillPassengers() {
		List<Unit> units = attackHolder(UnitType.HELICOPTER, SEA1);
		assertAllButOneDead(UnitType.HELICOPTER, units);
	}

	@Test
	public void heliNoKillPassengersInAirport() {
		sectorDaoService.captureCity(nationThem, PORT);
		setBuild(PORT, UnitType.FIGHTER);
		List<Unit> units = attackHolder(UnitType.HELICOPTER, PORT);
		assertAllAlive(UnitType.HELICOPTER, units);
	}

	@Test
	public void xportKillPassengers() {
		List<Unit> units = attackHolder(UnitType.TRANSPORT, SEA1);
		assertAllButOneDead(UnitType.TRANSPORT, units);
	}

	@Test
	public void xportNoKillPassengersInPort() {
		sectorDaoService.captureCity(nationThem, PORT);
		setBuild(PORT, UnitType.FIGHTER);
		List<Unit> units = attackHolder(UnitType.TRANSPORT, PORT);
		assertAllAlive(UnitType.TRANSPORT, units);
	}
}
