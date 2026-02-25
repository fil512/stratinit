package com.kenstevens.stratinit.server.rest.move;

import com.kenstevens.stratinit.BaseStratInitControllerTest;
import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.MoveCost;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.model.World;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import com.kenstevens.stratinit.world.WorldHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests infantry movement along a narrow 1-sector-wide strip of land
 * surrounded by water, including move order execution via updateUnit().
 */
public class NarrowLandStripMoveTest extends BaseStratInitControllerTest {
    // A narrow vertical strip of land at x=7, from y=0 to y=6
    // Everything else is water. Start city at (7,0) so player can join.
    // Second start city at (0,0) for the required second island.
    private static final SectorCoords TOP = new SectorCoords(7, 0);
    private static final SectorCoords MID = new SectorCoords(7, 3);
    private static final SectorCoords BOTTOM = new SectorCoords(7, 6);

    private final String[] myTypes = {
            //             11111
            //   012345678901234
            "S......S.......", // 0
            ".......#.......", // 1
            ".......#.......", // 2
            ".......#.......", // 3
            ".......#.......", // 4
            ".......#.......", // 5
            ".......#.......", // 6
            "...............", // 7
            "...............", // 8
            "...............", // 9
            "...............", // 10
            "...............", // 11
            "...............", // 12
            "...............", // 13
            "...............", // 14
    };
    private final String[] myIslands = {
            "0......1.......",
            ".......1.......",
            ".......1.......",
            ".......1.......",
            ".......1.......",
            ".......1.......",
            ".......1.......",
            "...............",
            "...............",
            "...............",
            "...............",
            "...............",
            "...............",
            "...............",
            "...............",
    };

    @Override
    protected World getWorld(Game testGame) {
        return WorldHelper.newWorld(testGame, myTypes, myIslands);
    }

    @BeforeEach
    public void joinGame() {
        joinGamePlayerMe();
    }

    @Test
    public void infantryMovesOneHexSouth() {
        SectorCoords oneStep = new SectorCoords(7, 1);
        Unit inf = unitService.buildUnit(nationMe, TOP, UnitType.INFANTRY);
        Result<MoveCost> result = moveUnits(makeUnitList(inf), oneStep);
        assertResult(result);
        assertEquals(oneStep, inf.getCoords());
    }

    @Test
    public void infantryMovesPartialThenMoveOrder() {
        // Infantry has 6 mob (base 2 + 2 addMobility calls), out of supply cost=2/hex
        // Can move 3 hexes this turn, remaining 3 should get a move order
        Unit inf = unitService.buildUnit(nationMe, TOP, UnitType.INFANTRY);
        inf.addMobility();
        inf.addMobility();
        Result<MoveCost> result = moveUnits(makeUnitList(inf), BOTTOM);
        // Unit should have moved partway and set a move order for the rest
        assertTrue(inf.getCoords().y > TOP.y,
                "Unit should have moved south from " + TOP + " but is at " + inf.getCoords());
        assertNotNull(inf.getUnitMove(), "Move order should be set for remaining distance");
        assertEquals(BOTTOM, inf.getUnitMove().getCoords());
    }

    @Test
    public void moveOrderExecutesAlongNarrowStrip() {
        Unit inf = unitService.buildUnit(nationMe, TOP, UnitType.INFANTRY);
        inf.setMobility(0);
        unitService.setUnitMove(inf, BOTTOM);

        // First tick: gains 2 mobility, moves 1 hex (cost 2 per hex out of supply)
        unitService.updateUnit(inf, new Date());
        assertEquals(new SectorCoords(7, 1), inf.getCoords(),
                "Unit should have moved 1 hex south with 2 mobility at cost 2 per hex");
        assertNotNull(inf.getUnitMove(), "Move order should still be set");

        // Second tick: gains 2 more mobility, moves another hex
        unitService.updateUnit(inf, new Date());
        assertTrue(inf.getCoords().y > 1,
                "Unit should have moved further south but is at " + inf.getCoords());

        // Keep ticking until it reaches destination
        for (int i = 0; i < 10; i++) {
            if (inf.getCoords().equals(BOTTOM)) break;
            unitService.updateUnit(inf, new Date());
        }
        assertEquals(BOTTOM, inf.getCoords(), "Unit should have reached destination via move orders");
        assertNull(inf.getUnitMove(), "Move order should be cleared after reaching destination");
    }

    @Test
    public void immediateMoveMidwayAlongStrip() {
        Unit inf = unitService.buildUnit(nationMe, MID, UnitType.INFANTRY);
        // 3 hexes from MID to BOTTOM, cost 2 each out of supply = 6 needed
        inf.addMobility();
        inf.addMobility();
        Result<MoveCost> result = moveUnits(makeUnitList(inf), BOTTOM);
        assertResult(result);
        assertEquals(BOTTOM, inf.getCoords());
    }
}
