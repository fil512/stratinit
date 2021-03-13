package com.kenstevens.stratinit.server.rest.attack;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.client.model.FlakBattleLog;
import com.kenstevens.stratinit.client.model.MoveCost;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.rest.TwoPlayerBase;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FlakTest extends TwoPlayerBase {
    private static final SectorCoords ATT = new SectorCoords(0, 1);
    private static final SectorCoords FORT = new SectorCoords(1, 4);
    private static final SectorCoords SEA = new SectorCoords(3, 0);
    @Autowired
    protected SectorDaoService sectorDaoServiceImpl;

    @Test
    public void flakKillsPlane() {
        declareWar();
        Unit fighter = unitDaoService.buildUnit(nationMe, ATT,
                UnitType.FIGHTER);
        fighter.setHp(1);
        unitDao.merge(fighter);
        cityDaoService.captureCity(nationThem, FORT);
        unitDaoService.buildUnit(nationThem, FORT,
                UnitType.INFANTRY);
        Result<MoveCost> result = moveUnits(
                makeUnitList(fighter), FORT);
        assertFalseResult(result);
        List<FlakBattleLog> logs = getFlakBattleLogs();
        assertTrue(logs.size() > 0);
        assertTrue(logs.get(0).getFlakDamage() > 0);
    }

    private List<FlakBattleLog> getFlakBattleLogs() {
        return Lists.newArrayList(logDao.getFlakBattleLogs(nationMe));
    }

    @Test
    public void shipHasFlak() {
        declareWar();
        Unit fighter = unitDaoService.buildUnit(nationMe, ATT,
                UnitType.FIGHTER);
        fighter.setHp(1);
        unitDaoService.buildUnit(nationThem, SEA,
                UnitType.CARRIER);
        Result<MoveCost> result = moveUnits(
                makeUnitList(fighter), SEA);
        assertFalseResult(result);
        List<FlakBattleLog> logs = getFlakBattleLogs();
        assertTrue(logs.size() > 0);
        assertTrue(logs.get(0).getFlakDamage() > 0);
    }

}
