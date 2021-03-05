package com.kenstevens.stratinit.balance;

import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.model.UnitBase;
import com.kenstevens.stratinit.type.UnitType;

public class AttackBalanceResult {
    final UnitType attackerType;
    final UnitType defenderType;
    int numAttacks = 0;
    int attHpRemaining;
    int defHpRemaining;
    private boolean attackerDied = false;

    public AttackBalanceResult(Unit att, Unit def) {
        this.attackerType = att.getType();
        this.defenderType = def.getType();
    }

    public void updateResult(Unit att, Unit def) {
        this.numAttacks++;
        this.attHpRemaining = att.getHp();
        this.defHpRemaining = def.getHp();
        if (attHpRemaining <= 0) {
            setAttackerDied(true);
        }
    }

    public boolean hasDamage() {
        return attHpRemaining < UnitBase.getUnitBase(attackerType).getHp() ||
                defHpRemaining < UnitBase.getUnitBase(defenderType).getHp();
    }

    public boolean attackerDied() {
        return attHpRemaining <= 0;
    }

    public int getNumAttacks() {
        return numAttacks;
    }

    public void setNumAttacks(int numAttacks) {
        this.numAttacks = numAttacks;
    }

    public int getAttHpRemaining() {
        return attHpRemaining;
    }

    public void setAttHpRemaining(int attHpRemaining) {
        this.attHpRemaining = attHpRemaining;
    }

    public int getDefHpRemaining() {
        return defHpRemaining;
    }

    public void setDefHpRemaining(int defHpRemaining) {
        this.defHpRemaining = defHpRemaining;
    }

    public UnitType getAttackerType() {
        return attackerType;
    }

    public UnitType getDefenderType() {
        return defenderType;
    }

    public boolean isAttackerDied() {
        return attackerDied;
    }

    public void setAttackerDied(boolean attackerDied) {
        this.attackerDied = attackerDied;
    }

}
