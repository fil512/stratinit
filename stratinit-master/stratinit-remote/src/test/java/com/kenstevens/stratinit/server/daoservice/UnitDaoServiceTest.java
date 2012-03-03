package com.kenstevens.stratinit.server.daoservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitSeen;
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

}
