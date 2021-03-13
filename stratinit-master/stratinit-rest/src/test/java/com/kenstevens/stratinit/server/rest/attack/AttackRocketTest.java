package com.kenstevens.stratinit.server.rest.attack;

import com.kenstevens.stratinit.client.model.MoveCost;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.rest.TwoPlayerBase;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AttackRocketTest extends TwoPlayerBase {
    private static final SectorCoords DEF = new SectorCoords(9, 0);
    private static final SectorCoords ATT = new SectorCoords(8, 1);
    private static final SectorCoords PORT = new SectorCoords(2, 2);
    private static final SectorCoords SEA = new SectorCoords(3, 2);
    @Autowired
    protected SectorDaoService sectorDaoServiceImpl;

    @Test
    public void infAttacksSat() {
        declareWar();
        Unit inf = unitDaoService.buildUnit(nationMe, ATT,
                UnitType.INFANTRY);
        inf.addMobility();
        Unit sat = unitDaoService.buildUnit(nationThem, DEF, UnitType.SATELLITE);
        Result<MoveCost> result = moveUnits(
                makeUnitList(inf), DEF);
        assertResult(result);
        assertNotDamaged(result, inf);
        assertFired(result, inf);
        assertDamaged(result, sat);
    }

    @Test
    public void infAttacksICBM() {
        declareWar();
        Unit inf = unitDaoService.buildUnit(nationMe, ATT,
                UnitType.INFANTRY);
        inf.addMobility();
        Unit icbm = unitDaoService.buildUnit(nationThem, DEF, UnitType.ICBM_1);
        Result<MoveCost> result = moveUnits(
                makeUnitList(inf), DEF);
        assertResult(result);
        assertNotDamaged(result, inf);
        assertFired(result, inf);
        assertDamaged(result, icbm);
    }

    @Test
    public void destAttacksSat() {
        declareWar();
        Unit dest = unitDaoService.buildUnit(nationMe, SEA,
                UnitType.DESTROYER);
        cityDaoService.captureCity(nationThem, PORT);
        setBuild(PORT, UnitType.ZEPPELIN);
        Unit sat = unitDaoService.buildUnit(nationThem, PORT, UnitType.SATELLITE);
        Result<MoveCost> result = moveUnits(
                makeUnitList(dest), PORT);
        assertResult(result);
        assertNotDamaged(result, dest);
        assertFired(result, dest);
        assertDamaged(result, sat);
    }

    @Test
    public void destAttacksICBM() {
        declareWar();
        Unit dest = unitDaoService.buildUnit(nationMe, SEA,
                UnitType.DESTROYER);
        cityDaoService.captureCity(nationThem, PORT);
        setBuild(PORT, UnitType.ZEPPELIN);
        Unit icbm = unitDaoService.buildUnit(nationThem, PORT, UnitType.ICBM_1);
        Result<MoveCost> result = moveUnits(
                makeUnitList(dest), PORT);
        assertResult(result);
        assertNotDamaged(result, dest);
        assertFired(result, dest);
        assertDamaged(result, icbm);
    }

}
