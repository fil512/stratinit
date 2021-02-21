package com.kenstevens.stratinit.server.remote.move;

import com.kenstevens.stratinit.model.MoveCost;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.remote.BaseStratInitWebTest;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DiagPathTest extends BaseStratInitWebTest {
    public static final SectorCoords START = new SectorCoords(6, 3);
    public static final SectorCoords START2 = new SectorCoords(7, 4);
    public static final SectorCoords END = new SectorCoords(10, 5);


    private final String[] myTypes = {
            //             11111
            //   012345678901234
            "S##....S##.....", // 0
            "###....###.....", // 1
            "S##....S##.....", // 2
            "###.....##.....", // 3
            "###....#.#.....", // 4
            "###....##......", // 5
            "###....###.....", // 6
            "###....###.....", // 7
            "###............", // 8
            "###....###.....", // 9
            "###....###.....", // 10
            "###....###.....", // 11
            "###....###.....", // 12
            "###....###.....", // 13
            "###....###.....", // 14
    };
    private final String[] myIslands = {
            "000....111.....",
            "000....111.....",
            "000....111.....",
            "000.....11.....",
            "000....1.1.....",
            "000....11......",
            "000....111.....",
            "000....111.....",
            "000............",
            "000....111.....",
            "000....111.....",
            "000....111.....",
            "000....111.....",
            "000....111.....",
            "000....111.....",
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
        Unit supply = unitDaoService.buildUnit(nationMe, START, UnitType.SUPPLY);
        Result<MoveCost> result = moveUnits(makeUnitList(supply), END);
        assertResult(result);
        assertEquals(4, supply.getUnitBase().getMobility() - supply.getMobility(), result.toString());
    }

	@Test
	public void shortestPath2() {
        Unit supply = unitDaoService.buildUnit(nationMe, START2, UnitType.SUPPLY);
        Result<MoveCost> result = moveUnits(makeUnitList(supply), END);
        assertResult(result);
        assertEquals(3, supply.getUnitBase().getMobility() - supply.getMobility(), result.toString());
    }

}
