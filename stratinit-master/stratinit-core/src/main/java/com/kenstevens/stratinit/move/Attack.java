package com.kenstevens.stratinit.move;

import com.kenstevens.stratinit.client.model.AttackType;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.model.WorldSector;
import com.kenstevens.stratinit.client.util.AttackHelper;
import com.kenstevens.stratinit.client.util.UnitHelper;
import com.kenstevens.stratinit.type.CoordMeasure;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorType;

public class Attack {
	private final WorldSector worldSector;

	public Attack(WorldSector worldSector) {
		this.worldSector = worldSector;
	}

	public boolean canAttack(CoordMeasure coordMeasure, AttackType attackType,
			Unit unit) {
		if (!UnitHelper.canAttack(unit, attackType)) {
			return false;
		}
		SectorType type = worldSector.getType();
		if (attackType == AttackType.COUNTER_ATTACK) {
			return canCounterAttack(unit, type);
		}

		if (!canAttack(unit, attackType)) {
			return false;
		}

		if (type == SectorType.LAND) {
			if (unit.isNavy()) {
				return unit.isNavyCanAttackLand();
			}
		} else if (type == SectorType.WATER) {
			if (unit.isLand()) {
				return unit.isLandCanAttackShips();
			} else if (unit.isBomber() && unit.getAttack() == 0) {
				return false;
			}
		}

		return canReachToAttack(coordMeasure, unit);
	}

	private boolean canAttack(Unit unit, AttackType attackType) {
		if (worldSector.isNeutralCity()) {
			return unit.isLand();
		}

		if (worldSector.isWasteland()) {
			return false;
		}

		RelationType myRelation = worldSector.getMyRelation();
		if (myRelation == null
				|| !AttackHelper.canAttack(attackType, myRelation)) {
			return false;
		}

		if (worldSector.isEmptyCity()) {
			return unit.isLand();
		}
		return true;
	}

	private boolean canReachToAttack(CoordMeasure coordMeasure, Unit unit) {
		int unitDistance = worldSector.getCoords().distanceTo(coordMeasure,
				unit.getCoords());
		// TODO REF this is probably redundant with another optimization. I
		// added it for interception.
		if (unitDistance > unit.getMobility()) {
			return false;
		}
		return !unit.requiresFuel() || unitDistance <= unit.getFuel() + 1;
	}

	private boolean canCounterAttack(Unit unit, SectorType type) {
		if (type == SectorType.LAND && unit.isNavy()) {
			return unit.isNavyCanAttackLand();
		}
		return true;
	}

	public boolean isAttackable(AttackType attackType) {
		return AttackHelper.canAttack(attackType, worldSector.getMyRelation())
				|| worldSector.isNeutralCity();
	}

	public boolean isAttackable() {
		return isAttackable(AttackType.INITIAL_ATTACK);
	}

	public boolean isEnemyCity() {
		return worldSector.isPlayerCity() && isAttackable();
	}
}
