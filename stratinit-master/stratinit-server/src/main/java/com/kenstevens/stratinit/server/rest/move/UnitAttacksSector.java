package com.kenstevens.stratinit.server.rest.move;

import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.client.util.AttackHelper;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.dto.SIBattleLog;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.CityDaoService;
import com.kenstevens.stratinit.server.daoservice.LogDaoService;
import com.kenstevens.stratinit.server.daoservice.UnitDaoService;
import com.kenstevens.stratinit.type.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Scope("prototype")
@Component
public class UnitAttacksSector {
    private final AttackType attackType;
    private final Unit attackingUnit;
    private final WorldSector targetSector;
    private final WorldView worldView;
    private final MoveSeen moveSeen;
    private final Nation actor;
    @Autowired
    private UnitDaoService unitDaoService;
    @Autowired
    private UnitDao unitDao;
    @Autowired
    private CityDaoService cityDaoService;
    @Autowired
    private LogDaoService logDaoService;
    @Autowired
    private UnitCommandFactory unitCommandFactory;

    public UnitAttacksSector(Nation actor, AttackType attackType, Unit attackingUnit, WorldSector targetSector, WorldView worldView, MoveSeen moveSeen) {
        this.actor = actor;
        this.attackType = attackType;
        this.attackingUnit = attackingUnit;
        this.targetSector = targetSector;
        this.worldView = worldView;
        this.moveSeen = moveSeen;
    }

    public Result<None> attack() {
        AttackReadiness attackReadiness = new AttackReadiness(attackType, attackingUnit, targetSector, worldView);
        Result<None> result = attackReadiness.canAttack(null);
        if (!result.isSuccess()) {
            return result;
        }
        int flakDamage = BattleLog.NO_DAMAGE;
        if (targetSector.getCityDefense(attackingUnit) > 0) {
            flakDamage = AttackHelper.randomFlakDamage(targetSector.getCityDefense(attackingUnit));
            unitDaoService.damage(attackingUnit, flakDamage);
            if (!attackingUnit.isAlive()) {
                FlakBattleLog flakBattleLog = new FlakBattleLog(AttackType.FLAK,
                        attackingUnit, targetSector.getNation(), targetSector.getCoords(), flakDamage);
                logDaoService.save(flakBattleLog);
                return new Result<None>(new SIBattleLog(actor, flakBattleLog), false);
            }
        }
        Collection<Unit> defUnits = new ArrayList<>(unitDao.getUnits(targetSector));
        if (!targetSector.isWater() && attackingUnit.isBomber()) {
            return bombTarget(defUnits, attackReadiness, flakDamage);
        }
        Unit enemyUnit = AttackingUnitFinderHelper.findUnitToAttack(attackType, attackingUnit, defUnits, targetSector, worldView);
        if (enemyUnit != null) {
            return attack(enemyUnit, attackReadiness, flakDamage);
        } else if (targetSector.isEmptyCity()) {
            return takeCity(attackReadiness);
        } else {
            return new Result<None>("Nothing to attack.", false);
        }
    }

    private Result<None> bombTarget(Collection<Unit> units, AttackReadiness attackReadiness, int flakDamage) {
        UnitBombsSector unitBombsSector = unitCommandFactory.getUnitBombsSector(actor, attackingUnit, targetSector, units, attackReadiness);
        return unitBombsSector.bomb(flakDamage);
    }

    private Result<None> attack(Unit enemyUnit, AttackReadiness attackReadiness, int flakDamage) {
        UnitAttacksUnit unitsAttackUnit = unitCommandFactory.getUnitAttacksUnit(worldView, actor, attackType, attackingUnit, enemyUnit, targetSector, moveSeen);
        return unitsAttackUnit.attack(attackReadiness, null, flakDamage);
    }


    private Result<None> takeCity(AttackReadiness attackReadiness) {
        Nation oldOwner = cityDaoService.captureCity(attackingUnit.getNation(),
                targetSector);
        unitDaoService.damage(attackingUnit, Constants.CITY_CAPTURE_DAMAGE);
        CityCapturedBattleLog battleLog = new CityCapturedBattleLog(attackType, attackingUnit,
                oldOwner, targetSector.getCoords());

        if (attackingUnit.isAlive()) {
            attackingUnit.setCoords(targetSector.getCoords());
            attackingUnit.decreaseMobility(attackReadiness.costToAttack());
            unitDaoService.merge(attackingUnit);
        } else {
            battleLog.setAttackerDied(true);
        }
        logDaoService.save(battleLog);
        return new Result<None>(new SIBattleLog(actor, battleLog));
    }
}
