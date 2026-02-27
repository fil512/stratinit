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
 * High-priority transport loading action (guarantee pattern).
 * Moves an empty transport to a water tile adjacent to a land unit for pickup.
 * Uses engineerGuaranteeMultiplier to ensure it beats normal expansion actions.
 */
public class LoadTransportGuaranteeAction implements BotAction {
    private final Unit transport;
    private final SectorCoords pickupCoords;
    private final Nation nation;
    private final MoveService moveService;

    public LoadTransportGuaranteeAction(Unit transport, SectorCoords pickupCoords, Nation nation, MoveService moveService) {
        this.transport = transport;
        this.pickupCoords = pickupCoords;
        this.nation = nation;
        this.moveService = moveService;
    }

    @Override
    public BotActionCategory getCategory() {
        return BotActionCategory.EXPANSION;
    }

    @Override
    public double computeUtility(BotWorldState state, BotWeights weights) {
        // Guarantee-level utility: must beat expansion actions
        double utility = weights.expansionBaseWeight * weights.transportLoadDesire
                * weights.engineerGuaranteeMultiplier;
        if (state.getGameTimePercent() < 0.3) {
            utility *= (1.0 + weights.earlyExpansionBonus);
        }
        return utility;
    }

    @Override
    public boolean execute() {
        SIUnit siUnit = new SIUnit(transport);
        Result<MoveCost> result = moveService.move(nation, Collections.singletonList(siUnit), pickupCoords);
        return result.isSuccess();
    }

    @Override
    public int getCommandPointCost() {
        return Constants.COMMAND_COST;
    }

    @Override
    public Integer getInvolvedUnitId() {
        return transport.getId();
    }

    @Override
    public String getActionType() {
        return "LoadTransportAction";
    }

    @Override
    public String describe() {
        return "Move transport " + transport.toMyString() + " to pick up units at " + pickupCoords + " (guarantee)";
    }
}
