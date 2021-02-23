package com.kenstevens.stratinit.balance;

import com.kenstevens.stratinit.type.UnitType;

public class BalanceResult {
	UnitType attackerType;
	UnitType defenderType;
	float avgNumAttacks;
	float avgAttHpRemaining;
	float avgDefHpRemaining;
	int percAttackerDied;
	String defSector;
	
	public UnitType getAttackerType() {
		return attackerType;
	}

	public void setAttackerType(UnitType attackerType) {
		this.attackerType = attackerType;
	}

	public UnitType getDefenderType() {
		return defenderType;
	}

	public void setDefenderType(UnitType defenderType) {
		this.defenderType = defenderType;
	}

	public float getAvgNumAttacks() {
		return avgNumAttacks;
	}

	public void setAvgNumAttacks(float avgNumAttacks) {
		this.avgNumAttacks = avgNumAttacks;
	}

	public float getAvgAttHpRemaining() {
		return avgAttHpRemaining;
	}

	public void setAvgAttHpRemaining(float avgAttHpRemaining) {
		this.avgAttHpRemaining = avgAttHpRemaining;
	}

	public float getAvgDefHpRemaining() {
		return avgDefHpRemaining;
	}

	public void setAvgDefHpRemaining(float avgDefHpRemaining) {
		this.avgDefHpRemaining = avgDefHpRemaining;
	}

	public int getPercAttackerDied() {
		return percAttackerDied;
	}

	public void setPercAttackerDied(int percAttackerDied) {
		this.percAttackerDied = percAttackerDied;
	}

	public String getDefSector() {
		return defSector;
	}

	public void setDefSector(String defSector) {
		this.defSector = defSector;
	}

	public BalanceResult() {
	}

	public BalanceResult(UnitType attackerType, UnitType defenderType,
			float avgNumAttacks, float avgAttHpRemaining,
			float avgDefHpRemaining, int percAttackerDied, String defSector) {
		super();
		this.attackerType = attackerType;
		this.defenderType = defenderType;
		this.avgNumAttacks = avgNumAttacks;
		this.avgAttHpRemaining = avgAttHpRemaining;
		this.avgDefHpRemaining = avgDefHpRemaining;
		this.percAttackerDied = percAttackerDied;
		this.defSector = defSector;
	}
	
}
