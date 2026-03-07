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

public class MoveBattleshipToTargetAction implements BotAction {
    private final Unit battleship;
    private final SectorCoords cityCoords;
    private final int distance;
    private final Nation nation;
    private final MoveService moveService;

    public MoveBattleshipToTargetAction(Unit battleship, SectorCoords cityCoords, int distance,
                                         Nation nation, MoveService moveService) {
        this.battleship = battleship;
        this.cityCoords = cityCoords;
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
        double utility = weights.militaryBaseWeight * weights.bombardCityDesire * 0.5;
        // Distance penalty
        utility /= (1.0 + distance * weights.distancePenalty);
        return utility;
    }

    @Override
    public boolean execute() {
        SIUnit siUnit = new SIUnit(battleship);
        // Target the city — pathfinder routes to adjacent water and attacks if in range
        Result<MoveCost> result = moveService.move(nation, Collections.singletonList(siUnit), cityCoords);
        return result.isSuccess();
    }

    @Override
    public int getCommandPointCost() {
        return Constants.COMMAND_COST;
    }

    @Override
    public Integer getInvolvedUnitId() {
        return battleship.getId();
    }

    @Override
    public String describe() {
        return "Move battleship " + battleship.toMyString() + " toward enemy coastal city at " + cityCoords;
    }
}
