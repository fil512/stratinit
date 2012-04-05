package com.kenstevens.stratinit.server.daoservice;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.server.daoserviceimpl.IntegrityCheckerServiceImpl;
import com.kenstevens.stratinit.server.remote.TwoPlayerBase;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

public class IntegrityCheckerServiceTest extends TwoPlayerBase {
	@Autowired
	protected SectorDaoService sectorDaoService;
	@Autowired
	protected IntegrityCheckerServiceImpl integrityCheckerService;
	
	private static final SectorCoords PORT = new SectorCoords(7, 8);
	private static final SectorCoords NEAR_PORT = new SectorCoords(6, 8);
	private static final SectorCoords NEAR_PORTN = new SectorCoords(6, 7);
	private static final SectorCoords NEAR_PORTS = new SectorCoords(6, 9);
	private static final SectorCoords NEAR_PORTNW = new SectorCoords(5, 7);
	private static final SectorCoords NEAR_PORTW = new SectorCoords(5, 8);
	private static final SectorCoords NEAR_PORTSW = new SectorCoords(5, 9);
	
	@Test
	public void noEnemyUnitInMycity() {
		sectorDaoService.captureCity(nationMe, PORT);
		Unit dest = unitDaoService.buildUnit(nationThem, PORT,
				UnitType.DESTROYER);
		assertTrue(dest.getCoords().equals(PORT));
		integrityCheckerService.checkAndFix(testGame);
		assertFalse(dest.getCoords().equals(PORT));
	}

	@Test
	public void noEnemyUnitInSameSectorAsMyUnit() {
		Unit dest = unitDaoService.buildUnit(nationMe, NEAR_PORT,
				UnitType.DESTROYER);
		unitDaoService.buildUnit(nationThem, NEAR_PORT,
				UnitType.DESTROYER);
		assertTrue(dest.getCoords().equals(NEAR_PORT));
		integrityCheckerService.checkAndFix(testGame);
		assertFalse(dest.getCoords().equals(NEAR_PORT));
		assertTrue(dest.isAlive());
	}
	
	@Test
	public void noEnemyUnitInSameSectorAsMyUnitNowhereToMove() {
		Unit dest = unitDaoService.buildUnit(nationThem, NEAR_PORT,
				UnitType.DESTROYER);
		unitDaoService.buildUnit(nationMe, NEAR_PORT,
				UnitType.DESTROYER);
		unitDaoService.buildUnit(nationMe, NEAR_PORTN,
				UnitType.DESTROYER);
		unitDaoService.buildUnit(nationMe, NEAR_PORTS,
				UnitType.DESTROYER);
		unitDaoService.buildUnit(nationMe, NEAR_PORTNW,
				UnitType.DESTROYER);
		unitDaoService.buildUnit(nationMe, NEAR_PORTW,
				UnitType.DESTROYER);
		unitDaoService.buildUnit(nationMe, NEAR_PORTSW,
				UnitType.DESTROYER);
		assertTrue(dest.getCoords().equals(NEAR_PORT));
		integrityCheckerService.checkAndFix(testGame);
		assertFalse(dest.isAlive());
	}

}
