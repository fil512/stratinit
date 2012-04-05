package com.kenstevens.stratinit.server.remote.move;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.kenstevens.stratinit.model.MoveCost;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.remote.StratInitWebBase;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

public class DiagPathTest extends StratInitWebBase {
	public static final SectorCoords START = new SectorCoords(6,3);
	public static final SectorCoords START2 = new SectorCoords(7,4);
	public static final SectorCoords END = new SectorCoords(10,5);


	private String myTypes[] = {
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
	private String myIslands[] = {
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

	@Before
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
		assertEquals(result.toString(), 4, supply.getUnitBase().getMobility() - supply.getMobility());
	}

	@Test
	public void shortestPath2() {
		Unit supply = unitDaoService.buildUnit(nationMe, START2, UnitType.SUPPLY);
		Result<MoveCost> result = moveUnits(makeUnitList(supply), END);
		assertResult(result);
		assertEquals(result.toString(), 3, supply.getUnitBase().getMobility() - supply.getMobility());
	}

}
