package com.kenstevens.stratinit.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.kenstevens.stratinit.type.NewsCategory;
import com.kenstevens.stratinit.type.SectorCoords;

@Entity
public class UnitAttackedBattleLog extends BattleLog {
	private int damage = NO_DAMAGE;
	private int returnDamage = NO_DAMAGE;
	private boolean attackerDied = false;
	private boolean defenderDied = false;
	private boolean defenderOutOfAmmo = false;
	private boolean defenderCannotAttack = false;
	private int defenderCollateralUnitsSunk = 0;
	private int attackerCollateralUnitsSunk = 0;

	@ManyToOne
	private Unit defenderUnit;

	public UnitAttackedBattleLog() {
	}

	public UnitAttackedBattleLog(AttackType attackType, Unit attackerUnit,
			Unit defenderUnit, SectorCoords coords, int flakDamage) {
		super(attackType, attackerUnit.getNation(), defenderUnit.getNation(),
				attackerUnit, coords, flakDamage);
		this.defenderUnit = defenderUnit;
	}

	public UnitAttackedBattleLog(AttackType attackType, Unit attackerUnit,
			Unit defenderUnit, SectorCoords coords) {
		this(attackType, attackerUnit, defenderUnit, coords, NO_DAMAGE);
	}

	@Override
	public List<String> getAttackerMessages() {
		ArrayList<String> retval = new ArrayList<String>();
		if (getFlakDamage() != NO_DAMAGE) {
			retval.add(getFlakType(attackerUnit)+" hit your " + attackerUnit + " for "
					+ getFlakDamage());
		}
		if (damage != NO_DAMAGE) {
			if (attackType == AttackType.BOMB) {
				retval.add(attackerUnit + " bombed "
						+ defenderUnit.toEnemyString() + " in " + coords
						+ " for " + damage + " damage");
			} else {
				retval.add(attackerUnit + " attacked "
						+ defenderUnit.toEnemyString() + " in " + coords
						+ " for " + damage + " damage");
				if (returnDamage != NO_DAMAGE) {
					retval.add("Return fire hit for " + returnDamage);
				}
			}
		}
		if (attackType == AttackType.NUKE) {
			retval.add(attackerUnit + " nuked " + defenderUnit.toEnemyString()
					+ " in " + coords);
		}
		if (defenderDied) {
			retval.add(defenderUnit.toEnemyString() + " destroyed");
			if (getDefenderCollateralUnitsSunk() > 0) {
				retval.add(getDefenderCollateralUnitsSunk()
						+ " units on board went down with "
						+ defenderUnit.toEnemyString());
			}
		} else if (defenderCannotAttack) {
			retval.add(defenderUnit.toEnemyString() + " cannot return fire");
		} else if (defenderOutOfAmmo) {
			retval.add(defenderUnit.toEnemyString()
					+ " is out of supply and cannot return fire");
		} else if (attackerDied) {
			retval.add("Your " + attackerUnit.getType() + " was destroyed");
			if (getAttackerCollateralUnitsSunk() > 0) {
				retval.add(getAttackerCollateralUnitsSunk()
						+ " of your units on board went down with your "
						+ attackerUnit.getType());
			}

		}

		return retval;
	}

	private String getFlakType(Unit attackerUnit) {
		if (attackerUnit.isAir()) {
		return "Flak";
		} else {
			return "Cannons";
		}
	}

	@Override
	public List<String> getDefenderMessages() {
		ArrayList<String> retval = new ArrayList<String>();

		String enemyString = attackerUnit.toEnemyString();
		if (getFlakDamage() != NO_DAMAGE) {
			retval.add("Your "+getFlakType(attackerUnit).toLowerCase()+" hit " + enemyString + " for "
					+ getFlakDamage());
		}
		if (damage != NO_DAMAGE) {
			if (attackType == AttackType.BOMB) {
				retval.add(enemyString + " bombed your " + defenderUnit
						+ " in " + coords + " for " + " for " + damage
						+ " damage");
			} else {
				retval.add(enemyString + " attacked your " + defenderUnit
						+ " in " + coords + " for " + " for " + damage
						+ " damage");
				if (returnDamage != NO_DAMAGE) {
					retval.add("Return fire hit for " + returnDamage);
				}
			}
		}
		if (attackType == AttackType.NUKE) {
			retval.add(enemyString + " nuked your " + defenderUnit + " in "
					+ coords);
		}
		if (defenderDied) {
			retval.add("Your " + defenderUnit + " was destroyed");
			if (getDefenderCollateralUnitsSunk() > 0) {
				retval.add(getDefenderCollateralUnitsSunk()
						+ " of your units on board went down with your "
						+ defenderUnit);
			}
		} else if (defenderCannotAttack) {
			retval.add("Your " + defenderUnit + " cannot return fire");
		} else if (defenderOutOfAmmo) {
			retval.add("Your " + defenderUnit
					+ " is out of supply and cannot return fire");
		} else if (attackerDied) {
			retval.add(enemyString + " destroyed");
			if (getAttackerCollateralUnitsSunk() > 0) {
				retval.add(getAttackerCollateralUnitsSunk()
						+ " units on board went down with " + enemyString);
			}
		}

		return retval;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public int getReturnDamage() {
		return returnDamage;
	}

	public void setReturnDamage(int returnDamage) {
		this.returnDamage = returnDamage;
	}

	public boolean isAttackerDied() {
		return attackerDied;
	}

	public void setAttackerDied(boolean attackerDied) {
		this.attackerDied = attackerDied;
	}

	public boolean isDefenderDied() {
		return defenderDied;
	}

	public void setDefenderDied(boolean defenderDied) {
		this.defenderDied = defenderDied;
	}

	public Unit getDefenderUnit() {
		return defenderUnit;
	}

	public void setDefenderUnit(Unit defenderUnit) {
		this.defenderUnit = defenderUnit;
	}

	public void setDefenderOutOfAmmo(boolean defenderOutOfAmmo) {
		this.defenderOutOfAmmo = defenderOutOfAmmo;
	}

	public boolean isDefenderOutOfAmmo() {
		return defenderOutOfAmmo;
	}

	public void setDefenderCannotAttack(boolean defenderCannotAttack) {
		this.defenderCannotAttack = defenderCannotAttack;
	}

	public boolean isDefenderCannotAttack() {
		return defenderCannotAttack;
	}

	public void setDefenderCollateralUnitsSunk(int defenderCollateralUnitsSunk) {
		this.defenderCollateralUnitsSunk = defenderCollateralUnitsSunk;
	}

	public int getDefenderCollateralUnitsSunk() {
		return defenderCollateralUnitsSunk;
	}

	public void setAttackerCollateralUnitsSunk(int attackerCollateralUnitsSunk) {
		this.attackerCollateralUnitsSunk = attackerCollateralUnitsSunk;
	}

	public int getAttackerCollateralUnitsSunk() {
		return attackerCollateralUnitsSunk;
	}
	
	@Override
	public NewsCategory getNewsCategory() {
		return NewsCategory.NEWS_FROM_THE_FRONT;
	}
}
