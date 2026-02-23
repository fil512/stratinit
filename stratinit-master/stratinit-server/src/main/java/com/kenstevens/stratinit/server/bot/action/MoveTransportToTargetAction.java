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

public class MoveTransportToTargetAction implements BotAction {
    private final Unit transport;
    private final SectorCoords targetCoords;
    private final int distance;
    private final boolean hasCityTarget;
    private final Nation nation;
    private final MoveService moveService;

    public MoveTransportToTargetAction(Unit transport, SectorCoords targetCoords, int distance,
                                        boolean hasCityTarget, Nation nation, MoveService moveService) {
        this.transport = transport;
        this.targetCoords = targetCoords;
        this.distance = distance;
        this.hasCityTarget = hasCityTarget;
        this.nation = nation;
        this.moveService = moveService;
    }

    @Override
    public BotActionCategory getCategory() {
        return BotActionCategory.EXPANSION;
    }

    @Override
    public double computeUtility(BotWorldState state, BotWeights weights) {
        double utility = weights.navalBaseWeight * weights.transportDestinationDesire;
        if (hasCityTarget) {
            utility *= 1.3;
        }
        utility -= distance * weights.distancePenalty;
        return Math.max(0, utility);
    }

    @Override
    public boolean execute() {
        SIUnit siUnit = new SIUnit(transport);
        Result<MoveCost> result = moveService.move(nation, Collections.singletonList(siUnit), targetCoords);
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
    public String describe() {
        return "Sail transport " + transport.toMyString() + " toward landing zone " + targetCoords
                + (hasCityTarget ? " (city target)" : "");
    }
}
