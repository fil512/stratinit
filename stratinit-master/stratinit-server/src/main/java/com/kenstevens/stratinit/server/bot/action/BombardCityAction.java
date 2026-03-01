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

public class BombardCityAction implements BotAction {
    private final Unit battleship;
    private final SectorCoords targetCoords;
    private final int distance;
    private final Nation nation;
    private final MoveService moveService;

    public BombardCityAction(Unit battleship, SectorCoords targetCoords, int distance, Nation nation,
                             MoveService moveService) {
        this.battleship = battleship;
        this.targetCoords = targetCoords;
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
        double utility = weights.militaryBaseWeight * weights.bombardCityDesire;

        // Distance penalty
        utility /= (1.0 + distance * weights.distancePenalty);

        return utility;
    }

    @Override
    public boolean execute() {
        SIUnit siUnit = new SIUnit(battleship);
        Result<MoveCost> result = moveService.move(nation, Collections.singletonList(siUnit), targetCoords);
        return result.isSuccess();
    }

    @Override
    public int getCommandPointCost() {
        return Constants.COMMAND_COST_ATTACK;
    }

    @Override
    public Integer getInvolvedUnitId() {
        return battleship.getId();
    }

    @Override
    public String describe() {
        return "Bombard city at " + targetCoords + " with " + battleship.toMyString();
    }
}
