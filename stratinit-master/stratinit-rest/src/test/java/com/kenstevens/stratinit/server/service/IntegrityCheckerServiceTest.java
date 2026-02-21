package com.kenstevens.stratinit.server.service;

import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.server.rest.TwoPlayerBase;
import com.kenstevens.stratinit.server.svc.IntegrityCheckerService;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IntegrityCheckerServiceTest extends TwoPlayerBase {
    private static final SectorCoords PORT = new SectorCoords(7, 8);
    private static final SectorCoords NEAR_PORT = new SectorCoords(6, 8);
    private static final SectorCoords NEAR_PORTN = new SectorCoords(6, 7);
    private static final SectorCoords NEAR_PORTS = new SectorCoords(6, 9);
    private static final SectorCoords NEAR_PORTNW = new SectorCoords(5, 7);
    private static final SectorCoords NEAR_PORTW = new SectorCoords(5, 8);
    private static final SectorCoords NEAR_PORTSW = new SectorCoords(5, 9);
    @Autowired
    protected SectorService sectorService;
    @Autowired
    protected IntegrityCheckerService integrityCheckerService;

    @Test
    public void noEnemyUnitInMycity() {
        cityService.captureCity(nationMe, PORT);
        Unit dest = unitService.buildUnit(nationThem, PORT,
                UnitType.DESTROYER);
        assertTrue(dest.getCoords().equals(PORT));
        integrityCheckerService.checkAndFix(testGame);
        assertFalse(dest.getCoords().equals(PORT));
    }

    @Test
    public void noEnemyUnitInSameSectorAsMyUnit() {
        Unit dest = unitService.buildUnit(nationMe, NEAR_PORT,
                UnitType.DESTROYER);
        unitService.buildUnit(nationThem, NEAR_PORT,
                UnitType.DESTROYER);
        assertTrue(dest.getCoords().equals(NEAR_PORT));
        integrityCheckerService.checkAndFix(testGame);
        assertFalse(dest.getCoords().equals(NEAR_PORT));
        assertTrue(dest.isAlive());
    }

    @Test
    public void noEnemyUnitInSameSectorAsMyUnitNowhereToMove() {
        Unit dest = unitService.buildUnit(nationThem, NEAR_PORT,
                UnitType.DESTROYER);
        unitService.buildUnit(nationMe, NEAR_PORT,
                UnitType.DESTROYER);
        unitService.buildUnit(nationMe, NEAR_PORTN,
                UnitType.DESTROYER);
        unitService.buildUnit(nationMe, NEAR_PORTS,
                UnitType.DESTROYER);
        unitService.buildUnit(nationMe, NEAR_PORTNW,
                UnitType.DESTROYER);
        unitService.buildUnit(nationMe, NEAR_PORTW,
                UnitType.DESTROYER);
        unitService.buildUnit(nationMe, NEAR_PORTSW,
                UnitType.DESTROYER);
        assertTrue(dest.getCoords().equals(NEAR_PORT));
        integrityCheckerService.checkAndFix(testGame);
        assertFalse(dest.isAlive());
    }

}
