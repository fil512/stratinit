package com.kenstevens.stratinit.server.remote.supply;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.remote.StratInitWebBase;
import com.kenstevens.stratinit.server.remote.move.cost.MoveCost;
import com.kenstevens.stratinit.supply.Supply;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

public class SupplyTest extends StratInitWebBase {
	@Autowired protected SectorDaoService sectorDaoService;
	private static final SectorCoords IN_SUPPLY = new SectorCoords(0, 0);
	private static final SectorCoords OUT_SUPPLY = new SectorCoords(0, 12);
	private static final SectorCoords ENGINEER = new SectorCoords(0, 7);
	private static final SectorCoords SEA_IN_REACH = new SectorCoords(6, 0);
	private static final SectorCoords IN_TSUPPLY = new SectorCoords(9, 5);
	private static final SectorCoords OUT_TSUPPLY = new SectorCoords(9, 6);
	private static final SectorCoords SEA_OUT_OF_REACH = new SectorCoords(11, 0);
	private static final SectorCoords FAR_PORT = new SectorCoords(6, 2);
	private static final SectorCoords CLOSE_ENOUGH_TO_PORT = new SectorCoords(6, 3);
	private static final SectorCoords NEAR_PORT = new SectorCoords(6, 8);
	private static final SectorCoords PORT = new SectorCoords(7, 8);
	private static final SectorCoords FARXPORT = new SectorCoords(10, 13);
	private static final SectorCoords FARXPORT1 = new SectorCoords(11, 13);
	private static final SectorCoords FARLAND = new SectorCoords(9, 13);

	@Before
	public void doJoinGame() {
		joinGamePlayerMe();
	}

	@Test
	public void unitInSupply() {
		Unit inf = unitDaoService.buildUnit(nationMe, IN_SUPPLY,
				UnitType.INFANTRY);
		WorldView worldView = sectorDaoService.getSupplyWorldView(inf);
		Supply supply = new Supply(worldView);
		assertTrue(supply.inSupply(inf));
	}

	@Test
	public void unitOutSupply() {
		Unit inf = unitDaoService.buildUnit(nationMe, OUT_SUPPLY,
				UnitType.INFANTRY);
		WorldView worldView = sectorDaoService.getSupplyWorldView(inf);
		Supply supply = new Supply(worldView);
		assertFalse(supply.inSupply(inf));
	}

	@Test
	public void engineerSupplies() {
		Unit inf = unitDaoService.buildUnit(nationMe, OUT_SUPPLY,
				UnitType.INFANTRY);
		unitDaoService.buildUnit(nationMe, ENGINEER,
				UnitType.ENGINEER);
		WorldView worldView = sectorDaoService.getSupplyWorldView(inf);
		Supply supply = new Supply(worldView);
		assertTrue(supply.inSupply(inf));
	}

	@Test
	public void unitInTSupply() {
		unitDaoService.buildUnit(nationMe, SEA_IN_REACH, UnitType.TRANSPORT);

		Unit inf = unitDaoService.buildUnit(nationMe, IN_TSUPPLY,
				UnitType.INFANTRY);
		WorldView worldView = sectorDaoService.getSupplyWorldView(inf);
		Supply supply = new Supply(worldView);
		assertTrue(supply.inSupply(inf));
	}

	@Test
	public void unitOutTSupply() {
		unitDaoService.buildUnit(nationMe, SEA_IN_REACH, UnitType.SUPPLY);

		Unit inf = unitDaoService.buildUnit(nationMe, OUT_TSUPPLY,
				UnitType.INFANTRY);
		unitDao.persist(inf);
		WorldView worldView = sectorDaoService.getSupplyWorldView(inf);
		Supply supply = new Supply(worldView);
		assertFalse(supply.inSupply(inf));
	}

