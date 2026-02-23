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
    private final Nation nation;
    private final MoveService moveService;

    public AttackEnemyAction(Unit attacker, Unit target, Nation nation, MoveService moveService) {
        this.attacker = attacker;
        this.target = target;
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

        return utility;
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
    public String describe() {
        return "Attack " + target.toEnemyString() + " at " + target.getCoords() + " with " + attacker.toMyString();
    }
}
