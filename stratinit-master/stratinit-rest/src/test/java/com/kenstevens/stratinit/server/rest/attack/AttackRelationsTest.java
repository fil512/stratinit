package com.kenstevens.stratinit.server.rest.attack;

import com.kenstevens.stratinit.client.model.MoveCost;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.rest.TwoPlayerBase;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AttackRelationsTest extends TwoPlayerBase {
    private static final SectorCoords ATTACK_FROM = new SectorCoords(8, 3);
    private static final SectorCoords CITY = new SectorCoords(8, 4);
    private static Unit testInf;
    @Autowired
    protected SectorDaoService sectorDaoServiceImpl;

    @BeforeEach
    public void player2Inf() {
        testInf = unitDaoService.buildUnit(nationMe, ATTACK_FROM,
                UnitType.INFANTRY);
    }

    private void battleHappened() {
        int logCount = unitAttackLogCount();
        Result<MoveCost> result = moveUnits(
                makeUnitList(testInf), CITY);
        assertResult(result);
        Unit inf = unitDao.findUnit(testInf.getId());
        assertDamaged(result, inf);
        assertTrue(logCount < unitAttackLogCount(), result.toString());
    }


    private void movedAndNoBattleHappened() {
        int logCount = unitAttackLogCount();
        Result<MoveCost> result = moveUnits(
                makeUnitList(testInf), CITY);
        assertResult(result);
        Unit inf = unitDao.findUnit(testInf.getId());
        assertNotDamaged(result, inf);
        assertTrue(logCount == unitAttackLogCount(), result.toString());
    }

    private void couldNotMove() {
        Result<MoveCost> result = moveUnits(
                makeUnitList(testInf), CITY);
        assertFalseResult(result);
    }

    private int unitAttackLogCount() {
        return (int) StreamSupport.stream(logDao.getUnitAttackedBattleLogs(nationMe).spliterator(), false).count();
    }

    @Test
    public void attackCityNeutral() {
        couldNotMove();
    }

    @Test
    public void attackCityMeWar() {
        declareWar();
        battleHappened();
    }

    @Test
    public void attackCityTheyWar() {
        warDeclared();
        couldNotMove();
    }

    @Test
    public void meAlliedTheyNotMeansNoMove() {
        declareAlliance();
        couldNotMove();
    }

    @Test
    public void theyAlliedMeNotMeansNoMove() {
        allianceDeclared();
        couldNotMove();
    }

    @Test
    public void jointAllianceCanEnter() {
        declareAlliance();
        allianceDeclared();
        movedAndNoBattleHappened();
    }

    @Test
    public void friendlyNoMove() {
        declareFriendly();
        friendlyDeclared();
        couldNotMove();
    }
}
