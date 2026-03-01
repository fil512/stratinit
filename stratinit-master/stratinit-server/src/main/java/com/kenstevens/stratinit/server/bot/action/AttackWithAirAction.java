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

public class AttackWithAirAction implements BotAction {
    private final Unit airUnit;
    private final SectorCoords targetCoords;
    private final int distance;
    private final Nation nation;
    private final MoveService moveService;
    private final boolean isBombingCity;

    public AttackWithAirAction(Unit airUnit, SectorCoords targetCoords, int distance, Nation nation,
                               MoveService moveService, boolean isBombingCity) {
        this.airUnit = airUnit;
        this.targetCoords = targetCoords;
        this.distance = distance;
        this.nation = nation;
        this.moveService = moveService;
        this.isBombingCity = isBombingCity;
    }

    @Override
    public BotActionCategory getCategory() {
        return BotActionCategory.MILITARY;
    }

    @Override
    public double computeUtility(BotWorldState state, BotWeights weights) {
        double utility = weights.militaryBaseWeight * weights.airStrikeDesire;

        if (isBombingCity) {
            utility *= 1.2;
        }

        // Air support bonus: prioritize strikes where ground forces can follow up
        if (state.hasLandThreatNear(targetCoords)) {
            utility += weights.airSupportBonus;
        }

        // Distance penalty: multiplicative to avoid zeroing out
        utility /= (1.0 + distance * weights.distancePenalty);

        return utility;
    }

    @Override
    public boolean execute() {
        SIUnit siUnit = new SIUnit(airUnit);
        Result<MoveCost> result = moveService.move(nation, Collections.singletonList(siUnit), targetCoords);
        return result.isSuccess();
    }

    @Override
    public int getCommandPointCost() {
        return Constants.COMMAND_COST_ATTACK;
    }

    @Override
    public Integer getInvolvedUnitId() {
        return airUnit.getId();
    }

    @Override
    public String describe() {
        String action = isBombingCity ? "Bomb city" : "Air strike";
        return action + " at " + targetCoords + " with " + airUnit.toMyString();
    }
}
