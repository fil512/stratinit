package com.kenstevens.stratinit.server.rest.attack;

import com.kenstevens.stratinit.client.model.MoveCost;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.service.SectorService;
import com.kenstevens.stratinit.server.rest.TwoPlayerBase;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LandAttackShipTest extends TwoPlayerBase {
    private static final SectorCoords LAND = new SectorCoords(2, 0);
    private static final SectorCoords SEA = new SectorCoords(3, 0);
    private static final SectorCoords SEA2 = new SectorCoords(4, 0);
    @Autowired
    protected SectorService sectorServiceImpl;

    @Test
    public void infCannotAttackShip() {
        declareWar();
        Unit pat = unitService.buildUnit(nationThem, SEA, UnitType.PATROL);
        Unit inf = unitService.buildUnit(nationMe, LAND, UnitType.INFANTRY);
        Result<MoveCost> result = moveUnits(
                makeUnitList(inf), SEA);
        assertFalseResult(result);
        assertNotMoved(result, inf);
        assertTrue(inf.getUnitBase().getAmmo() == inf.getAmmo(), result.toString());
        assertNotDamaged(result, pat);
    }

    @Test
    public void tankCanAttackShip() {
        declareWar();
        Unit pat = unitService.buildUnit(nationThem, SEA, UnitType.PATROL);
        Unit tank = unitService.buildUnit(nationMe, LAND, UnitType.TANK);
        Result<MoveCost> result = moveUnits(
                makeUnitList(tank), SEA);
        assertResult(result);
        assertMoved(result, tank);
        assertDamaged(result, pat);
        assertNotDamaged(result, tank);
    }

    @Test
    public void tankOnShipCannotAttackShip() {
        declareWar();
        Unit pat = unitService.buildUnit(nationThem, SEA, UnitType.PATROL);
        unitService.buildUnit(nationMe, SEA2, UnitType.TRANSPORT);
        Unit tank = unitService.buildUnit(nationMe, SEA2, UnitType.TANK);
        Result<MoveCost> result = moveUnits(
                makeUnitList(tank), SEA);
        assertFalseResult(result);
        assertNotMoved(result, tank);
        assertNotDamaged(result, tank);
        assertNotDamaged(result, pat);
    }
}
