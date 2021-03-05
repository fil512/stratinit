package com.kenstevens.stratinit.client.model.stats;

import com.kenstevens.stratinit.client.model.BattleLogEntry;
import com.kenstevens.stratinit.client.model.BattleLogList;
import com.kenstevens.stratinit.dto.SIUnitBuilt;
import com.kenstevens.stratinit.type.UnitType;

import java.util.List;
import java.util.Set;

public class StatsHolder {
	private final DominanceMap dominanceMap = new DominanceMap();
	private final UnitDeltaMap unitDelta = new UnitDeltaMap();
	private final UnitPreservationMap unitPreservationMap = new UnitPreservationMap();

	public void clear() {
		dominanceMap.clear();
		unitDelta.clear();
		unitPreservationMap.clear();
	}

	public void addBuilt(List<SIUnitBuilt> unitsBuilt) {
		unitPreservationMap.addBuilt(unitsBuilt);
	}

	public void rebuildStats(BattleLogList battleLogList) {
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
	
	public Set<String> getOpponents() {
		return dominanceMap.getOpponents();
	}

	public List<String[]> getOpponentStats() {
		return dominanceMap.getOpponentStats();
	}

	public List<String[]>  getOpponentStats(String opponent) {
		return unitDelta.getOpponentStats(opponent);
	}

	public List<String[]>  getUnitRecordStats() {
		return unitPreservationMap.getUnitRecordStats();
	}

	public String getWorstOpponent() {
		return dominanceMap.getWorstOpponent();
	}
}
