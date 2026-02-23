package com.kenstevens.stratinit.server.bot.action;

import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.bot.BotWeights;
import com.kenstevens.stratinit.server.bot.BotWorldState;
import com.kenstevens.stratinit.server.service.CityService;
import com.kenstevens.stratinit.type.Constants;

public class BuildCityWithEngineerAction implements BotAction {
    private final Unit engineer;
    private final CityService cityService;

    public BuildCityWithEngineerAction(Unit engineer, CityService cityService) {
        this.engineer = engineer;
        this.cityService = cityService;
    }

    @Override
    public BotActionCategory getCategory() {
        return BotActionCategory.EXPANSION;
    }

    @Override
    public double computeUtility(BotWorldState state, BotWeights weights) {
        double utility = weights.expansionBaseWeight * weights.buildCityDesire;
        // Early game bonus
        if (state.getGameTimePercent() < 0.3) {
            utility *= (1.0 + weights.earlyExpansionBonus);
        }
        // Only build if engineer has enough mobility
        if (engineer.getMobility() < Constants.MOB_COST_TO_CREATE_CITY) {
            return 0;
        }
        return utility;
    }

    @Override
    public boolean execute() {
        Result<None> result = cityService.establishCity(engineer);
        return result.isSuccess();
    }

    @Override
    public int getCommandPointCost() {
        return Constants.COMMAND_COST_BUILD_CITY;
    }

    @Override
    public String describe() {
        return "Engineer at " + engineer.getCoords() + " builds city";
    }
}