	@Test
	public void unitInSSupply() {
		unitDaoService.buildUnit(nationMe, SEA_IN_REACH, UnitType.SUPPLY);
		Unit inf = unitDaoService.buildUnit(nationMe, IN_TSUPPLY,
				UnitType.INFANTRY);
		WorldView worldView = sectorDaoService.getSupplyWorldView(inf);
		Supply supply = new Supply(worldView);
		assertTrue(supply.inSupply(inf));

	}

	@Test
	public void unitOutSSupply() {
		unitDaoService.buildUnit(nationMe, SEA_IN_REACH, UnitType.SUPPLY);
		Unit inf = unitDaoService.buildUnit(nationMe, OUT_TSUPPLY,
				UnitType.INFANTRY);
		WorldView worldView = sectorDaoService.getSupplyWorldView(inf);
		Supply supply = new Supply(worldView);
		assertFalse(supply.inSupply(inf));
	}

	@Test
	public void unitOnTransportInSupply() {
		Unit inf = unitDaoService.buildUnit(nationMe, SEA_OUT_OF_REACH,
				UnitType.INFANTRY);
		unitDaoService.buildUnit(nationMe, SEA_OUT_OF_REACH, UnitType.TRANSPORT);
		WorldView worldView = sectorDaoService.getSupplyWorldView(inf);
		Supply supply = new Supply(worldView);
		assertTrue(supply.inSupply(inf));
	}

	@Test
	public void shipNearPortSupply() {
		Unit dest = unitDaoService.buildUnit(nationMe, NEAR_PORT,
				UnitType.DESTROYER);
		WorldView worldView = sectorDaoService.getSupplyWorldView(dest);
		Supply supply = new Supply(worldView);
		assertFalse(supply.inSupply(dest));
		sectorDaoService.captureCity(nationMe, PORT);
		setBuild(PORT, UnitType.TRANSPORT);
		worldView = sectorDaoService.getSupplyWorldView(dest);
		supply = new Supply(worldView);
		assertTrue(supply.inSupply(dest));
	}

	@Test
	public void shipFarPortSupply() {
		Unit dest = unitDaoService.buildUnit(nationMe, FAR_PORT,
				UnitType.DESTROYER);
		WorldView worldView = sectorDaoService.getSupplyWorldView(dest);
		Supply supply = new Supply(worldView);
		assertFalse(supply.inSupply(dest));
		sectorDaoService.captureCity(nationMe, PORT);
		worldView = sectorDaoService.getSupplyWorldView(dest);
		supply = new Supply(worldView);
		assertFalse(supply.inSupply(dest));
	}

	@Test
	public void destMoveIntoSupply() {
		Unit dest = unitDaoService.buildUnit(nationMe, FAR_PORT,
				UnitType.DESTROYER);
		WorldView worldView = sectorDaoService.getSupplyWorldView(dest);
		Supply supply = new Supply(worldView);
		assertFalse(supply.inSupply(dest));
		sectorDaoService.captureCity(nationMe, PORT);
		setBuild(PORT, UnitType.TRANSPORT);
		Result<MoveCost> result = moveUnits(
				makeUnitList(dest), CLOSE_ENOUGH_TO_PORT);
		assertResult(result);
		worldView = sectorDaoService.getSupplyWorldView(dest);
		supply = new Supply(worldView);
		assertTrue(supply.inSupply(dest));
	}

	@Test
	public void supplyMoveIntoSupply() {
		Unit supply = unitDaoService.buildUnit(nationMe, FAR_PORT,
				UnitType.SUPPLY);
		supply.decreaseAmmo();
		sectorDaoService.captureCity(nationMe, PORT);
		setBuild(PORT, UnitType.TRANSPORT);
		Result<MoveCost> result = moveUnits(
				makeUnitList(supply), CLOSE_ENOUGH_TO_PORT);
		assertResult(result);
		assertNotFired(result, supply);
	}

