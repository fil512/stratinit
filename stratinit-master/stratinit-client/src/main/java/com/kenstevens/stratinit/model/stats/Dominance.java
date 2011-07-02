package com.kenstevens.stratinit.model.stats;

import com.kenstevens.stratinit.model.BattleLogEntry;
import com.kenstevens.stratinit.model.UnitBase;
import com.kenstevens.stratinit.type.UnitType;

public class Dominance {
	public int iLost;
	public int iKilled;
	public int iLostValue;
	public int iKilledValue;

	public int getNet() {
		return iKilled - iLost;
	}
	
	public int getNetValue() {
		return iKilledValue - iLostValue;
	}
	
	public int getDominance() {
		if (iLostValue + iKilledValue == 0) {
			return 0;
		}
		return 100 * iKilledValue / (iLostValue + iKilledValue); 
	}

	public void add(BattleLogEntry entry, UnitType unitType) {
		if (entry.isIDied()) {
			iLost += 1;
			iLostValue += getValue(unitType);
		} else if (entry.isOtherDied()) {
			iKilled += 1;
			iKilledValue += getValue(unitType);
		}
	}

	private int getValue(UnitType unitType) {
		UnitBase unitBase = UnitBase.getUnitBase(unitType);
		return unitBase.getProductionTime();
	}

	public int getCost(UnitType unitType) {
		return getNet() * getValue(unitType);
	}

	public void add(Dominance dominance) {
		iLost += dominance.iLost;
		iKilled += dominance.iKilled;
		iLostValue += dominance.iLostValue;
		iKilledValue += dominance.iKilledValue;
	}
}
