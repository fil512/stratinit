package com.kenstevens.stratinit.server.rest.attack;

import com.kenstevens.stratinit.model.MoveCost;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.rest.TwoPlayerBase;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class BombTest extends TwoPlayerBase {
    private static final SectorCoords DEF = new SectorCoords(0, 0);
    private static final SectorCoords ATT = new SectorCoords(0, 1);
    private static final SectorCoords SEA = new SectorCoords(3, 0);
    @Autowired
    protected SectorDaoService sectorDaoServiceImpl;

    @Test
    public void attackCanBombInf() {
        declareWar();
        Unit bomber = unitDaoService.buildUnit(nationMe, ATT,
                UnitType.HEAVY_BOMBER);
        Unit inf1 = unitDaoService.buildUnit(nationThem, DEF,
                UnitType.INFANTRY);
        Unit inf2 = unitDaoService.buildUnit(nationThem, DEF,
                UnitType.INFANTRY);
        Result<MoveCost> result = moveUnits(
                makeUnitList(bomber), DEF);
        assertResult(result);
        assertEquals(2, inf1.getHp());
        assertEquals(2, inf2.getHp());
    }

    @Test
    public void attackCanLBombInf() {
        declareWar();
        Unit bomber = unitDaoService.buildUnit(nationMe, ATT,
                UnitType.NAVAL_BOMBER);
        Unit inf1 = unitDaoService.buildUnit(nationThem, DEF,
                UnitType.INFANTRY);
        Unit inf2 = unitDaoService.buildUnit(nationThem, DEF,
                UnitType.INFANTRY);
        Result<MoveCost> result = moveUnits(
                makeUnitList(bomber), DEF);
        assertResult(result);
        assertEquals(3, inf1.getHp());
        assertEquals(3, inf2.getHp());
    }

    @Test
    public void attackCanBombInfTwice() {
        declareWar();
        Unit bomber = unitDaoService.buildUnit(nationMe, ATT,
                UnitType.HEAVY_BOMBER);
        Unit inf1 = unitDaoService.buildUnit(nationThem, DEF,
                UnitType.INFANTRY);
        Unit inf2 = unitDaoService.buildUnit(nationThem, DEF,
                UnitType.INFANTRY);
        Result<MoveCost> result = moveUnits(
                makeUnitList(bomber), DEF);
        assertResult(result);
        result = moveUnits(
                makeUnitList(bomber), DEF);
        assertResult(result);
        assertEquals(1, inf1.getHp());
        assertEquals(1, inf2.getHp());
    }

    @Test
    public void twoBombers() {
        declareWar();
        Unit bomber1 = unitDaoService.buildUnit(nationMe, ATT,
                UnitType.HEAVY_BOMBER);
        Unit bomber2 = unitDaoService.buildUnit(nationMe, ATT,
                UnitType.HEAVY_BOMBER);
        Unit inf = unitDaoService.buildUnit(nationThem, DEF,
                UnitType.INFANTRY);
        Result<MoveCost> result = moveUnits(
                makeUnitList(bomber1, bomber2), DEF);
        assertResult(result);
        assertEquals(1, inf.getHp());
    }

    @Test
    public void twoBombersTwice() {
        declareWar();
        Unit bomber1 = unitDaoService.buildUnit(nationMe, ATT,
                UnitType.HEAVY_BOMBER);
        Unit bomber2 = unitDaoService.buildUnit(nationMe, ATT,
                UnitType.HEAVY_BOMBER);
        Unit inf = unitDaoService.buildUnit(nationThem, DEF,
                UnitType.INFANTRY);
        Result<MoveCost> result = moveUnits(
                makeUnitList(bomber1, bomber2), DEF);
        assertResult(result);
        result = moveUnits(
                makeUnitList(bomber1, bomber2), DEF);
        assertResult(result);
        assertFalse(inf.isAlive());
    }

    @Test
    public void heavyBomberNoAttackShip() {
        declareWar();
        Unit bomber = unitDaoService.buildUnit(nationMe, ATT,
                UnitType.HEAVY_BOMBER);
        Unit dest = unitDaoService.buildUnit(nationThem, SEA,
                UnitType.DESTROYER);
        Result<MoveCost> result = moveUnits(
                makeUnitList(bomber), SEA);
        assertFalseResult(result);
        assertNotDamaged(result, dest);
    }

    @Test
    public void navalBomberAttacksShip() {
        declareWar();
        Unit bomber = unitDaoService.buildUnit(nationMe, ATT,
                UnitType.NAVAL_BOMBER);
        Unit dest = unitDaoService.buildUnit(nationThem, SEA,
                UnitType.DESTROYER);
        Result<MoveCost> result = moveUnits(
                makeUnitList(bomber), SEA);
        assertResult(result);
        assertDamaged(result, dest);
    }

    @Test
    public void navalAndHeavyBomberAttacksShip() {
        declareWar();
        Unit hbomber = unitDaoService.buildUnit(nationMe, ATT,
                UnitType.HEAVY_BOMBER);
        Unit nbomber = unitDaoService.buildUnit(nationMe, ATT,
                UnitType.NAVAL_BOMBER);
        Unit dest = unitDaoService.buildUnit(nationThem, SEA,
                UnitType.DESTROYER);
        Result<MoveCost> result = moveUnits(
                makeUnitList(hbomber, nbomber), SEA);
        assertResult(result);
        assertDamaged(result, dest);
    }

}
