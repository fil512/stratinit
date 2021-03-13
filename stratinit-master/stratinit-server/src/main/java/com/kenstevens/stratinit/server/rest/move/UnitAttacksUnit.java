package com.kenstevens.stratinit.server.rest.move;

import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.client.util.AttackHelper;
import com.kenstevens.stratinit.dto.SIBattleLog;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.LogDaoService;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.server.svc.FogService;
import com.kenstevens.stratinit.type.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Scope("prototype")
@Component
public class UnitAttacksUnit {
    private final AttackType attackType;
    private final Unit attacker;
    private final Unit defender;
    private final WorldSector targetSector;
    private final MoveSeen moveSeen;
    private final Nation actor;
    private final WorldView worldView;
    @Autowired
    private LogDaoService logDaoService;
    @Autowired
    private SectorDaoService sectorDaoService;
    @Autowired
    private UnitDaoService unitDaoService;
    @Autowired
    private UnitCommandFactory unitCommandFactory;
    @Autowired
    protected FogService fogService;

    public UnitAttacksUnit(WorldView worldView, Nation actor,
                           AttackType attackType, Unit attacker, Unit defender,
                           WorldSector targetSector, MoveSeen moveSeen) {
        this.worldView = worldView;
        this.actor = actor;
        this.attackType = attackType;
        this.attacker = attacker;
        this.defender = defender;
        this.targetSector = targetSector;
        this.moveSeen = moveSeen;
    }

    protected Result<None> attack(AttackReadiness attackReadiness,
                                  UnitAttackedBattleLog previousUnitAttackedattleLog, int flakDamage) {

        decreaseAmmo(attackReadiness);
        UnitAttackedBattleLog unitAttackedBattleLog = previousUnitAttackedattleLog;
        if (attackType != AttackType.COUNTER_ATTACK) {
            attacker.decreaseMobility(attackReadiness.costToAttack());
            if (unitAttackedBattleLog != null) {
                throw new IllegalStateException(
                        "A unitAttackedBattleLog should not be in progress");
            }
            unitAttackedBattleLog = new UnitAttackedBattleLog(attackType,
                    attacker, defender, targetSector.getCoords(), flakDamage);
        }
        damage(unitAttackedBattleLog);
        counterAttackIfDefenderAlive(unitAttackedBattleLog);
        seeAttackingSub();
        if (attackType == AttackType.INTERCEPTION) {
            defender.setIntercepted(true);
        }
        logDaoService.save(unitAttackedBattleLog);
        // TODO REF need this?
        // unitDaoService.merge(enemyUnit);
        return new Result<None>(new SIBattleLog(actor, unitAttackedBattleLog));
    }

    private void decreaseAmmo(AttackReadiness attackReadiness) {
        attacker.decreaseAmmo();
        if (attackReadiness.inSupply()) {
            attackReadiness.resupply();
        }
    }

    private void seeAttackingSub() {
        if (attackType != AttackType.COUNTER_ATTACK && attacker.isSubmarine()
                && attacker.isAlive()) {
            fogService.unitSeen(worldView, attacker, moveSeen, true);
        }
    }

    private void counterAttackIfDefenderAlive(
            UnitAttackedBattleLog unitAttackedBattleLog) {
        if (!defender.isAlive()) {
            defenderDied(unitAttackedBattleLog);
        } else {
            if (canCounterAttack()) {
                counterAttack(attacker, defender, unitAttackedBattleLog);
            }
        }
    }

    private boolean canCounterAttack() {
        boolean retval = true;
        if (attackType == AttackType.COUNTER_ATTACK) {
            retval = false;
        } else if (attackType == AttackType.INTERCEPTION && defender.isLand()) {
            retval = false;
        } else if (attackType == AttackType.INTERDICTION && defender.isNavy()) {
            retval = false;
        }
        return retval;
    }

    private void defenderDied(UnitAttackedBattleLog unitAttackedBattleLog) {
        if (attackType == AttackType.COUNTER_ATTACK) {
            unitAttackedBattleLog.setAttackerDied(true);
        } else {
            unitAttackedBattleLog.setDefenderDied(true);
        }
        killPassengers(unitAttackedBattleLog, defender);
    }

