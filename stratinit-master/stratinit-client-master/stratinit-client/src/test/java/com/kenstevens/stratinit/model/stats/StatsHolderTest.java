package com.kenstevens.stratinit.model.stats;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.kenstevens.stratinit.dto.SIBattleLog;
import com.kenstevens.stratinit.model.AttackType;
import com.kenstevens.stratinit.model.BattleLogEntry;
import com.kenstevens.stratinit.model.BattleLogList;
import com.kenstevens.stratinit.model.FlakBattleLog;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitBase;
import com.kenstevens.stratinit.type.UnitType;

public class StatsHolderTest extends TwoPlayerTest {
	@Test
	public void canParseFlak() {

		Unit attackerUnit = new Unit(nationA, UnitType.FIGHTER, coords);
		FlakBattleLog log = new FlakBattleLog(AttackType.INITIAL_ATTACK,
				attackerUnit, nationB, coords, 1);
		log.setId(1);
		SIBattleLog silog = new SIBattleLog(nationA, log);
		BattleLogEntry entry = new BattleLogEntry(silog);
		entry.setOpponent(nationB.getName());
		
		StatsHolder statsHolder = new StatsHolder();
		BattleLogList battleLogList = new BattleLogList();
		List<BattleLogEntry> entries = new ArrayList<BattleLogEntry>();
		entries.add(entry);
		
		battleLogList.addAll(entries );
		statsHolder.rebuildStats(battleLogList);

		List<String[]> stats = statsHolder.getOpponentStats(nationB.getName());
		assertEquals("TOTAL", stats.get(1)[0]);
		assertEquals("1", stats.get(1)[1]);
		assertEquals("0", stats.get(1)[2]);
		assertEquals("-1", stats.get(1)[3]);
		assertEquals("-"+UnitBase.getUnitBase(UnitType.FIGHTER).getProductionTime(), stats.get(1)[4]);
	}
}