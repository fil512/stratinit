package com.kenstevens.stratinit.client.server.rest.move;

import com.kenstevens.stratinit.client.model.AttackType;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.UnitType;

public final class AttackValueHelper {
    private AttackValueHelper() {
    }

    public static int getInitialAttackValue(AttackType attackType,
                                            Unit attacker, Unit defender) {
        int attack = attacker.getAttack();
        if (attackType == AttackType.COUNTER_ATTACK
                && isMinCounterAttack(attacker, defender)) {
            attack = Constants.MIN_ATTACK;
        }
        if (attacker.getType() == UnitType.SUBMARINE && defender.isAir()) {
            attack = Constants.MIN_ATTACK;
        } else if (attacker.getType() == UnitType.NAVAL_BOMBER
                && !defender.isNavy()) {
            attack = Constants.MIN_ATTACK;
        }
        return attack;
    }

    private static boolean isMinCounterAttack(Unit attacker, Unit defender) {
        if (attacker.isBomber()) {
            return true;
        }
        if (attacker.isNavy() && defender.isAir()) {
            return true;
        }
        return attacker.isNavy() && !attacker.isCanSeeSubs()
                && defender.isSubmarine();
    }

}
