package com.kenstevens.stratinit.server.bot.action;

import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.bot.BotWeights;
import com.kenstevens.stratinit.server.bot.BotWorldState;
import com.kenstevens.stratinit.server.svc.MoveService;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorCoords;

import java.util.Collections;

public class DefendCityAction implements BotAction {
    private final Unit unit;
    private final City city;
    private final Nation nation;
    private final MoveService moveService;

    public DefendCityAction(Unit unit, City city, Nation nation, MoveService moveService) {
        this.unit = unit;
        this.city = city;
        this.nation = nation;
        this.moveService = moveService;
    }

    @Override
    public BotActionCategory getCategory() {
        return BotActionCategory.DEFENSE;
    }

    @Override
    public double computeUtility(BotWorldState state, BotWeights weights) {
        return weights.defenseBaseWeight * weights.cityDefenseDesire * weights.undefendedPenalty;
    }

    @Override
    public boolean execute() {
        SIUnit siUnit = new SIUnit(unit);
        SectorCoords targetCoords = city.getCoords();
        Result<MoveCost> result = moveService.move(nation, Collections.singletonList(siUnit), targetCoords);
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
        return "Defend city at " + city.getCoords() + " with " + unit.toMyString();
    }
}
