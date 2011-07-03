package com.kenstevens.stratinit.util;

import java.util.Random;

import com.kenstevens.stratinit.model.AttackType;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.type.RelationType;

public final class AttackHelper {
	private static final Random RANDOM = new Random();

	private AttackHelper() {}

	public static int randomDamage(int maxDamage) {
		if (maxDamage == 1) {
			return 1;
		}
		if (maxDamage == 2) {
			return randomFlakDamage(maxDamage);
		}
		int attack = maxDamage;
		int half = attack / 2;
		int remainder = attack - half;
		return half + RANDOM.nextInt(remainder) + 1;
	}

	public static int randomFlakDamage(int maxDamage) {
		return RANDOM.nextInt(maxDamage) + 1;
	}

	public static int costToMove(Unit unit, boolean inSupply) {
		int cost = 1;
		if (!inSupply && !unit.isAir()) {
			cost = 2;
		}
		return cost;
	}

	public static boolean canAttack(AttackType attackType,
			RelationType myRelation) {
		boolean retval = true;
		if (attackType == AttackType.INITIAL_ATTACK && myRelation != RelationType.WAR) {
			retval = false;
		} else if (attackType == AttackType.INTERCEPTION || attackType == AttackType.INTERDICTION) {
			if (myRelation == null || myRelation.compareTo(RelationType.FRIENDLY) >= 0) {
				retval = false;
			}
		}
		return retval;
	}
}
