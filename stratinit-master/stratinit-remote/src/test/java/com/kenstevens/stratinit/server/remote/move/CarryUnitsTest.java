package com.kenstevens.stratinit.server.remote.move;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.remote.StratInitWebBase;
import com.kenstevens.stratinit.server.remote.move.cost.MoveCost;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

public class CarryUnitsTest extends StratInitWebBase {
	@Autowired
	protected SectorDaoService sectorDaoServiceImpl;
	private static final SectorCoords PORT = new SectorCoords(2, 2);
	private static final SectorCoords BY_PORT = new SectorCoords(1, 2);
	protected static final SectorCoords SEA1 = new SectorCoords(4, 0);
	protected static final SectorCoords SEA2 = new SectorCoords(4, 1);
	protected static final SectorCoords BETWEEN = new SectorCoords(1, 1);
	protected static final SectorCoords TARGET = new SectorCoords(0, 0);

	@Before
	public void doJoinGame() {
		joinGamePlayerMe();
	}

	// TODO assertEquals arguments are backwards

	@Test
	public void carrierDragsMostFighters() {
		Unit[] fighters = new Unit[CARRIER_CAPACITY + 1];
		for (int i = 0; i < CARRIER_CAPACITY + 1; ++i) {
			fighters[i] = unitDaoService.buildUnit(nationMe, SEA1,
					UnitType.FIGHTER);
		}
		Unit carrier = unitDaoService.buildUnit(nationMe, SEA1,
				UnitType.CARRIER);
		Result<MoveCost> result = moveUnits(makeUnitList(carrier), SEA2);
		assertResult(result);
		assertEquals(result.toString(), carrier.getCoords(), SEA2);
		for (int i = 0; i < CARRIER_CAPACITY; ++i) {
			assertEquals(result.toString(), fighters[i].getCoords(), SEA2);
		}
		assertEquals(result.toString(), fighters[CARRIER_CAPACITY].getCoords(),
				SEA1);
	}

	@Test
	public void carrierNoDragsTank() {
		Unit[] tank = new Unit[TRANSPORT_CAPACITY + 1];
		for (int i = 0; i < TRANSPORT_CAPACITY + 1; ++i) {
			tank[i] = unitDaoService.buildUnit(nationMe, PORT, UnitType.TANK);
		}
		sectorDaoServiceImpl.captureCity(nationMe, PORT);
		Unit carrier = unitDaoService.buildUnit(nationMe, PORT,
				UnitType.CARRIER);
		Result<MoveCost> result = moveUnits(makeUnitList(carrier), SEA1);
		assertResult(result);
		assertEquals(result.toString(), carrier.getCoords(), SEA1);
		for (int i = 0; i < TRANSPORT_CAPACITY; ++i) {
			assertEquals(result.toString(), tank[i].getCoords(), PORT);
		}
		assertEquals(result.toString(), tank[TRANSPORT_CAPACITY].getCoords(),
				PORT);
	}

	@Test
	public void transportDragsMostInf() {
		Unit[] inf = new Unit[TRANSPORT_CAPACITY + 1];
		for (int i = 0; i < TRANSPORT_CAPACITY + 1; ++i) {
			inf[i] = unitDaoService
					.buildUnit(nationMe, PORT, UnitType.INFANTRY);
		}
		sectorDaoServiceImpl.captureCity(nationMe, PORT);
		Unit transport = unitDaoService.buildUnit(nationMe, PORT,
				UnitType.TRANSPORT);
		Result<MoveCost> result = moveUnits(makeUnitList(transport), SEA1);
		assertResult(result);
		assertEquals(result.toString(), transport.getCoords(), SEA1);
		for (int i = 0; i < TRANSPORT_CAPACITY; ++i) {
			assertEquals(result.toString(), inf[i].getCoords(), SEA1);
		}
		assertEquals(result.toString(), inf[TRANSPORT_CAPACITY].getCoords(),
				PORT);
	}

