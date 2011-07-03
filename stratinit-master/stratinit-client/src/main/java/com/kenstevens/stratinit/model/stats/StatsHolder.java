package com.kenstevens.stratinit.model.stats;

import java.util.List;
import java.util.Set;

import com.kenstevens.stratinit.dto.SIUnitBuilt;
import com.kenstevens.stratinit.model.BattleLogEntry;
import com.kenstevens.stratinit.model.BattleLogList;
import com.kenstevens.stratinit.type.UnitType;

public class StatsHolder {
	private final DominanceMap dominanceMap = new DominanceMap();
	private final UnitDeltaMap unitDelta = new UnitDeltaMap();
	private final UnitPreservationMap unitPreservationMap = new UnitPreservationMap();

	protected void clear() {
		dominanceMap.clear();
		unitDelta.clear();
		unitPreservationMap.clear();
	}

	protected void addBuilt(List<SIUnitBuilt> unitsBuilt) {
		unitPreservationMap.addBuilt(unitsBuilt);
	}

	protected void rebuildStats(BattleLogList battleLogList) {
		for (BattleLogEntry entry : battleLogList) {
			if (!entry.isIDied() && !entry.isOtherDied()) {
				continue;
			}
			String opponent = entry.getOpponent();
			Dominance playerDominance = dominanceMap.getPlayerDominance(opponent);
			UnitType deadUnitType = getDeadUnitType(entry);
			if (deadUnitType != null) {
				playerDominance.add(entry, deadUnitType);
				Dominance unitDominance = unitDelta.getUnitDominance(opponent, deadUnitType);
				unitDominance.add(entry, deadUnitType);
				if (entry.isIDied()) {
					Preservation preservation = unitPreservationMap.getPreservation(deadUnitType);
					preservation.lost += 1;
				}
			}
		}
	}

	private UnitType getDeadUnitType(BattleLogEntry entry) {
		if (entry.isIDied()) {
			return getUnitType(entry.getMyUnit());
		} else if (entry.isOtherDied()) {
			return getUnitType(entry.getOpponentUnit());
		}
		return null;
	}

	private UnitType getUnitType(String unitTypeString) {
		try {
			if (BattleLogEntry.CITY.equals(unitTypeString)) {
				return null;
			} else {
				return UnitType.valueOf(unitTypeString.toUpperCase());
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	protected Set<String> getOpponents() {
		return dominanceMap.getOpponents();
	}

	protected List<String[]> getOpponentStats() {
		return dominanceMap.getOpponentStats();
	}

	protected List<String[]>  getOpponentStats(String opponent) {
		return unitDelta.getOpponentStats(opponent);
	}

	protected List<String[]>  getUnitRecordStats() {
		return unitPreservationMap.getUnitRecordStats();
	}
}
