package com.kenstevens.stratinit.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

import com.kenstevens.stratinit.type.NewsCategory;
import com.kenstevens.stratinit.type.SectorCoords;

@Entity
public class CityCapturedBattleLog extends BattleLog {
	private boolean attackerDied = false;

	public CityCapturedBattleLog(AttackType attackType, Unit attackerUnit, Nation defender, SectorCoords coords) {
		super(attackType, attackerUnit.getNation(), defender, attackerUnit, coords);
	}

	public CityCapturedBattleLog() {}
	
	@Override
	public List<String> getAttackerMessages() {
		ArrayList<String> retval = new ArrayList<String>();
		retval.add("Your "+attackerUnit+" captured "+defender+" city at "+coords);
		return retval;

	}

	@Override
	public List<String> getDefenderMessages() {
		ArrayList<String> retval = new ArrayList<String>();
		String enemyString = attackerUnit.toEnemyString();
		retval.add(enemyString+" captured your city at "+coords);
		return retval;
	}

	@Override
	public NewsCategory getNewsCategory() {
		return NewsCategory.CONQUEST;
	}

	public void setAttackerDied(boolean attackerDied) {
		this.attackerDied = attackerDied;
	}

	public boolean isAttackerDied() {
		return attackerDied;
	}
}
