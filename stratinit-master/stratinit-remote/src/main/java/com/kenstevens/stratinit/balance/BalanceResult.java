package com.kenstevens.stratinit.balance;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import com.kenstevens.stratinit.type.UnitType;

@Root
public class BalanceResult {
	@Attribute
	UnitType attackerType;
	@Attribute
	UnitType defenderType;
	@Attribute
	float avgNumAttacks;
	@Attribute
	float avgAttHpRemaining;
	@Attribute
	float avgDefHpRemaining;
	@Attribute
	int percAttackerDied;
	@Attribute
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
