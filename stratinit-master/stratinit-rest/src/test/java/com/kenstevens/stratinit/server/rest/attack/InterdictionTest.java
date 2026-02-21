package com.kenstevens.stratinit.server.rest.attack;

import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.service.SectorService;
import com.kenstevens.stratinit.server.rest.TwoPlayerBase;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InterdictionTest extends TwoPlayerBase {
    private static final SectorCoords TANK = new SectorCoords(2, 0);
    private static final SectorCoords MOV = new SectorCoords(3, 0);
    private static final SectorCoords MOV2 = new SectorCoords(3, 6);
    private static final SectorCoords BETWEEN = new SectorCoords(4, 0);
    private static final SectorCoords CLOSER = new SectorCoords(5, 0);
    private static final SectorCoords INT = new SectorCoords(5, 1);
    private static final SectorCoords INT2 = new SectorCoords(6, 1);
    private static final SectorCoords PORT = new SectorCoords(2, 2);
    private static final SectorCoords BYPORT = new SectorCoords(3, 2);
    @Autowired
    protected SectorService sectorServiceImpl;

    @Test
    public void destNoSupplyNoInterdictsDest() {
        Unit mdest = unitService
                .buildUnit(nationMe, MOV, UnitType.DESTROYER);
        Unit idest = unitService.buildUnit(nationThem, INT,
                UnitType.DESTROYER);
        Result<MoveCost> result = moveUnits(makeUnitList(mdest),
                BETWEEN);
        assertResult(result);
        assertNoInterdiction(mdest, idest, result);
    }

    @Test
    public void destInSupplyInterdictsDest() {
        Unit mdest = unitService
                .buildUnit(nationMe, MOV, UnitType.DESTROYER);
        Unit idest = unitService.buildUnit(nationThem, INT,
                UnitType.DESTROYER);
        cityService.captureCity(nationThem, PORT);
        setBuild(PORT, UnitType.TRANSPORT);
        Result<MoveCost> result = moveUnits(makeUnitList(mdest),
                BETWEEN);
        assertFalseResult(result);
        assertInterdiction(mdest, idest, result);
        List<UnitAttackedBattleLog> logs = getUnitAttackedLogs();
        assertEquals(1, logs.size());
        UnitAttackedBattleLog log = logs.get(0);
        assertEquals(nationThem, log.getAttacker());
        assertEquals(nationMe, log.getDefender());
        assertEquals(idest, log.getAttackerUnit());
        assertEquals(mdest, log.getDefenderUnit());
        assertEquals(AttackType.INTERDICTION, log.getAttackType());
        assertTrue(idest.getCoords().equals(INT));

    }

    @Test
    public void patrolInSupplyInterdictsPatrol() {
        Unit mpatrol = unitService.buildUnit(nationMe, MOV, UnitType.PATROL);
        Unit ipatrol = unitService.buildUnit(nationThem, INT,
                UnitType.PATROL);
        cityService.captureCity(nationThem, PORT);
        setBuild(PORT, UnitType.TRANSPORT);
        Result<MoveCost> result = moveUnits(makeUnitList(mpatrol),
                BETWEEN);
        assertFalseResult(result);
        assertInterdiction(mpatrol, ipatrol, result);
        List<UnitAttackedBattleLog> logs = getUnitAttackedLogs();
        assertEquals(1, logs.size());
        UnitAttackedBattleLog log = logs.get(0);
        assertEquals(nationThem, log.getAttacker());
        assertEquals(nationMe, log.getDefender());
        assertEquals(ipatrol, log.getAttackerUnit());
        assertEquals(mpatrol, log.getDefenderUnit());
        assertEquals(AttackType.INTERDICTION, log.getAttackType());
    }

    @Test
    public void patrolInSupplyNoInterdictsDest() {
        Unit mdest = unitService
                .buildUnit(nationMe, MOV, UnitType.DESTROYER);
        Unit ipatrol = unitService.buildUnit(nationThem, INT,
                UnitType.PATROL);
        cityService.captureCity(nationThem, PORT);
        setBuild(PORT, UnitType.TRANSPORT);
        Result<MoveCost> result = moveUnits(makeUnitList(mdest),
                BETWEEN);
        assertResult(result);
        assertNoInterdiction(mdest, ipatrol, result);
    }

    @Test
    public void bbInSupplyInterdictsDest2Away() {
        Unit mdest = unitService
                .buildUnit(nationMe, MOV, UnitType.DESTROYER);
        Unit ibb = unitService.buildUnit(nationThem, INT2,
                UnitType.BATTLESHIP);
        cityService.captureCity(nationThem, PORT);
        setBuild(PORT, UnitType.TRANSPORT);
        Result<MoveCost> result = moveUnits(makeUnitList(mdest),
                BETWEEN);
        assertFalseResult(result);
        assertInterdiction(mdest, ibb, result);
        List<UnitAttackedBattleLog> logs = getUnitAttackedLogs();
        assertEquals(1, logs.size());
        UnitAttackedBattleLog log = logs.get(0);
        assertEquals(nationThem, log.getAttacker());
        assertEquals(nationMe, log.getDefender());
        assertEquals(ibb, log.getAttackerUnit());
        assertEquals(mdest, log.getDefenderUnit());
        assertEquals(AttackType.INTERDICTION, log.getAttackType());
    }

    @Test
    public void patrolInSupplyInterdictsTransport2Away() {
        Unit mtransport = unitService.buildUnit(nationMe, MOV,
                UnitType.TRANSPORT);
        Unit ipatrol = unitService.buildUnit(nationThem, INT2,
                UnitType.PATROL);
        cityService.captureCity(nationThem, PORT);
        setBuild(PORT, UnitType.TRANSPORT);
        Result<MoveCost> result = moveUnits(makeUnitList(mtransport),
                BETWEEN);
        assertFalseResult(result);
        assertInterdiction(mtransport, ipatrol, result);
        List<UnitAttackedBattleLog> logs = getUnitAttackedLogs();
        assertEquals(1, logs.size());
        UnitAttackedBattleLog log = logs.get(0);
        assertEquals(nationThem, log.getAttacker());
        assertEquals(nationMe, log.getDefender());
        assertEquals(ipatrol, log.getAttackerUnit());
        assertEquals(mtransport, log.getDefenderUnit());
        assertEquals(AttackType.INTERDICTION, log.getAttackType());
    }

    @Test
    public void destInSupplyInterdictsDest2Away() {
        Unit mdest = unitService
                .buildUnit(nationMe, MOV, UnitType.DESTROYER);
        Unit idest = unitService.buildUnit(nationThem, INT2,
                UnitType.DESTROYER);
        cityService.captureCity(nationThem, PORT);
        setBuild(PORT, UnitType.TRANSPORT);
        Result<MoveCost> result = moveUnits(makeUnitList(mdest),
                BETWEEN);
        assertFalseResult(result);
        assertInterdiction(mdest, idest, result);
        List<UnitAttackedBattleLog> logs = getUnitAttackedLogs();
        assertEquals(1, logs.size());
        UnitAttackedBattleLog log = logs.get(0);
        assertEquals(nationThem, log.getAttacker());
        assertEquals(nationMe, log.getDefender());
        assertEquals(idest, log.getAttackerUnit());
        assertEquals(mdest, log.getDefenderUnit());
        assertEquals(AttackType.INTERDICTION, log.getAttackType());
        assertFalse(idest.getCoords().equals(INT2));
    }

    @Test
    public void destInSupplyNoInterdictsSub2Away() {
        Unit msub = unitService.buildUnit(nationMe, MOV, UnitType.SUBMARINE);
        Unit idest = unitService.buildUnit(nationThem, INT2,
                UnitType.DESTROYER);
        cityService.captureCity(nationThem, PORT);
        setBuild(PORT, UnitType.TRANSPORT);
        Result<MoveCost> result = moveUnits(makeUnitList(msub),
                BETWEEN);
        assertResult(result);
        assertNoInterdiction(msub, idest, result);
        List<UnitAttackedBattleLog> logs = getUnitAttackedLogs();
        assertEquals(0, logs.size());
    }

    private ArrayList<UnitAttackedBattleLog> getUnitAttackedLogs() {
        ArrayList<UnitAttackedBattleLog> list = new ArrayList<>();
        logDao.getUnitAttackedBattleLogs(nationMe).forEach(list::add);
        return list;
    }

    @Test
    public void twoDestInSupplyInterdictsDestNoStack() {
        Unit mdest = unitService
                .buildUnit(nationMe, MOV, UnitType.DESTROYER);
        Unit idest = unitService.buildUnit(nationThem, INT,
                UnitType.DESTROYER);
        Unit idest2 = unitService.buildUnit(nationThem, INT2,
                UnitType.DESTROYER);
        cityService.captureCity(nationThem, PORT);
        setBuild(PORT, UnitType.TRANSPORT);
        Result<MoveCost> result = moveUnits(makeUnitList(mdest),
                BETWEEN);
        assertFalseResult(result);
        assertFalse(idest.getCoords().equals(idest2.getCoords()));
        assertInterdiction(mdest, idest, result);
    }

    @Test
    public void interdictionStopsMovement() {
        Unit mdest = unitService
                .buildUnit(nationMe, MOV, UnitType.DESTROYER);
        Unit idest = unitService.buildUnit(nationThem, INT,
                UnitType.DESTROYER);
        cityService.captureCity(nationThem, PORT);
        setBuild(PORT, UnitType.TRANSPORT);
        Result<MoveCost> result = moveUnits(makeUnitList(mdest),
                CLOSER);
        assertFalseResult(result);
        assertFalse(mdest.getCoords().equals(CLOSER));
        assertInterdiction(mdest, idest, result);
    }

    @Test
    public void interdictionMovesXportInf() {
        Unit mxport = unitService
                .buildUnit(nationMe, MOV2, UnitType.TRANSPORT);
        mxport.setMobility(UnitBase.getUnitBase(UnitType.TRANSPORT).getMaxMobility());

        Unit minf = unitService
                .buildUnit(nationMe, MOV2, UnitType.INFANTRY);
        Unit idest = unitService.buildUnit(nationThem, INT,
                UnitType.DESTROYER);
        cityService.captureCity(nationThem, PORT);
        setBuild(PORT, UnitType.TRANSPORT);
        Result<MoveCost> result = moveUnits(makeUnitList(mxport),
                CLOSER);
        assertFalseResult(result);
        assertFalse(mxport.getCoords().equals(CLOSER));
        assertFalse(mxport.getCoords().equals(MOV2));
        assertFalse(minf.getCoords().equals(MOV2));
        assertTrue(minf.getCoords().equals(mxport.getCoords()));
        assertInterdiction(mxport, idest, result);
    }

    @Test
    public void interdictionMovesDeadXportKillsInf() {
        Unit mxport = unitService
                .buildUnit(nationMe, MOV2, UnitType.TRANSPORT);
        mxport.setMobility(UnitBase.getUnitBase(UnitType.TRANSPORT).getMaxMobility());
        mxport.setHp(1);
        Unit minf = unitService
                .buildUnit(nationMe, MOV2, UnitType.INFANTRY);
        unitService.buildUnit(nationThem, INT,
                UnitType.DESTROYER);
        cityService.captureCity(nationThem, PORT);
        setBuild(PORT, UnitType.TRANSPORT);
        Result<MoveCost> result = moveUnits(makeUnitList(mxport),
                CLOSER);
        assertFalseResult(result);
        assertFalse(mxport.isAlive());
        assertFalse(minf.isAlive());
    }

    @Test
    public void friendlyDestInSupplyNoInterdictsDest() {
        friendlyDeclared();
        Unit mdest = unitService
                .buildUnit(nationMe, MOV, UnitType.DESTROYER);
        Unit idest = unitService.buildUnit(nationThem, INT,
                UnitType.DESTROYER);
        cityService.captureCity(nationThem, PORT);
        Result<MoveCost> result = moveUnits(makeUnitList(mdest),
                BETWEEN);
        assertResult(result);
        assertNoInterdiction(mdest, idest, result);
    }

    @Test
    public void tankOnLandInterdictsDestroyerNoCounterAttack() {
        Unit mdest = unitService.buildUnit(nationMe, BETWEEN,
                UnitType.DESTROYER);
        Unit itank = unitService.buildUnit(nationThem, TANK, UnitType.TANK);
        Result<MoveCost> result = moveUnits(makeUnitList(mdest), MOV);
        assertFalseResult(result);
        assertInterdiction(mdest, itank, result);
        assertNotDamaged(result, itank);
        assertFiredOnce(result, itank);
        assertNotFired(result, mdest);
    }

    @Test
    public void tankOnLandInterdictsBBNoCounterAttack() {
        Unit mbb = unitService.buildUnit(nationMe, BETWEEN,
                UnitType.BATTLESHIP);
        Unit itank = unitService.buildUnit(nationThem, TANK, UnitType.TANK);
        Result<MoveCost> result = moveUnits(makeUnitList(mbb), MOV);
        assertFalseResult(result);
        assertInterdiction(mbb, itank, result);
        assertNotDamaged(result, itank);
        assertFiredOnce(result, itank);
        assertNotFired(result, mbb);
    }

    @Test
    public void infOnLandNoInterdictsDestroyer() {
        Unit mdest = unitService.buildUnit(nationMe, BETWEEN,
                UnitType.DESTROYER);
        Unit iinf = unitService.buildUnit(nationThem, TANK,
                UnitType.INFANTRY);
        Result<MoveCost> result = moveUnits(makeUnitList(mdest), MOV);
        assertResult(result);
        assertNoInterdiction(mdest, iinf, result);
    }

    @Test
    public void tankInCityInterdictsDestroyerNoCounterAttack() {
        Unit mdest = unitService.buildUnit(nationMe, BETWEEN,
                UnitType.DESTROYER);
        Unit itank = unitService.buildUnit(nationThem, PORT, UnitType.TANK);
        cityService.captureCity(nationThem, PORT);
        Result<MoveCost> result = moveUnits(makeUnitList(mdest),
                BYPORT);
        assertFalseResult(result);
        assertInterdiction(mdest, itank, result);
        assertNotDamaged(result, itank);
        assertNotFired(result, mdest);
    }

    @Test
    public void nbInterdictsAttack() {
        Unit mdest = unitService
                .buildUnit(nationMe, MOV, UnitType.DESTROYER);
        Unit inb = unitService.buildUnit(nationThem, INT,
                UnitType.NAVAL_BOMBER);
        Result<MoveCost> result = moveUnits(makeUnitList(mdest),
                BETWEEN);
        assertFalseResult(result);
        assertInterdiction(mdest, inb, result);
    }

    @Test
    public void fighterInterceptsInterdictingNb() {
        Unit mdest = unitService
                .buildUnit(nationMe, MOV, UnitType.DESTROYER);
        Unit fighter = unitService
                .buildUnit(nationMe, TANK, UnitType.FIGHTER);
        unitService.buildUnit(nationThem, INT,
                UnitType.SUPPLY);
        Unit inb = unitService.buildUnit(nationThem, INT2,
                UnitType.NAVAL_BOMBER);
        Result<MoveCost> result = moveUnits(makeUnitList(mdest),
                BETWEEN);
        assertResult(result);
        assertNotDamaged(result, mdest);
        assertMoved(result, fighter);
        assertEquals(inb.getCoords(), INT2);
    }

    @Test
    public void nb2InterdictsAttack() {
        Unit mdest = unitService
                .buildUnit(nationMe, MOV, UnitType.DESTROYER);
        unitService.buildUnit(nationThem, INT,
                UnitType.SUPPLY);
        Unit inb = unitService.buildUnit(nationThem, INT2,
                UnitType.NAVAL_BOMBER);
        Result<MoveCost> result = moveUnits(makeUnitList(mdest),
                BETWEEN);
        assertFalseResult(result);
        assertInterdiction(mdest, inb, result);
        assertEquals(inb.getCoords(), INT2);
        assertFired(result, inb);
    }

    @Test
    public void nb2OnCarrierResuppliesInterdictsAttack() {
        Unit mdest = unitService
                .buildUnit(nationMe, MOV, UnitType.DESTROYER);

        unitService
                .buildUnit(nationThem, INT2, UnitType.CARRIER);
        Unit inb = unitService.buildUnit(nationThem, INT2,
                UnitType.NAVAL_BOMBER);
        Result<MoveCost> result = moveUnits(makeUnitList(mdest),
                BETWEEN);
        assertFalseResult(result);
        assertInterdiction(mdest, inb, result);
        assertEquals(inb.getCoords(), INT2);
        assertNotFired(result, inb);
    }

    @Test
    public void nbNoSeeNoInterdictsAttack() {
        Unit mdest = unitService
                .buildUnit(nationMe, MOV, UnitType.DESTROYER);
        Unit inb = unitService.buildUnit(nationThem, INT2,
                UnitType.NAVAL_BOMBER);
        Result<MoveCost> result = moveUnits(makeUnitList(mdest),
                BETWEEN);
        assertResult(result);
        assertNoInterdiction(mdest, inb, result);
    }

    @Test
    public void fighterNoInterdictsAttack() {
        Unit mdest = unitService
                .buildUnit(nationMe, MOV, UnitType.DESTROYER);
        Unit inb = unitService.buildUnit(nationThem, INT,
                UnitType.FIGHTER);
        Result<MoveCost> result = moveUnits(makeUnitList(mdest),
                BETWEEN);
        assertResult(result);
        assertNoInterdiction(mdest, inb, result);
    }

    private void assertInterdiction(Unit mdest, Unit idest,
                                    Result<MoveCost> result) {
        assertDamaged(result, mdest);
        assertMoved(result, idest);
    }

    private void assertNoInterdiction(Unit mdest, Unit iunit,
                                      Result<MoveCost> result) {
        assertNotDamaged(result, mdest);
        assertNotMoved(result, iunit);
    }

}