	@Test
	public void helicopterDragsSomeInf() {
		Unit[] inf = new Unit[HELICOPTER_CAPACITY + 1];
		for (int i = 0; i < HELICOPTER_CAPACITY + 1; ++i) {
			inf[i] = unitDaoService
					.buildUnit(nationMe, PORT, UnitType.INFANTRY);
		}
		sectorDaoServiceImpl.captureCity(nationMe, PORT);
		Unit heli = unitDaoService.buildUnit(nationMe, PORT,
				UnitType.HELICOPTER);
		Result<MoveCost> result = moveUnits(makeUnitList(heli), SEA1);
		assertResult(result);
		assertEquals(result.toString(), heli.getCoords(), SEA1);
		for (int i = 0; i < HELICOPTER_CAPACITY; ++i) {
			assertEquals(result.toString(), inf[i].getCoords(), SEA1);
		}
		assertEquals(result.toString(), inf[HELICOPTER_CAPACITY].getCoords(),
				PORT);
	}

	@Test
	public void helicopterDragsInfFromCity() {
		Unit inf = unitDaoService.buildUnit(nationMe, PORT, UnitType.INFANTRY);
		sectorDaoServiceImpl.captureCity(nationMe, PORT);
		Unit heli = unitDaoService.buildUnit(nationMe, PORT,
				UnitType.HELICOPTER);
		Result<MoveCost> result = moveUnits(makeUnitList(heli), SEA1);
		assertResult(result);
		assertEquals(result.toString(), heli.getCoords(), SEA1);
		assertEquals(result.toString(), inf.getCoords(), SEA1);
	}

	@Test
	public void helicopterNoDragsHurtInfFromCity() {
		Unit inf = unitDaoService.buildUnit(nationMe, PORT, UnitType.INFANTRY);
		inf.damage(4);
		unitDao.persist(inf);
		sectorDaoServiceImpl.captureCity(nationMe, PORT);
		Unit heli = unitDaoService.buildUnit(nationMe, PORT,
				UnitType.HELICOPTER);
		Result<MoveCost> result = moveUnits(makeUnitList(heli), SEA1);
		assertResult(result);
		assertEquals(result.toString(), heli.getCoords(), SEA1);
		assertFalse(result.toString(), inf.getCoords().equals(SEA1));
		assertEquals(result.toString(), inf.getCoords(), PORT);
	}

	@Test
	public void helicopterNoDragsNoMobInfFromCity() {
		Unit inf = unitDaoService.buildUnit(nationMe, PORT, UnitType.INFANTRY);
		inf.decreaseMobility(inf.getMobility());
		unitDao.persist(inf);
		sectorDaoServiceImpl.captureCity(nationMe, PORT);
		Unit heli = unitDaoService.buildUnit(nationMe, PORT,
				UnitType.HELICOPTER);
		Result<MoveCost> result = moveUnits(makeUnitList(heli), SEA1);
		assertResult(result);
		assertEquals(result.toString(), heli.getCoords(), SEA1);
		assertFalse(result.toString(), inf.getCoords().equals(SEA1));
		assertEquals(result.toString(), inf.getCoords(), PORT);
	}

	@Test
	public void helicopterDragsNoFighters() {
		Unit[] fighter = new Unit[HELICOPTER_CAPACITY + 1];
		for (int i = 0; i < HELICOPTER_CAPACITY + 1; ++i) {
			fighter[i] = unitDaoService.buildUnit(nationMe, PORT,
					UnitType.FIGHTER);
		}
		sectorDaoServiceImpl.captureCity(nationMe, PORT);
		Unit heli = unitDaoService.buildUnit(nationMe, PORT,
				UnitType.HELICOPTER);
		Result<MoveCost> result = moveUnits(makeUnitList(heli), SEA1);
		assertResult(result);
		assertEquals(result.toString(), heli.getCoords(), SEA1);
		for (int i = 0; i < HELICOPTER_CAPACITY; ++i) {
			assertEquals(result.toString(), fighter[i].getCoords(), PORT);
		}
		assertEquals(result.toString(),
				fighter[HELICOPTER_CAPACITY].getCoords(), PORT);
	}

