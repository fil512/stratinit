package com.kenstevens.stratinit.server.bot.action;

import com.kenstevens.stratinit.client.model.Sector;
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
    private final boolean canBuild;

    public BuildCityWithEngineerAction(Unit engineer, CityService cityService, boolean canBuild) {
        this.engineer = engineer;
        this.cityService = cityService;
        this.canBuild = canBuild;
    }

    @Override
    public BotActionCategory getCategory() {
        return BotActionCategory.EXPANSION;
    }

    @Override
    public double computeUtility(BotWorldState state, BotWeights weights) {
        // Only build if engineer has enough mobility
        if (engineer.getMobility() < Constants.MOB_COST_TO_CREATE_CITY) {
            return 0;
        }
        // Check if the server will actually allow city placement here
        if (!canBuild) {
            return 0;
        }
        double utility = weights.expansionBaseWeight * weights.buildCityDesire;
        // Early game bonus
        if (state.getGameTimePercent() < 0.3) {
            utility *= (1.0 + weights.earlyExpansionBonus);
        }
        // Coastal city bonus: boost when engineer is on a coastal sector and bot has no port
        if (!state.hasCoastalCity()) {
            Sector sector = state.getWorld().getSectorOrNull(engineer.getCoords());
            if (sector != null && state.getWorld().isCoastal(sector)) {
                utility *= (1.0 + weights.coastalCityDesire);
            }
        }
        // New-island boost: building first city on a non-home island is very high priority
        Sector engineerSector = state.getWorld().getSectorOrNull(engineer.getCoords());
        if (engineerSector != null && !engineerSector.isWater()) {
            int islandId = engineerSector.getIsland();
            if (islandId != state.getHomeIslandId() && state.countMyCitiesOnIsland(islandId) == 0) {
                utility *= (1.0 + weights.coastalCityDesire);
            }
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
    public Integer getInvolvedUnitId() {
        return engineer.getId();
    }

    @Override
    public String describe() {
        return "Engineer at " + engineer.getCoords() + " builds city";
    }
}
