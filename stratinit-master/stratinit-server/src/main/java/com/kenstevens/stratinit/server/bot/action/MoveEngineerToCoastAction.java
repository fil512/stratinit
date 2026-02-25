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

public class MoveEngineerToCoastAction implements BotAction {
    private final Unit engineer;
    private final SectorCoords target;
    private final int distance;
    private final Nation nation;
    private final MoveService moveService;

    public MoveEngineerToCoastAction(Unit engineer, SectorCoords target, int distance,
                                      Nation nation, MoveService moveService) {
        this.engineer = engineer;
        this.target = target;
        this.distance = distance;
        this.nation = nation;
        this.moveService = moveService;
    }

    @Override
    public BotActionCategory getCategory() {
        return BotActionCategory.EXPANSION;
    }

    @Override
    public double computeUtility(BotWorldState state, BotWeights weights) {
        double utility = weights.expansionBaseWeight * weights.coastalCityDesire;
        // Penalize distant targets
        utility -= distance * weights.distancePenalty;
        // Reduce desire if bot already has a coastal city
        if (state.hasCoastalCity()) {
            utility *= 0.3;
        }
        // Early game bonus
        if (state.getGameTimePercent() < 0.3) {
            utility *= (1.0 + weights.earlyExpansionBonus);
        }
        return Math.max(0, utility);
    }

    @Override
    public boolean execute() {
        SIUnit siUnit = new SIUnit(engineer);
        Result<MoveCost> result = moveService.move(nation, Collections.singletonList(siUnit), target);
        return result.isSuccess();
    }

    @Override
    public int getCommandPointCost() {
        return Constants.COMMAND_COST;
    }

    @Override
    public Integer getInvolvedUnitId() {
        return engineer.getId();
    }

    @Override
    public String describe() {
        return "Move engineer from " + engineer.getCoords() + " toward coast at " + target;
    }
}
