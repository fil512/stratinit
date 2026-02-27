package com.kenstevens.stratinit.server.bot.action;

import com.kenstevens.stratinit.client.model.MoveCost;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.bot.BotWeights;
import com.kenstevens.stratinit.server.bot.BotWorldState;
import com.kenstevens.stratinit.server.svc.MoveService;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorCoords;

import java.util.Collections;

public class AttackEnemyAction implements BotAction {
    private final Unit attacker;
    private final Unit target;
    private final int distance;
    private final Nation nation;
    private final MoveService moveService;

    public AttackEnemyAction(Unit attacker, Unit target, int distance, Nation nation, MoveService moveService) {
        this.attacker = attacker;
        this.target = target;
        this.distance = distance;
        this.nation = nation;
        this.moveService = moveService;
    }

    @Override
    public BotActionCategory getCategory() {
        return BotActionCategory.MILITARY;
    }

    @Override
    public double computeUtility(BotWorldState state, BotWeights weights) {
        double utility = weights.militaryBaseWeight;

        // Prefer attacking weaker enemies
        double hpRatio = (double) attacker.getHp() / Math.max(1, target.getHp());
        if (hpRatio > 1.0) {
            utility *= weights.attackWeakDesire;
            utility += (hpRatio - 1.0) * weights.hpAdvantageFactor;
        }

        // Late game military bonus
        if (state.getGameTimePercent() > 0.5) {
            utility *= (1.0 + weights.lateMilitaryBonus);
        }

        // Coordination bonus: reward converging multiple units on the same target
        int alliesInRange = state.countLandUnitsInRangeOf(target.getCoords()) - 1; // exclude self
        if (alliesInRange >= weights.massAttackThreshold - 1) {
            utility += alliesInRange * weights.coordinationBonus;
        }

        // Partial synergy when air can also reach this target
        if (state.countAirUnitsInRangeOf(target.getCoords()) > 0) {
            utility += weights.airSupportBonus * 0.5;
        }

        // Distance penalty: farther enemies are less attractive
        utility -= distance * weights.distancePenalty;

        return Math.max(0, utility);
    }

    @Override
    public boolean execute() {
        SIUnit siUnit = new SIUnit(attacker);
        SectorCoords targetCoords = target.getCoords();
        Result<MoveCost> result = moveService.move(nation, Collections.singletonList(siUnit), targetCoords);
        return result.isSuccess();
    }

    @Override
    public int getCommandPointCost() {
        return Constants.COMMAND_COST_ATTACK;
    }

    @Override
    public Integer getInvolvedUnitId() {
        return attacker.getId();
    }

    @Override
    public String describe() {
        return "Attack " + target.toEnemyString() + " at " + target.getCoords() + " with " + attacker.toMyString();
    }
}
