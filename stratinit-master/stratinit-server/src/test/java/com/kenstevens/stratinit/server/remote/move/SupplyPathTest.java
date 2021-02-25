package com.kenstevens.stratinit.server.remote.move;

import com.kenstevens.stratinit.model.MoveCost;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.remote.StratInitWebBase;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SupplyPathTest extends StratInitWebBase {
    public static final SectorCoords SUPPLY = new SectorCoords(6, 6);
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

    protected String[] getIslands() {
        return myIslands;
    }

    protected String[] getTypes() {
        return myTypes;
    }

    @Test
    public void shortestPath() {
        unitDaoService.buildUnit(nationMe, SUPPLY, UnitType.SUPPLY);
        Unit dest = unitDaoService.buildUnit(nationMe, START, UnitType.DESTROYER);
        dest.setMobility(12);
        Result<MoveCost> result = moveUnits(makeUnitList(dest), END);
        assertResult(result);
        assertEquals(0, dest.getMobility(), result.toString());
        assertEquals(END, dest.getCoords(), result.toString());
    }

    @Test
    public void wentNorth() {
        unitDaoService.buildUnit(nationMe, SUPPLY, UnitType.SUPPLY);
        Unit dest = unitDaoService.buildUnit(nationMe, START, UnitType.DESTROYER);
        dest.setMobility(11);
        Result<MoveCost> result = moveUnits(makeUnitList(dest), END);
        assertResult(result);
        assertEquals(0, dest.getMobility(), result.toString());
        assertEquals(END.y - 1, dest.getCoords().y, result.toString());
    }

}