    private void damage(UnitAttackedBattleLog unitAttackedBattleLog) {
        int damage = damage(attacker, defender);
        if (attackType == AttackType.COUNTER_ATTACK) {
            unitAttackedBattleLog.setReturnDamage(damage);
        } else {
            unitAttackedBattleLog.setDamage(damage);
        }
    }

    private void counterAttack(Unit unit, Unit enemyUnit,
                               UnitAttackedBattleLog unitAttackedBattleLog) {
        WorldView enemyWorldView = sectorDaoService
                .getSupplyWorldView(enemyUnit);
        WorldSector attackerSector = enemyWorldView.getWorldSector(unit);
        UnitAttacksUnit unitsAttackUnit = unitCommandFactory
                .getUnitAttacksUnit(worldView, actor,
                        AttackType.COUNTER_ATTACK, enemyUnit, unit,
                        attackerSector, moveSeen);
        AttackReadiness attackReadiness = new AttackReadiness(
                AttackType.COUNTER_ATTACK, enemyUnit, attackerSector,
                enemyWorldView);
        Result<None> result = attackReadiness.canAttack(unitAttackedBattleLog);
        if (!result.isSuccess()) {
            return;
        }
        unitsAttackUnit.attack(attackReadiness, unitAttackedBattleLog,
                BattleLog.NO_DAMAGE);
    }

    private void killPassengers(UnitAttackedBattleLog unitAttackedBattleLog,
                                Unit holder) {
        if (!holder.carriesUnits()) {
            return;
        }

        if (holder.isNavy() && !targetSector.isWater()) {
            return;
        }

        if (holder.isAir()
                && sectorDaoService.getSectorView(holder).canRefuel(holder)) {
            return;
        }

        List<Unit> passengers = unitDaoService.getPassengers(holder,
                targetSector);
        for (Unit passenger : passengers) {
            unitDaoService.killUnit(passenger);
            passengerBattleLog(unitAttackedBattleLog, passenger);
        }
        int count = passengers.size();
        if (count > 0) {
            if (attackType == AttackType.COUNTER_ATTACK) {
                unitAttackedBattleLog.setAttackerCollateralUnitsSunk(count);
            } else {
                unitAttackedBattleLog.setDefenderCollateralUnitsSunk(count);
            }
        }
    }

    private void passengerBattleLog(UnitAttackedBattleLog parentLog,
                                    Unit passenger) {
        UnitAttackedBattleLog childLog = new UnitAttackedBattleLog(
                parentLog.getAttackType(), parentLog.getAttackerUnit(),
                passenger, passenger.getCoords());
        childLog.setDefenderDied(true);
        logDaoService.save(childLog);
    }

    private int damage(Unit attacker, Unit defender) {
        int attack = AttackValueHelper.getInitialAttackValue(attackType, attacker, defender);
        int damage = AttackHelper.randomDamage(attack);
        damage = multiplyDamage(attacker, defender, damage);
        damage = reduceDamageForFortifiedUnits(defender, damage);
        unitDaoService.damage(defender, damage);
        return damage;
    }

    private int reduceDamageForFortifiedUnits(Unit defender, int initialDamage) {
        int damage = initialDamage;
        if (targetSector != null && targetSector.isFort() && defender.isLand()) {
            damage /= 2;
            if (damage == 0) {
                damage = 1;
            }
        }
        return damage;
    }

    private int multiplyDamage(Unit attacker, Unit defender, int initialDamage) {
        int damage = initialDamage;
        damage *= UnitBase.getMultiplier(attacker.getUnitBase(),
                defender.getUnitBase());
        if (defender.isNavy() && targetSector.isPlayerCity()) {
            damage *= Constants.SHIP_IN_CITY_MULTIPLIER;
        } else if (defender.isAir() && targetSector.isPlayerCity()) {
            damage *= Constants.AIR_IN_CITY_MULTIPLIER;
        }
        return damage;
    }


}
