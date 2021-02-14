package com.kenstevens.stratinit.server.remote.move;

import com.kenstevens.stratinit.model.MoveCost;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.remote.StratInitWebBase;
import com.kenstevens.stratinit.supply.Supply;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UnitMoveTest extends StratInitWebBase {
	public static final SectorCoords S00 = new SectorCoords(0, 0);
	public static final SectorCoords S01 = new SectorCoords(0, 1);
	public static final SectorCoords S02 = new SectorCoords(0, 2);
	public static final SectorCoords NO_SUPPLY = new SectorCoords(0, 12);
	public static final SectorCoords NO_SUPPLY_DEST = new SectorCoords(0, 13);

	@BeforeEach
	public void doJoinGame() {
		joinGamePlayerMe();
	}

	@Test
	public void sufMob() {
		Unit inf = unitDaoService.buildUnit(nationMe, S00,
				UnitType.INFANTRY);
		Result<MoveCost> result = moveUnits(makeUnitList(inf), S01);
		assertResult(result);
		assertEquals(S01, inf.getCoords());
		assertNull(inf.getUnitMove());
	}

	@Test
	public void noMob() {
		Unit inf = unitDaoService.buildUnit(nationMe, S00,
				UnitType.INFANTRY);
		inf.setMobility(0);
		Result<MoveCost> result = moveUnits(makeUnitList(inf), S01);
		assertFalseResult(result);
		assertEquals(S00, inf.getCoords());
		assertNotNull(inf.getUnitMove());
	}

	@Test
	public void noMobNoSupply() {
		Unit inf = unitDaoService.buildUnit(nationMe, NO_SUPPLY,
				UnitType.INFANTRY);
		inf.setMobility(0);
		WorldView worldView = sectorDaoService.getSupplyWorldView(inf);
		Supply supply = new Supply(worldView);
		assertFalse(supply.inSupply(inf));
		Result<MoveCost> result = moveUnits(makeUnitList(inf), NO_SUPPLY_DEST);
		assertFalseResult(result);
		assertEquals(NO_SUPPLY, inf.getCoords());
		assertNotNull(inf.getUnitMove(), result.toString());
	}

	@Test
	public void noMobNoSupplyOneMob() {
		Unit inf = unitDaoService.buildUnit(nationMe, NO_SUPPLY,
				UnitType.INFANTRY);
		inf.setMobility(1);
		WorldView worldView = sectorDaoService.getSupplyWorldView(inf);
		Supply supply = new Supply(worldView);
		assertFalse(supply.inSupply(inf));
		Result<MoveCost> result = moveUnits(makeUnitList(inf), NO_SUPPLY_DEST);
		assertFalseResult(result);
		assertEquals(NO_SUPPLY, inf.getCoords());
		assertNotNull(inf.getUnitMove(), result.toString());
	}

	@Test
	public void noMob2() {
		Unit inf = unitDaoService.buildUnit(nationMe, S00,
				UnitType.INFANTRY);
		inf.setMobility(0);
		Result<MoveCost> result = moveUnits(makeUnitList(inf), S02);
		assertFalseResult(result);
		assertEquals(S00, inf.getCoords());
		assertNotNull(inf.getUnitMove());
	}

}
