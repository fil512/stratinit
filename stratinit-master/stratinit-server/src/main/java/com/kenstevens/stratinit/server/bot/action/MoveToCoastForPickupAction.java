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

public class MoveToCoastForPickupAction implements BotAction {
    private final Unit unit;
    private final SectorCoords target;
    private final int distance;
    private final boolean transportNearby;
    private final Nation nation;
    private final MoveService moveService;

    public MoveToCoastForPickupAction(Unit unit, SectorCoords target, int distance,
                                       boolean transportNearby, Nation nation, MoveService moveService) {
        this.unit = unit;
        this.target = target;
        this.distance = distance;
        this.transportNearby = transportNearby;
        this.nation = nation;
        this.moveService = moveService;
    }

    @Override
    public BotActionCategory getCategory() {
        return BotActionCategory.EXPANSION;
    }

    @Override
    public double computeUtility(BotWorldState state, BotWeights weights) {
        double utility = weights.expansionBaseWeight * weights.moveToCoastDesire;
        // Big boost if transport is within 6 hexes of coast target
        if (transportNearby) {
            utility *= (1.0 + weights.transportLoadDesire);
        }
        // Early game bonus
        if (state.getGameTimePercent() < 0.3) {
            utility *= (1.0 + weights.earlyExpansionBonus);
        }
        // Distance penalty: multiplicative to avoid zeroing out
        utility /= (1.0 + distance * weights.distancePenalty);
        return utility;
    }

    @Override
    public boolean execute() {
        SIUnit siUnit = new SIUnit(unit);
        Result<MoveCost> result = moveService.move(nation, Collections.singletonList(siUnit), target);
        return result.isSuccess();
    }

    @Override
    public int getCommandPointCost() {
        return Constants.COMMAND_COST;
    }

    @Override
    public Integer getInvolvedUnitId() {
        return unit.getId();
    }

    @Override
    public String describe() {
        return "Move " + unit.toMyString() + " from " + unit.getCoords() + " to coast at " + target + " for pickup";
    }
}
