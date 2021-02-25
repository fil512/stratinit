package com.kenstevens.stratinit.server.remote.move;

import com.kenstevens.stratinit.model.AttackType;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitAttackedBattleLog;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.move.Attack;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.supply.Supply;
import com.kenstevens.stratinit.util.AttackHelper;
import com.kenstevens.stratinit.util.UnitHelper;

public class AttackReadiness {
    private final AttackType attackType;
    private final Unit unit;
    private final WorldSector targetSector;
    private final WorldView worldView;
    private boolean inSupply;
    private int costToAttack;
    private boolean initialized = false;
    private Supply supply;

    public AttackReadiness(AttackType attackType, Unit unit,
                           WorldSector targetSector, WorldView worldView) {
        this.attackType = attackType;
        this.unit = unit;
        this.targetSector = targetSector;
        this.worldView = worldView;
    }

    public Result<None> canAttack(UnitAttackedBattleLog previousUnitAttackedBattleLog) {
        initialized = true;

        UnitAttackedBattleLog unitAttackedBattleLog = previousUnitAttackedBattleLog;
        if (unitAttackedBattleLog == null) {
            unitAttackedBattleLog = new UnitAttackedBattleLog(); // Will be
            // tossed.
        }
        Attack attack = new Attack(targetSector);
        if (!UnitHelper.canAttack(unit, attackType)
                || !attack.canAttack(worldView, attackType, unit)) {
            return new Result<None>(unit + " is not able to attack "
                    + targetSector.getCoords(), false);
        }
        if (landAtSea(unit) && !targetSector.isEmptyCity()) {
            unitAttackedBattleLog.setDefenderCannotAttack(true);
            return new Result<None>(unit + " is on a ship and may not attack.",
                    false);
        }
        supply = new Supply(worldView);
        inSupply = supply.inSupply(unit);
        costToAttack = AttackHelper.costToMove(unit, inSupply);
        if (attackType != AttackType.COUNTER_ATTACK
                && unit.getMobility() < costToAttack) {
            return new Result<None>("insufficient mobility (need "
                    + costToAttack + ")", false);
        }
//		TODO * do I need the !inSupply?
//		if (!inSupply && unit.getAmmo() < 1) {
        if (unit.getAmmo() < 1) {
            unitAttackedBattleLog.setDefenderOutOfAmmo(true);
            return new Result<None>("Out of ammo, not attacking.", false);
        }
        return Result.trueInstance();
    }

    public boolean inSupply() {
        if (!initialized) {
            throw new IllegalStateException(
                    "Must call canAttack() before inSupply()");
        }
        return inSupply;
    }

    public int costToAttack() {
        if (!initialized) {
            throw new IllegalStateException(
                    "Must call canAttack() before inSupply()");
        }
        return costToAttack;
    }

    private boolean landAtSea(Unit unit) {
        if (unit.isLand()) {
            WorldSector unitSector = worldView.getWorldSector(unit);
            return unitSector.isHoldsShipAtSea();
        }
        return false;
    }

    public void resupply() {
        if (supply == null) {
            supply = new Supply(worldView);
        }
        supply.resupply(unit);
    }
}
