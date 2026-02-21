package com.kenstevens.stratinit.server.rest.attack;

import com.kenstevens.stratinit.client.model.MoveCost;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.service.SectorService;
import com.kenstevens.stratinit.server.service.UnitService;
import com.kenstevens.stratinit.server.rest.TwoPlayerBase;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CanAttackTest extends TwoPlayerBase {
    private static final SectorCoords DEF = new SectorCoords(5, 8);
    private static final SectorCoords ATT = new SectorCoords(6, 8);
    @Autowired
    protected SectorService sectorServiceImpl;
    @Autowired
    private UnitService unitService;

    @Test
    public void attackNoAmmo() {
        declareWar();
        Unit dest = unitService.buildUnit(nationMe, ATT,
                UnitType.DESTROYER);
        unitService.buildUnit(nationThem, DEF,
                UnitType.DESTROYER);
        dest.decreaseAmmo();
        dest.decreaseAmmo();
        unitService.merge(dest);
        Result<MoveCost> result = moveUnits(
                makeUnitList(dest), DEF);
        assertFalseResult(result);
        dest = unitDao.findUnit(dest.getId());
        assertEquals(dest.getUnitBase().getMobility(), dest.getMobility());
    }

    @Test
    public void attackNoFuel() {
        declareWar();
        Unit fight = unitService.buildUnit(nationMe, ATT,
                UnitType.FIGHTER);
        unitService.buildUnit(nationThem, DEF,
                UnitType.FIGHTER);
        for (int i = 0; i < fight.getUnitBase().getMobility(); ++i) {
            fight.decreaseFuel();
        }
        unitService.merge(fight);
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
        Unit fight = unitService.buildUnit(nationMe, ATT,
                UnitType.FIGHTER);
        unitService.buildUnit(nationThem, DEF,
                UnitType.FIGHTER);
        fight.setMobility(0);
        unitService.merge(fight);
        Result<MoveCost> result = moveUnits(
                makeUnitList(fight), DEF);
        assertFalseResult(result);
        fight = unitDao.findUnit(fight.getId());
        assertEquals(fight.getUnitBase().getAmmo(), fight.getAmmo());
    }

}
