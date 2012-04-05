package com.kenstevens.stratinit.server.remote.attack;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kenstevens.stratinit.model.MoveCost;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.remote.TwoPlayerBase;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

public class CanAttackTest extends TwoPlayerBase {
	private static final SectorCoords DEF = new SectorCoords(5, 8);
	private static final SectorCoords ATT = new SectorCoords(6, 8);
	@Autowired
	protected SectorDaoService sectorDaoServiceImpl;

	@Test
	public void attackNoAmmo() {
		declareWar();
		Unit dest = unitDaoService.buildUnit(nationMe, ATT,
				UnitType.DESTROYER);
		unitDaoService.buildUnit(nationThem, DEF,
				UnitType.DESTROYER);
		dest.decreaseAmmo();
		dest.decreaseAmmo();
		entityManager.merge(dest);
		Result<MoveCost> result = moveUnits(
				makeUnitList(dest), DEF);
		assertFalseResult(result);
		dest = unitDao.findUnit(dest.getId());
		assertEquals(dest.getUnitBase().getMobility(), dest.getMobility());
	}
	@Test
	public void attackNoFuel() {
		declareWar();
		Unit fight = unitDaoService.buildUnit(nationMe, ATT,
				UnitType.FIGHTER);
		unitDaoService.buildUnit(nationThem, DEF,
				UnitType.FIGHTER);
		for (int i = 0; i < fight.getUnitBase().getMobility(); ++i) {
			fight.decreaseFuel();
		}
		entityManager.merge(fight);
		Result<MoveCost> result = moveUnits(
				makeUnitList(fight), DEF);
		assertResult(result);
		fight = unitDao.findUnit(fight.getId());
		assertEquals(fight.getUnitBase().getMobility() - 1, fight.getMobility());
		assertEquals(fight.getUnitBase().getAmmo() - 1, fight.getAmmo());
	}
	
	@Test
	public void attackNoMob() {
		declareWar();
		Unit fight = unitDaoService.buildUnit(nationMe, ATT,
				UnitType.FIGHTER);
		unitDaoService.buildUnit(nationThem, DEF,
				UnitType.FIGHTER);
		fight.setMobility(0);
		entityManager.merge(fight);
		Result<MoveCost> result = moveUnits(
				makeUnitList(fight), DEF);
		assertFalseResult(result);
		fight = unitDao.findUnit(fight.getId());
		assertEquals(fight.getUnitBase().getAmmo(), fight.getAmmo());
	}
	
}