	@Test
	public void destMoveIntoSupplyRefillsAmmo() {
		Unit dest = unitDaoService.buildUnit(nationMe, FAR_PORT,
				UnitType.DESTROYER);
		dest.decreaseAmmo();
		sectorDaoService.captureCity(nationMe, PORT);
		setBuild(PORT, UnitType.TRANSPORT);
		Result<MoveCost> result = moveUnits(
				makeUnitList(dest), CLOSE_ENOUGH_TO_PORT);
		assertResult(result);
		assertNotFired(result, dest);
	}
	@Test
	public void supplyMoveIntoSupplyRefillsAmmo() {
		Unit supply = unitDaoService.buildUnit(nationMe, FAR_PORT,
				UnitType.SUPPLY);
		supply.decreaseAmmo();
		sectorDaoService.captureCity(nationMe, PORT);
		setBuild(PORT, UnitType.TRANSPORT);
		Result<MoveCost> result = moveUnits(
				makeUnitList(supply), CLOSE_ENOUGH_TO_PORT);
		assertResult(result);
		assertNotFired(result, supply);
	}

	@Test
	public void supplyFarInfMove() {
		Unit inf = unitDaoService.buildUnit(nationMe, FARXPORT,
				UnitType.INFANTRY);
		Unit inf2 = unitDaoService.buildUnit(nationMe, FARXPORT,
				UnitType.INFANTRY);
		Unit xport = unitDaoService.buildUnit(nationMe, FARXPORT,
				UnitType.TRANSPORT);
		Supply supply = new Supply(sectorDaoService.getSupplyWorldView(xport));
		assertFalse(supply.inSupply(xport));
		assertTrue(supply.inSupply(inf));
		assertTrue(supply.inSupply(inf2));
		Result<MoveCost> result = moveUnits(
				makeUnitList(inf), FARLAND);
		assertResult(result);
		assertEquals(inf.getUnitBase().getMobility() - 1, inf.getMobility());
	}

	@Test
	public void supplyFarSupMove() {
		Unit sup = unitDaoService.buildUnit(nationMe, FARXPORT,
				UnitType.SUPPLY);
		Supply supply = new Supply(sectorDaoService.getSupplyWorldView(sup));
		assertTrue(supply.inSupply(sup));
		Result<MoveCost> result = moveUnits(
				makeUnitList(sup), FARXPORT1);
		assertResult(result);
		assertEquals(sup.getUnitBase().getMobility() - 1, sup.getMobility());
	}

	@Test
	public void supplyFarEmptySupMove() {
		Unit sup = unitDaoService.buildUnit(nationMe, FARXPORT,
				UnitType.SUPPLY);
		sup.setAmmo(0);
		Supply supply = new Supply(sectorDaoService.getSupplyWorldView(sup));
		assertTrue(supply.inSupply(sup));
		Result<MoveCost> result = moveUnits(
				makeUnitList(sup), FARXPORT1);
		assertResult(result);
		assertEquals(sup.getUnitBase().getMobility() - 1, sup.getMobility());
	}
	
	@Test
	public void zepMoveIntoMyCityRefillsAmmo() {
		Unit zep = unitDaoService.buildUnit(nationMe, NEAR_PORT,
				UnitType.ZEPPELIN);
		zep.decreaseAmmo();
		sectorDaoService.captureCity(nationMe, PORT);
		setBuild(PORT, UnitType.TRANSPORT);
		Result<MoveCost> result = moveUnits(
				makeUnitList(zep), PORT);
		assertResult(result);
		assertNotFired(result, zep);
		assertFullFuel(result, zep);
	}
	
	@Test
	public void zepMoveNoRefillsAmmo() {
		Unit zep = unitDaoService.buildUnit(nationMe, NEAR_PORT,
				UnitType.ZEPPELIN);
		zep.decreaseAmmo();
		sectorDaoService.captureCity(nationMe, PORT);
		setBuild(PORT, UnitType.TRANSPORT);
		Result<MoveCost> result = moveUnits(
				makeUnitList(zep), CLOSE_ENOUGH_TO_PORT);
		assertResult(result);
		assertFired(result, zep);
		assertShortFuel(result, zep);
	}

}
