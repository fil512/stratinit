package com.kenstevens.stratinit.server.remote.attack;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kenstevens.stratinit.model.FlakBattleLog;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.remote.TwoPlayerBase;
import com.kenstevens.stratinit.server.remote.move.cost.MoveCost;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

public class FlakTest extends TwoPlayerBase {
	@Autowired protected SectorDaoService sectorDaoServiceImpl;
	private static final SectorCoords ATT = new SectorCoords(0, 1);
	private static final SectorCoords FORT = new SectorCoords(1, 4);
	private static final SectorCoords SEA = new SectorCoords(3, 0);

	@Test
	public void flakKillsPlane() {
		declareWar();
		Unit fighter = unitDaoService.buildUnit(nationMe, ATT,
				UnitType.FIGHTER);
		fighter.setHp(1);
		unitDao.merge(fighter);
		sectorDaoServiceImpl.captureCity(nationThem, FORT);
		unitDaoService.buildUnit(nationThem, FORT,
				UnitType.INFANTRY);
		Result<MoveCost> result = moveUnits(
				makeUnitList(fighter), FORT);
		assertFalseResult(result);
		List<FlakBattleLog> logs = logDao.getFlakBattleLogs(nationMe);
		assertTrue(logs.size() > 0);
		assertTrue(logs.get(0).getFlakDamage() > 0);
	}
	
	@Test
	public void shipHasFlak() {
		declareWar();
		Unit fighter = unitDaoService.buildUnit(nationMe, ATT,
				UnitType.FIGHTER);
		fighter.setHp(1);
		unitDaoService.buildUnit(nationThem, SEA,
				UnitType.CARRIER);
		Result<MoveCost> result = moveUnits(
				makeUnitList(fighter), SEA);
		assertFalseResult(result);
		List<FlakBattleLog> logs = logDao.getFlakBattleLogs(nationMe);
		assertTrue(logs.size() > 0);
		assertTrue(logs.get(0).getFlakDamage() > 0);
	}
	
}
