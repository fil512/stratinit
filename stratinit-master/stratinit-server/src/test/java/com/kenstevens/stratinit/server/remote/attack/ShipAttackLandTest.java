package com.kenstevens.stratinit.server.remote.attack;

import com.kenstevens.stratinit.model.MoveCost;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.remote.TwoPlayerBase;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShipAttackLandTest extends TwoPlayerBase {
    private static final SectorCoords LAND = new SectorCoords(2, 0);
    private static final SectorCoords SEA = new SectorCoords(3, 0);
    private static final SectorCoords CITY = new SectorCoords(2, 2);
    @Autowired
    protected SectorDaoService sectorDaoServiceImpl;

    @Test
    public void destCannotAttackLand() {
        declareWar();
        Unit inf = unitDaoService.buildUnit(nationThem, LAND, UnitType.INFANTRY);
        Unit dest = unitDaoService.buildUnit(nationMe, SEA, UnitType.DESTROYER);
        Result<MoveCost> result = moveUnits(
                makeUnitList(dest), LAND);
        assertFalseResult(result);
        assertNotFired(result, dest);
        assertNotDamaged(result, inf);
    }

    @Test
    public void bbCanAttackLand() {
        declareWar();
        Unit inf = unitDaoService.buildUnit(nationThem, LAND, UnitType.INFANTRY);
        Unit bb = unitDaoService.buildUnit(nationMe, SEA, UnitType.BATTLESHIP);
        Result<MoveCost> result = moveUnits(
                makeUnitList(bb), LAND);
        assertResult(result);
        assertFired(result, bb);
        assertDamaged(result, inf);
    }

    @Test
    public void bbCannotAttackEmptyLand() {
        declareWar();
        Unit bb = unitDaoService.buildUnit(nationMe, SEA, UnitType.BATTLESHIP);
        Result<MoveCost> result = moveUnits(
                makeUnitList(bb), LAND);
        assertFalseResult(result);
        assertEquals(bb.getUnitBase().getAmmo(), bb.getAmmo(), result.toString());
    }

    @Test
    public void destCanAttackEnemyCityWithUnit() {
        declareWar();
        Unit inf = unitDaoService.buildUnit(nationThem, CITY, UnitType.INFANTRY);
        Unit dest = unitDaoService.buildUnit(nationMe, SEA, UnitType.DESTROYER);
        sectorDaoServiceImpl.captureCity(nationThem, CITY);
        Result<MoveCost> result = moveUnits(
                makeUnitList(dest), CITY);
        assertResult(result);
        assertFired(result, dest);
        assertDamaged(result, inf);

    }

    @Test
    public void destCannotAttackEmptyEnemyCity() {
        declareWar();
        Unit dest = unitDaoService.buildUnit(nationMe, SEA, UnitType.DESTROYER);
        Result<MoveCost> result = moveUnits(
                makeUnitList(dest), CITY);
        assertNotFired(result, dest);
    }

}
