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

public class SwimEngineerToIslandAction implements BotAction {
    private final Unit engineer;
    private final SectorCoords target;
    private final int distance;
    private final int islandScore;
    private final Nation nation;
    private final MoveService moveService;

    public SwimEngineerToIslandAction(Unit engineer, SectorCoords target, int distance,
                                       int islandScore, Nation nation, MoveService moveService) {
        this.engineer = engineer;
        this.target = target;
        this.distance = distance;
        this.islandScore = islandScore;
        this.nation = nation;
        this.moveService = moveService;
    }

    @Override
    public BotActionCategory getCategory() {
        return BotActionCategory.EXPANSION;
    }

    @Override
    public double computeUtility(BotWorldState state, BotWeights weights) {
        double utility = weights.expansionBaseWeight * weights.engineerSwimDesire;
        // Scale by island score
        utility *= (1.0 + (islandScore - 1) * 0.2);
        // Penalize distant targets
        utility -= distance * weights.distancePenalty;
        // Early game bonus
        if (state.getGameTimePercent() < 0.3) {
            utility *= (1.0 + weights.earlyExpansionBonus);
        }
        // Extra boost if island has no bot presence
        if (state.countMyCitiesOnIsland(getTargetIslandId(state)) == 0) {
            utility *= (1.0 + weights.coastalCityDesire);
        }
        return Math.max(0, utility);
    }

    private int getTargetIslandId(BotWorldState state) {
        var sector = state.getWorld().getSectorOrNull(target);
        return sector != null ? sector.getIsland() : -1;
    }

    @Override
    public boolean execute() {
        SIUnit siUnit = new SIUnit(engineer);
        Result<MoveCost> result = moveService.move(nation, Collections.singletonList(siUnit), target);
        return result.isSuccess();
    }

    @Override
    public int getCommandPointCost() {
        return Constants.COMMAND_COST;
    }

    @Override
    public Integer getInvolvedUnitId() {
        return engineer.getId();
    }

    @Override
    public String describe() {
        return "Swim engineer from " + engineer.getCoords() + " to island at " + target
                + " (score=" + islandScore + ", dist=" + distance + ")";
    }
}
