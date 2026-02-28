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
    private final int islandScore;
    private final boolean isEmpty;
    private final Nation nation;
    private final MoveService moveService;

    public MoveTransportToTargetAction(Unit transport, SectorCoords targetCoords, int distance,
                                        boolean hasCityTarget, int islandScore, Nation nation, MoveService moveService) {
        this(transport, targetCoords, distance, hasCityTarget, islandScore, false, nation, moveService);
    }

    public MoveTransportToTargetAction(Unit transport, SectorCoords targetCoords, int distance,
                                        boolean hasCityTarget, int islandScore, boolean isEmpty,
                                        Nation nation, MoveService moveService) {
        this.transport = transport;
        this.targetCoords = targetCoords;
        this.distance = distance;
        this.hasCityTarget = hasCityTarget;
        this.islandScore = islandScore;
        this.isEmpty = isEmpty;
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
        // Distance penalty: multiplicative to avoid zeroing out
        utility /= (1.0 + distance * weights.distancePenalty);
        utility *= (1.0 + (islandScore - 1) * 0.2);
        if (isEmpty) {
            utility *= 0.5;
        }
        return utility;
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
        return "Sail " + (isEmpty ? "empty " : "") + "transport " + transport.toMyString()
                + " toward landing zone " + targetCoords
                + (hasCityTarget ? " (city target)" : "")
                + " [island score=" + islandScore + "]";
    }
}
