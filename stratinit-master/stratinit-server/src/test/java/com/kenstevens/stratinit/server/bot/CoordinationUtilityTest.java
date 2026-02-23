package com.kenstevens.stratinit.server.bot;

import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.model.World;
import com.kenstevens.stratinit.server.bot.action.AttackEnemyAction;
import com.kenstevens.stratinit.server.bot.action.AttackNavalAction;
import com.kenstevens.stratinit.server.bot.action.AttackWithAirAction;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CoordinationUtilityTest {

    private BotWeights weights;
    private Game game;
    private World world;

    @BeforeEach
    void setUp() {
        weights = new BotWeights();
        game = new Game("test");
        game.setGamesize(20);
        game.setIslands(1);
        game.setStartTime(new Date(0));
        game.setEnds(new Date(1000000));
        world = new World(game, false);
    }

    private Unit makeUnit(UnitType type, SectorCoords coords, int id) {
        Unit unit = new Unit();
        unit.setType(type);
        unit.setCoords(coords);
        unit.setId(id);
        unit.setAlive(true);
        unit.setMobility(unit.getUnitBase().getMobility());
        unit.setHp(unit.getUnitBase().getHp());
        return unit;
    }

    private BotWorldState buildState(List<Unit> myUnits) {
        return new BotWorldState(
                null, game, myUnits, Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyMap(), Collections.emptyMap(),
                Collections.emptySet(), Collections.emptyList(),
                world, Collections.emptySet(), Collections.emptyList(),
                500000L // mid-game
        );
    }

    @Test
    void coordinationBonusIncreasesWithAllyCount() {
        SectorCoords target = new SectorCoords(5, 5);
        Unit enemy = makeUnit(UnitType.INFANTRY, target, 100);

        // 3 infantry near the target (within mobility range)
        Unit inf1 = makeUnit(UnitType.INFANTRY, new SectorCoords(5, 4), 1);
        Unit inf2 = makeUnit(UnitType.INFANTRY, new SectorCoords(4, 5), 2);
        Unit inf3 = makeUnit(UnitType.INFANTRY, new SectorCoords(5, 6), 3);

        BotWorldState state2 = buildState(List.of(inf1, inf2));
        BotWorldState state3 = buildState(List.of(inf1, inf2, inf3));

        AttackEnemyAction action = new AttackEnemyAction(inf1, enemy, null, null);

        double utility2 = action.computeUtility(state2, weights);
        double utility3 = action.computeUtility(state3, weights);

        assertTrue(utility3 > utility2,
                "More allies should yield higher utility: 3-ally=" + utility3 + " > 2-ally=" + utility2);
    }

    @Test
    void isolatedAttackGetsNoCoordinationBonus() {
        SectorCoords target = new SectorCoords(5, 5);
        Unit enemy = makeUnit(UnitType.INFANTRY, target, 100);

        // Single infantry near target
        Unit inf1 = makeUnit(UnitType.INFANTRY, new SectorCoords(5, 4), 1);

        BotWorldState stateAlone = buildState(List.of(inf1));

        // Same setup but with a far-away unit that can't reach
        Unit farUnit = makeUnit(UnitType.INFANTRY, new SectorCoords(19, 19), 2);
        BotWorldState stateWithFar = buildState(List.of(inf1, farUnit));

        AttackEnemyAction action = new AttackEnemyAction(inf1, enemy, null, null);

        double utilAlone = action.computeUtility(stateAlone, weights);
        double utilWithFar = action.computeUtility(stateWithFar, weights);

        assertEquals(utilAlone, utilWithFar, 0.001,
                "Far-away unit should not affect coordination bonus");
    }

    @Test
    void airSupportBonusWhenGroundForcesNearby() {
        SectorCoords target = new SectorCoords(5, 5);

        // Air unit attacking the target
        Unit fighter = makeUnit(UnitType.FIGHTER, new SectorCoords(4, 4), 1);

        // Ground force that can reach the target
        Unit inf = makeUnit(UnitType.INFANTRY, new SectorCoords(5, 4), 2);

        BotWorldState stateWithGround = buildState(List.of(fighter, inf));
        BotWorldState stateNoGround = buildState(List.of(fighter));

        AttackWithAirAction action = new AttackWithAirAction(fighter, target, null, null, false);

        double utilWithGround = action.computeUtility(stateWithGround, weights);
        double utilNoGround = action.computeUtility(stateNoGround, weights);

        assertTrue(utilWithGround > utilNoGround,
                "Air strike should get bonus when ground forces nearby: " +
                        utilWithGround + " > " + utilNoGround);
        assertEquals(utilWithGround - utilNoGround, weights.airSupportBonus, 0.001,
                "Bonus should equal airSupportBonus weight");
    }

    @Test
    void airSupportBonusBoostsAirAboveBaseGround() {
        SectorCoords target = new SectorCoords(5, 5);
        Unit enemy = makeUnit(UnitType.INFANTRY, target, 100);

        Unit fighter = makeUnit(UnitType.FIGHTER, new SectorCoords(4, 4), 1);
        Unit inf = makeUnit(UnitType.INFANTRY, new SectorCoords(5, 4), 2);

        BotWorldState state = buildState(List.of(fighter, inf));

        AttackWithAirAction airAction = new AttackWithAirAction(fighter, target, null, null, false);
        AttackWithAirAction airActionAlone = new AttackWithAirAction(fighter, target, null, null, false);

        BotWorldState stateAlone = buildState(List.of(fighter));

        double airWithGround = airAction.computeUtility(state, weights);
        double airAlone = airActionAlone.computeUtility(stateAlone, weights);

        assertTrue(airWithGround > airAlone,
                "Air utility with ground support (" + airWithGround +
                        ") should exceed air alone (" + airAlone + ")");
        assertEquals(weights.airSupportBonus, airWithGround - airAlone, 0.001,
                "Boost should equal airSupportBonus");
    }

    @Test
    void navalEscortBonusNearTransport() {
        SectorCoords target = new SectorCoords(5, 5);
        Unit enemyShip = makeUnit(UnitType.DESTROYER, target, 100);

        Unit destroyer = makeUnit(UnitType.DESTROYER, new SectorCoords(4, 5), 1);
        Unit transport = makeUnit(UnitType.TRANSPORT, new SectorCoords(6, 5), 2);

        BotWorldState stateWithTransport = buildState(List.of(destroyer, transport));
        BotWorldState stateNoTransport = buildState(List.of(destroyer));

        AttackNavalAction action = new AttackNavalAction(destroyer, enemyShip, null, null);

        double utilWithTransport = action.computeUtility(stateWithTransport, weights);
        double utilNoTransport = action.computeUtility(stateNoTransport, weights);

        assertTrue(utilWithTransport > utilNoTransport,
                "Naval attack should get escort bonus near transport: " +
                        utilWithTransport + " > " + utilNoTransport);
        assertEquals(utilWithTransport - utilNoTransport, weights.navalEscortBonus, 0.001,
                "Bonus should equal navalEscortBonus weight");
    }

    @Test
    void massAttackThresholdRespected() {
        SectorCoords target = new SectorCoords(5, 5);
        Unit enemy = makeUnit(UnitType.INFANTRY, target, 100);

        // Only 1 ally in range (self) — below threshold of 2
        Unit inf1 = makeUnit(UnitType.INFANTRY, new SectorCoords(5, 4), 1);

        // Set threshold high so 1 ally doesn't qualify
        BotWeights highThreshold = new BotWeights();
        highThreshold.massAttackThreshold = 4.0;

        // 2 allies near target — still below threshold - 1 = 3
        Unit inf2 = makeUnit(UnitType.INFANTRY, new SectorCoords(4, 5), 2);
        Unit inf3 = makeUnit(UnitType.INFANTRY, new SectorCoords(5, 6), 3);

        BotWorldState state = buildState(List.of(inf1, inf2, inf3));

        AttackEnemyAction action = new AttackEnemyAction(inf1, enemy, null, null);

        // With high threshold, 2 allies (excluding self) should not meet threshold - 1 = 3
        double utilHighThreshold = action.computeUtility(state, highThreshold);

        // With default threshold (2.0), 2 allies (excluding self) >= threshold - 1 = 1
        double utilDefaultThreshold = action.computeUtility(state, weights);

        assertTrue(utilDefaultThreshold > utilHighThreshold,
                "Default threshold should allow bonus but high threshold should not: " +
                        utilDefaultThreshold + " > " + utilHighThreshold);
    }
}
