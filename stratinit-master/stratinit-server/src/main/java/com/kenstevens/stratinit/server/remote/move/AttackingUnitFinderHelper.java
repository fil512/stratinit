package com.kenstevens.stratinit.server.remote.move;

import com.kenstevens.stratinit.model.AttackType;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitBase;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.UnitType;
import com.kenstevens.stratinit.util.AttackHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class AttackingUnitFinderHelper {
	private AttackingUnitFinderHelper() {
	}

	public static Unit findUnitToAttack(AttackType attackType,
										Unit attackingUnit, Collection<Unit> defUnits,
										WorldSector targetSector, WorldView worldView) {
		if (attackType == AttackType.INTERCEPTION) {
			removeIntercepted(defUnits);
		}
		if (defUnits.isEmpty()) {
			return null;
		}
		Unit shipOnWater = findShipOnWater(targetSector, defUnits);
		if (shipOnWater != null) {
			return shipOnWater;
		}

		if (attackType == AttackType.INTERCEPTION) {
			// when intercepting, prefer fighters.
			Unit fighter = findFighter(defUnits);
			if (fighter != null) {
				return fighter;
			}
		}

		return getUnitWithHighestMultiplier(attackType, attackingUnit,
				defUnits, worldView);
	}

	private static Unit findFighter(Collection<Unit> defUnits) {
		for (Unit unit : defUnits) {
			if (unit.getType() == UnitType.FIGHTER) {
				return unit;
			}
		}
		return null;
	}

	private static Unit findShipOnWater(WorldSector targetSector,
										Collection<Unit> defUnits) {
		if (!targetSector.isWater()) {
			return null;
		}
		// we're at sea, pick the ship if there is one
		for (Unit unit : defUnits) {
			if (unit.isNavy()) {
				return unit;
			}
		}
		return null;
	}

	private static Unit getUnitWithHighestMultiplier(AttackType attackType,
													 Unit attackingUnit, Collection<Unit> defUnits, WorldView worldView) {
		UnitBase attackerBase = attackingUnit.getUnitBase();
		int maxMultiplier = -1;
		Unit maxUnit = null;

		for (Unit defender : defUnits) {
			RelationType myRelation = worldView.getMyRelation(defender
					.getNation());
			if (!AttackHelper.canAttack(attackType, myRelation)) {
				continue;
			}
			int multiplier = UnitBase.getMultiplier(attackerBase,
					defender.getUnitBase());
			if (multiplier > maxMultiplier) {
				maxMultiplier = multiplier;
				maxUnit = defender;
			}
		}
		return maxUnit;
	}

	private static void removeIntercepted(Collection<Unit> units) {
		List<Unit> unitsToRemove = new ArrayList<Unit>();
		for (Unit unit : units) {
			if (unit.isIntercepted()) {
				unitsToRemove.add(unit);
			}
		}
		units.removeAll(unitsToRemove);
	}
}
