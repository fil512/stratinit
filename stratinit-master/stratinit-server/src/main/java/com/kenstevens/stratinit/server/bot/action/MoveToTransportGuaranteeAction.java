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

/**
 * Guarantee action: move a land unit toward a coastal tile near an empty transport.
 * Uses the engineer guarantee multiplier to ensure it beats normal expansion,
 * similar to the engineer and research guarantee patterns.
 */
public class MoveToTransportGuaranteeAction implements BotAction {
    private final Unit unit;
    private final SectorCoords target;
    private final int distance;
    private final Nation nation;
    private final MoveService moveService;

    public MoveToTransportGuaranteeAction(Unit unit, SectorCoords target, int distance,
                                           Nation nation, MoveService moveService) {
        this.unit = unit;
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
        // Guarantee-level utility: expansionBaseWeight * engineerGuaranteeMultiplier
        // This ensures it beats normal expansion actions
        double utility = weights.expansionBaseWeight * weights.engineerGuaranteeMultiplier;
        // Distance penalty: multiplicative to avoid zeroing out (divided by 3 to keep it high even for far units)
        utility /= (1.0 + distance * weights.distancePenalty / 3.0);
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
    public String getActionType() {
        return "MoveToCoastForPickupAction";
    }

    @Override
    public String describe() {
        return "Move " + unit.toMyString() + " from " + unit.getCoords() + " toward transport at coast " + target + " (guarantee)";
    }
}