	@Test
	public void engDragsSomeMissile() {
		Unit[] missile = new Unit[ENGINEER_CAPACITY + 1];
		for (int i = 0; i < ENGINEER_CAPACITY + 1; ++i) {
			missile[i] = unitDaoService.buildUnit(nationMe, PORT,
					UnitType.ICBM_1);
		}
		sectorDaoServiceImpl.captureCity(nationMe, PORT);
		Unit eng = unitDaoService.buildUnit(nationMe, PORT, UnitType.ENGINEER);
		Result<MoveCost> result = moveUnits(makeUnitList(eng), BY_PORT);
		assertResult(result);
		assertEquals(result.toString(), eng.getCoords(), BY_PORT);
		for (int i = 0; i < ENGINEER_CAPACITY; ++i) {
			assertEquals(result.toString(), missile[i].getCoords(), BY_PORT);
		}
		assertEquals(result.toString(), missile[ENGINEER_CAPACITY].getCoords(),
				PORT);
	}

	@Test
	public void subDragsSomeMissile() {
		Unit[] missile = new Unit[SUB_CAPACITY + 1];
		for (int i = 0; i < SUB_CAPACITY + 1; ++i) {
			missile[i] = unitDaoService.buildUnit(nationMe, PORT,
					UnitType.ICBM_1);
		}
		sectorDaoServiceImpl.captureCity(nationMe, PORT);
		Unit sub = unitDaoService.buildUnit(nationMe, PORT, UnitType.SUBMARINE);
		Result<MoveCost> result = moveUnits(makeUnitList(sub), SEA1);
		assertResult(result);
		assertEquals(result.toString(), sub.getCoords(), SEA1);
		for (int i = 0; i < SUB_CAPACITY; ++i) {
			assertEquals(result.toString(), missile[i].getCoords(), SEA1);
		}
		assertEquals(result.toString(), missile[SUB_CAPACITY].getCoords(), PORT);
	}

	@Test
	public void cargoDragsSomeInf() {
		Unit cargo = unitDaoService.buildUnit(nationMe, PORT,
				UnitType.CARGO_PLANE);
		Unit[] inf = new Unit[CARGO_CAPACITY + 1];
		for (int i = 0; i < CARGO_CAPACITY + 1; ++i) {
			inf[i] = unitDaoService
					.buildUnit(nationMe, PORT, UnitType.INFANTRY);
		}
		sectorDaoServiceImpl.captureCity(nationMe, PORT);
		Result<MoveCost> result = moveUnits(makeUnitList(cargo), TARGET);
		assertResult(result);
		assertEquals(result.toString(), cargo.getCoords(), TARGET);
		for (int i = 0; i < CARGO_CAPACITY; ++i) {
			assertEquals(i + ": " + result.toString(), TARGET, inf[i].getCoords());
		}
		assertEquals(result.toString(), PORT, inf[CARGO_CAPACITY].getCoords());
	}

	@Test
	public void cargoNoScoop() {
		Unit cargo = unitDaoService.buildUnit(nationMe, PORT,
				UnitType.CARGO_PLANE);
		Unit infPort = unitDaoService.buildUnit(nationMe, PORT,
				UnitType.INFANTRY);
		Unit infMiddle = unitDaoService.buildUnit(nationMe, BETWEEN,
				UnitType.INFANTRY);
		sectorDaoServiceImpl.captureCity(nationMe, PORT);
		Result<MoveCost> result = moveUnits(makeUnitList(cargo), TARGET);
		assertResult(result);
		assertEquals(result.toString(), TARGET, cargo.getCoords());
		assertEquals(result.toString(), TARGET, infPort.getCoords());
		assertEquals(result.toString(), BETWEEN, infMiddle.getCoords());
	}

}
