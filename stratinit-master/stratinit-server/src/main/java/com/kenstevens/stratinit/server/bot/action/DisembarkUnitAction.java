package com.kenstevens.stratinit.server.bot.action;

import com.kenstevens.stratinit.client.model.MoveCost;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Sector;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.bot.BotWeights;
import com.kenstevens.stratinit.server.bot.BotWorldState;
import com.kenstevens.stratinit.server.svc.MoveService;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorCoords;

import java.util.Collections;

public class DisembarkUnitAction implements BotAction {
    private final Unit landUnit;
    private final SectorCoords targetCoords;
    private final boolean isCity;
    private final Nation nation;
    private final MoveService moveService;

    public DisembarkUnitAction(Unit landUnit, SectorCoords targetCoords, boolean isCity,
                               Nation nation, MoveService moveService) {
        this.landUnit = landUnit;
        this.targetCoords = targetCoords;
        this.isCity = isCity;
        this.nation = nation;
        this.moveService = moveService;
    }

    @Override
    public BotActionCategory getCategory() {
        return BotActionCategory.EXPANSION;
    }

    @Override
    public double computeUtility(BotWorldState state, BotWeights weights) {
        double utility = weights.expansionBaseWeight * weights.disembarkDesire;
        if (isCity) {
            utility *= (1.0 + weights.neutralCityCaptureDesire);
        }
        // New-island boost: disembarking onto an island with no owned cities
        Sector targetSector = state.getWorld().getSectorOrNull(targetCoords);
        if (targetSector != null && !targetSector.isWater()) {
            int islandId = targetSector.getIsland();
            if (state.countMyCitiesOnIsland(islandId) == 0) {
                utility *= (1.0 + weights.coastalCityDesire);
            }
        }
        return utility;
    }

    @Override
    public boolean execute() {
        SIUnit siUnit = new SIUnit(landUnit);
        Result<MoveCost> result = moveService.move(nation, Collections.singletonList(siUnit), targetCoords);
        return result.isSuccess();
    }

    @Override
    public int getCommandPointCost() {
        return Constants.COMMAND_COST;
    }

    @Override
    public Integer getInvolvedUnitId() {
        return landUnit.getId();
    }

    @Override
    public String describe() {
        return "Disembark " + landUnit.toMyString() + " onto " + targetCoords
                + (isCity ? " (city)" : "");
    }
}
