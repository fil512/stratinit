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

public class TwoInfAttackTest extends TwoPlayerBase {
    private static final SectorCoords DEF = new SectorCoords(9, 0);
    private static final SectorCoords ATT = new SectorCoords(8, 1);
    private static final SectorCoords ATT_FAR = new SectorCoords(9, 3);
    @Autowired
    protected SectorDaoService sectorDaoServiceImpl;

    @Test
    public void doubleAttackBothAttack() {
        declareWar();
        Unit inf1 = unitDaoService.buildUnit(nationMe, ATT,
                UnitType.INFANTRY);
        Unit inf2 = unitDaoService.buildUnit(nationMe, ATT,
                UnitType.INFANTRY);
        inf1.addMobility();
        inf1.addMobility();
        inf2.addMobility();
        inf2.addMobility();
        unitDaoService.buildUnit(nationThem, DEF, UnitType.INFANTRY);
        Result<MoveCost> result = moveUnits(
                makeUnitList(inf1, inf2), DEF);
        assertResult(result);
        assertDamaged(result, inf1);
        assertFired(result, inf1);
        assertFired(result, inf2);
    }

    @Test
    public void doubleAttackOneAttacks() {
        declareWar();
        Unit inf1 = unitDaoService.buildUnit(nationMe, ATT,
                UnitType.INFANTRY);
        Unit inf2 = unitDaoService.buildUnit(nationMe, ATT,
                UnitType.INFANTRY);
        inf1.addMobility();
        inf1.addMobility();
        inf2.addMobility();
        inf2.addMobility();
        Unit def = unitDaoService.buildUnit(nationThem, DEF,
                UnitType.INFANTRY);
        def.setHp(1);
        unitDao.merge(def);
        Result<MoveCost> result = moveUnits(
                makeUnitList(inf1, inf2), DEF);
        assertResult(result);
        assertNotDamaged(result, inf1);
        assertFired(result, inf1);
        assertNotFired(result, inf2);
    }

    @Test
    public void doubleAttackOnlyOneHasMob() {
        declareWar();
        Unit inf1 = unitDaoService.buildUnit(nationMe, ATT_FAR,
                UnitType.INFANTRY);
        Unit inf2 = unitDaoService.buildUnit(nationMe, ATT_FAR,
                UnitType.INFANTRY);
        inf1.addMobility();
        inf1.addMobility();
        unitDaoService.buildUnit(nationThem, DEF, UnitType.INFANTRY);
        Result<MoveCost> result = moveUnits(
                makeUnitList(inf1, inf2), DEF);
        assertResult(result);
        assertDamaged(result, inf1);
        assertFired(result, inf1);
        assertNotMoved(result, inf2);
        assertNotFired(result, inf2);
        assertEquals(ATT, inf1.getCoords());
        assertEquals(ATT_FAR, inf2.getCoords());
    }

    @Test
    public void doubleAttackNeitherHasMob() {
        declareWar();
        Unit inf1 = unitDaoService.buildUnit(nationMe, ATT_FAR,
                UnitType.INFANTRY);
        Unit inf2 = unitDaoService.buildUnit(nationMe, ATT_FAR,
                UnitType.INFANTRY);
        Unit def = unitDaoService.buildUnit(nationThem, DEF, UnitType.INFANTRY);
        Result<MoveCost> result = moveUnits(
                makeUnitList(inf1, inf2), DEF);
        assertFalseResult(result);
        assertNotDamaged(result, def);
        assertNotMoved(result, inf1);
        assertNotFired(result, inf1);
        assertNotMoved(result, inf2);
        assertNotFired(result, inf2);
        assertEquals(ATT_FAR, inf1.getCoords());
        assertEquals(ATT_FAR, inf2.getCoords());
    }
}
