package com.kenstevens.stratinit.client.model;

import com.kenstevens.stratinit.type.NewsCategory;
import com.kenstevens.stratinit.type.SectorCoords;

import jakarta.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
public class FlakBattleLog extends BattleLog {
	public FlakBattleLog(AttackType attackType, Unit attackerUnit, Nation defender, SectorCoords coords, int flakDamage) {
		super(attackType, attackerUnit.getNation(), defender, attackerUnit, coords, flakDamage);
	}

	public FlakBattleLog() {}
	
	@Override
	public List<String> getAttackerMessages() {
		ArrayList<String> retval = new ArrayList<String>();
		retval.add("Your "+attackerUnit+" was destroyed by "+defender+" flak from "+coords);
		return retval;

	}

	@Override
	public List<String> getDefenderMessages() {
		ArrayList<String> retval = new ArrayList<String>();
		retval.add(attackerUnit.toEnemyString()+" was destroyed by your flak at "+coords);
		return retval;
	}

	@Override
	public NewsCategory getNewsCategory() {
		return NewsCategory.AIR_DEFENCE;
	}
}
