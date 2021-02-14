package com.kenstevens.stratinit.server.remote.supply;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.model.MoveCost;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.remote.TwoPlayerBase;
import com.kenstevens.stratinit.supply.SupplyTree;
import com.kenstevens.stratinit.type.SectorCoordVector;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SupplyTreeTest  extends TwoPlayerBase {
	private static final SectorCoords SUPPLY = new SectorCoords(3, 0);
	private static final SectorCoords SUPPLY2 = new SectorCoords(3, 4);
	private static final SectorCoords DEST = new SectorCoords(4, 0);
	private static final SectorCoords DEF = new SectorCoords(5, 0);
	private static final SectorCoords PORT = new SectorCoords(2, 2);
	private static final SectorCoords FAR_PORT = new SectorCoords(7, 8);
	private static final SectorCoords ATTINF = new SectorCoords(1, 3);
	private static final SectorCoords DEFINF = new SectorCoords(1, 2);
	private static final SectorCoords FARXPORT = new SectorCoords(6, 4);
	private static final SectorCoords FARATTINF = new SectorCoords(7, 4);
	private static final SectorCoords FARCITY = new SectorCoords(8, 4);

	@Test
	public void supplyDepleted() {
		declareWar();
		Unit supply = unitDaoService.buildUnit(nationMe, SUPPLY,
				UnitType.SUPPLY);
		Unit dest = unitDaoService.buildUnit(nationMe, DEST,
				UnitType.DESTROYER);
		unitDaoService.buildUnit(nationThem, DEF,
				UnitType.TRANSPORT);
		Result<MoveCost> result = moveUnits(
				makeUnitList(dest), DEF);
		assertResult(result);
		assertNotFired(result, dest);
		assertFired(result, supply);
	}

	@Test
	public void supplyWithPortNotDepleted() {
		declareWar();
		Unit supply = unitDaoService.buildUnit(nationMe, SUPPLY,
				UnitType.SUPPLY);
		Unit dest = unitDaoService.buildUnit(nationMe, DEST,
				UnitType.DESTROYER);
		unitDaoService.buildUnit(nationThem, DEF,
				UnitType.TRANSPORT);
		sectorDaoService.captureCity(nationMe, PORT);
		Result<MoveCost> result = moveUnits(
				makeUnitList(dest), DEF);
		assertResult(result);
		assertNotFired(result, dest);
		assertNotFired(result, supply);
	}
	
	@Test
	public void supplyWithFarPortDepleted() {
		declareWar();
		Unit supply = unitDaoService.buildUnit(nationMe, SUPPLY,
				UnitType.SUPPLY);
		Unit dest = unitDaoService.buildUnit(nationMe, DEST,
				UnitType.DESTROYER);
		unitDaoService.buildUnit(nationThem, DEF,
				UnitType.TRANSPORT);
		sectorDaoService.captureCity(nationMe, FAR_PORT);
		Result<MoveCost> result = moveUnits(
				makeUnitList(dest), DEF);
		assertResult(result);
		assertNotFired(result, dest);
		assertFired(result, supply);
	}
	
	@Test
	public void supply2Depleted() {
		declareWar();
		Unit supply = unitDaoService.buildUnit(nationMe, SUPPLY,
				UnitType.SUPPLY);
		Unit supply2 = unitDaoService.buildUnit(nationMe, SUPPLY2,
				UnitType.SUPPLY);
		Unit dest = unitDaoService.buildUnit(nationMe, DEST,
				UnitType.DESTROYER);
		unitDaoService.buildUnit(nationThem, DEF,
				UnitType.TRANSPORT);
		Result<MoveCost> result = moveUnits(
				makeUnitList(dest), DEF);
		assertResult(result);
		assertNotFired(result, dest);
		assertNotFired(result, supply);
		assertFired(result, supply2);
	}

	@Test
	public void supply2WithFarPortNotDepleted() {
		declareWar();
		Unit supply = unitDaoService.buildUnit(nationMe, SUPPLY,
				UnitType.SUPPLY);
		Unit supply2 = unitDaoService.buildUnit(nationMe, SUPPLY2,
				UnitType.SUPPLY);
		Unit dest = unitDaoService.buildUnit(nationMe, DEST,
				UnitType.DESTROYER);
		unitDaoService.buildUnit(nationThem, DEF,
				UnitType.TRANSPORT);
		sectorDaoService.captureCity(nationMe, FAR_PORT);
		Result<MoveCost> result = moveUnits(
				makeUnitList(dest), DEF);
		assertResult(result);
		assertNotFired(result, dest);
		assertNotFired(result, supply);
		assertNotFired(result, supply2);
	}
	
	@Test
	public void supplyChain() {
		unitDaoService.buildUnit(nationMe, SUPPLY,
				UnitType.SUPPLY);
		unitDaoService.buildUnit(nationMe, SUPPLY2,
				UnitType.SUPPLY);
		Unit dest = unitDaoService.buildUnit(nationMe, DEST,
				UnitType.DESTROYER);
		unitDaoService.buildUnit(nationThem, DEF,
				UnitType.TRANSPORT);
		sectorDaoService.captureCity(nationMe, FAR_PORT);
		WorldView worldView = sectorDaoService.getAllWorldView(nationMe);
		SupplyTree supplyTree = new SupplyTree(worldView, dest);
		Iterator<SectorCoordVector> iterator = supplyTree.getSupplyChain();
		List<SectorCoordVector> list = Lists.newArrayList(iterator);
		assertEquals(3, list.size());
		assertEquals(new SectorCoordVector(DEST, SUPPLY), list.get(0));
		assertEquals(new SectorCoordVector(SUPPLY, SUPPLY2), list.get(1));
		assertEquals(new SectorCoordVector(SUPPLY2, FAR_PORT), list.get(2));
	}
	
	
	@Test
	public void supplyInf() {
		declareWar();
		Unit inf = unitDaoService.buildUnit(nationMe, ATTINF,
				UnitType.INFANTRY);
		unitDaoService.buildUnit(nationThem, DEFINF,
				UnitType.INFANTRY);
		Result<MoveCost> result = moveUnits(
				makeUnitList(inf), DEFINF);
		assertResult(result);
		assertNotFired(result, inf);
	}

	@Test
	public void noSupplyFarInf() {
		declareWar();
		Unit inf = unitDaoService.buildUnit(nationMe, FARATTINF,
				UnitType.INFANTRY);
		Result<MoveCost> result = moveUnits(
				makeUnitList(inf), FARCITY);
		assertResult(result);
		assertFired(result, inf);
	}
	
	@Test
	public void supplyFarInf() {
		declareWar();
		Unit inf = unitDaoService.buildUnit(nationMe, FARATTINF,
				UnitType.INFANTRY);
		unitDaoService.buildUnit(nationMe, FARXPORT,
				UnitType.TRANSPORT);
		Result<MoveCost> result = moveUnits(
				makeUnitList(inf), FARCITY);
		assertResult(result);
		assertNotFired(result, inf);
	}
}
