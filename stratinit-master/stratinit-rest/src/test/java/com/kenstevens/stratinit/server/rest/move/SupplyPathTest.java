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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SupplyPathTest extends BaseStratInitControllerTest {
    public static final SectorCoords SUPPLY = new SectorCoords(5, 6);
    public static final SectorCoords START = new SectorCoords(6, 11);
    public static final SectorCoords END = new SectorCoords(10, 11);



    private final String[] myTypes = {
            //             11111
            //   012345678901234
            "SS#....#S#.....", // 0
            "###....#S#.....", // 1
            "###....###.....", // 2
            "###....###.....", // 3
            "###....###.....", // 4
            "###....###.....", // 5
            "###............", // 6x
            "###....###.....", // 7x
            "###....###.....", // 8x
            "###....###.....", // 9x
            "###....###.....", // 10x
            "###....###.....", // 11x
            "###....###.....", // 12
            "###....###.....", // 13
            "###............", // 14
    };
    private final String[] myIslands = {
            "000....111.....",
            "000....111.....",
            "000....111.....",
            "000....111.....",
            "000....111.....",
            "000....111.....",
            "000............",
            "000....111.....",
            "000....111.....",
            "000....111.....",
            "000....111.....",
            "000....111.....",
            "000....111.....",
            "000....111.....",
            "000............",
    };

    @BeforeEach
    public void joinGame() {
        joinGamePlayerMe();
    }

    @Override
    protected World getWorld(Game testGame) {
        return WorldHelper.newWorld(testGame, myTypes, myIslands);
    }

    @Test
    public void shortestPath() {
        unitService.buildUnit(nationMe, SUPPLY, UnitType.SUPPLY);
        Unit dest = unitService.buildUnit(nationMe, START, UnitType.DESTROYER);
        // In hex, northern route is 14 steps: 13 in-supply (cost 1) + 1 out-of-supply (cost 2) = 15
        dest.setMobility(15);
        Result<MoveCost> result = moveUnits(makeUnitList(dest), END);
        assertResult(result);
        assertEquals(0, dest.getMobility(), result.toString());
        assertEquals(END, dest.getCoords(), result.toString());
    }

    @Test
    public void wentNorth() {
        unitService.buildUnit(nationMe, SUPPLY, UnitType.SUPPLY);
        Unit dest = unitService.buildUnit(nationMe, START, UnitType.DESTROYER);
        // In hex, 13 mobility covers the 13 in-supply steps, stopping at (10,10)
        dest.setMobility(13);
        Result<MoveCost> result = moveUnits(makeUnitList(dest), END);
        assertResult(result);
        assertEquals(0, dest.getMobility(), result.toString());
        assertEquals(END.y - 1, dest.getCoords().y, result.toString());
    }

}
