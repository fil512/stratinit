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

import java.util.Collections;

public class AttackNavalAction implements BotAction {
    private final Unit attacker;
    private final Unit target;
    private final int distance;
    private final Nation nation;
    private final MoveService moveService;

    public AttackNavalAction(Unit attacker, Unit target, int distance, Nation nation, MoveService moveService) {
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
        double utility = weights.militaryBaseWeight * weights.navalCombatDesire;

        double hpRatio = (double) attacker.getHp() / Math.max(1, target.getHp());
        if (hpRatio > 1.0) {
            utility += (hpRatio - 1.0) * weights.hpAdvantageFactor;
        }

        if (state.getGameTimePercent() > 0.5) {
            utility *= (1.0 + weights.lateMilitaryBonus);
        }

        // Escort bonus: prioritize clearing threats near friendly transports
        if (state.hasNearbyTransport(target.getCoords(), 3)) {
            utility += weights.navalEscortBonus;
        }

        // Distance penalty: farther enemies are less attractive
        utility -= distance * weights.distancePenalty;

        return Math.max(0, utility);
    }

    @Override
    public boolean execute() {
        SIUnit siUnit = new SIUnit(attacker);
        Result<MoveCost> result = moveService.move(nation, Collections.singletonList(siUnit), target.getCoords());
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
        return "Naval attack " + target.toEnemyString() + " at " + target.getCoords() + " with " + attacker.toMyString();
    }
}
