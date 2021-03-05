package com.kenstevens.stratinit.client.model;

import com.kenstevens.stratinit.type.NewsCategory;
import com.kenstevens.stratinit.type.SectorCoords;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CityNukedBattleLog extends BattleLog {
	public CityNukedBattleLog(Unit attackerUnit, Nation defender, SectorCoords coords) {
		super(AttackType.NUKE, attackerUnit.getNation(), defender, attackerUnit, coords);
	}

	public CityNukedBattleLog() {}
	
	@Override
	public List<String> getAttackerMessages() {
		ArrayList<String> retval = new ArrayList<String>();
		retval.add("Your "+attackerUnit+" nuked "+defender+" city at "+coords);
		return retval;

	}

	@Override
	public List<String> getDefenderMessages() {
		ArrayList<String> retval = new ArrayList<String>();
		String enemyString = attackerUnit.toEnemyString();
		retval.add(enemyString+" nuked your city at "+coords);
		return retval;
	}

	@Override
	public NewsCategory getNewsCategory() {
		return NewsCategory.NUCLEAR_DETONATIONS;
	}
}
