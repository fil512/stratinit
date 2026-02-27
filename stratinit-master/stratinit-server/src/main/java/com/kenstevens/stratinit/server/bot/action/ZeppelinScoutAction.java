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

public class ZeppelinScoutAction implements BotAction {
    private final Unit zeppelin;
    private final SectorCoords target;
    private final int distance;
    private final int revealCount;
    private final double waterRatio;
    private final boolean isReturnToCity;
    private final Nation nation;
    private final MoveService moveService;

    public ZeppelinScoutAction(Unit zeppelin, SectorCoords target, int distance,
                                int revealCount, double waterRatio, boolean isReturnToCity, Nation nation, MoveService moveService) {
        this.zeppelin = zeppelin;
        this.target = target;
        this.distance = distance;
        this.revealCount = revealCount;
        this.waterRatio = waterRatio;
        this.isReturnToCity = isReturnToCity;
        this.nation = nation;
        this.moveService = moveService;
    }

    @Override
    public BotActionCategory getCategory() {
        return BotActionCategory.EXPANSION;
    }

    @Override
    public double computeUtility(BotWorldState state, BotWeights weights) {
        double utility = weights.expansionBaseWeight * weights.zeppelinScoutDesire;
        // Scale by number of new squares that would be revealed
        utility *= Math.max(1, revealCount);
        // Boost for water-heavy directions (discover other islands)
        utility *= (1.0 + waterRatio * weights.zeppelinWaterBonus);
        // Penalize distant targets
        utility -= distance * weights.distancePenalty;
        // Early game scouting is more valuable
        if (state.getGameTimePercent() < 0.3) {
            utility *= (1.0 + weights.earlyExpansionBonus);
        }
        // Returning to city for resupply is less desirable but necessary
        if (isReturnToCity) {
            utility *= 0.5;
        }
        return Math.max(0, utility);
    }

    @Override
    public boolean execute() {
        SIUnit siUnit = new SIUnit(zeppelin);
        Result<MoveCost> result = moveService.move(nation, Collections.singletonList(siUnit), target);
        return result.isSuccess();
    }

    @Override
    public int getCommandPointCost() {
        return Constants.COMMAND_COST;
    }

    @Override
    public Integer getInvolvedUnitId() {
        return zeppelin.getId();
    }

    @Override
    public String describe() {
        if (isReturnToCity) {
            return "Zeppelin returning to city at " + target;
        }
        return "Zeppelin scouting toward " + target + " (reveals ~" + revealCount + " squares, water=" + String.format("%.0f%%", waterRatio * 100) + ")";
    }
}
