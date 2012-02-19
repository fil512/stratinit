package com.kenstevens.stratinit.util;

import com.kenstevens.stratinit.model.AttackType;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.type.UnitType;

public final class UnitHelper {
	private UnitHelper() {}

	public static final int range(Unit unit) {
		return actualRange(unit, unit.getMobility());
	}

	public static final int maxRange(Unit unit) {
		return actualRange(unit, unit.getMaxMobility());
	}

	private static int actualRange(Unit unit, int mobility) {
		if (!unit.requiresFuel()) {
			return mobility;
		}
		int fuel = unit.getFuel();
		int range = mobility;
		// TODO TEST I changed this from fuel < mobility to fuel <= mobility.  Hope this is correct!
		if (fuel <= mobility) {
			if (fuel > 0) {
				range = fuel - 1;
			} else if (fuel == 0) {
				range = 0;
			}
		}
		return range;
	}


	public static Unit getMaxRange(Iterable<Unit> units) {
		int range = Integer.MIN_VALUE;
		Unit longest = null;
		for (Unit unit : units) {
			if (range(unit) > range) {
				range = range(unit);
				longest = unit;
			}
		}
		return longest;
	}

	public static boolean canAttack(Unit unit, AttackType attackType) {
		if (attackType == AttackType.INTERDICTION) {
			if (!unit.getUnitBase().canInterdict()) {
				return false;
			}
		} else if (attackType == AttackType.INTERCEPTION
				&& unit.getType() != UnitType.FIGHTER) {
			return false;
		}
		if (unit.getUnitBase().getAttack() > 0) {
			return true;
		}
		if (attackType == AttackType.INITIAL_ATTACK) {
			return unit.isBomber();
		}
		return false;
	}

	public static boolean canCarry(Unit holder, Unit unit, WorldSector worldSector) {
		if (!unit.getNation().equals(holder.getNation())) {
			return false;
		}
		if (holder.getUnitBase().isAir()) {
			return airCanCarry(holder, unit, worldSector);
		}
		if (holder.isCarrier()) {
			return unit.isLightAir() || unit.isInfantry();
		}
		if (holder.isTransport()) {
			return unit.isLand();
		}
		if (holder.isEngineer() || holder.isSubmarine()) {
			return unit.devastates();
		}
		
		return false;
	}

	public static boolean airCanCarry(Unit holder, Unit unit, WorldSector worldSector) {
		if (!unit.isInfantry()) {
			return false;
		}
		if (worldSector.isPlayerCity()
				&& (unit.getMobility() == 0 || unit.isHurt())) {
			return false;
		}
		return true;
	}
}
