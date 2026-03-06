package com.kenstevens.stratinit.server.bot.action;

import com.kenstevens.stratinit.client.model.MoveCost;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.bot.BotWeights;
import com.kenstevens.stratinit.server.bot.BotWorldState;
import com.kenstevens.stratinit.server.svc.MoveService;
import com.kenstevens.stratinit.type.BotPersonality;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

import java.util.Collections;

public class MoveUnitToExpandAction implements BotAction {
    private final Unit unit;
    private final SectorCoords target;
    private final Nation nation;
    private final MoveService moveService;
    private final int distance;
    private final boolean isHomeIsland;
    private final boolean isForeignIsland;

    public MoveUnitToExpandAction(Unit unit, SectorCoords target, int distance,
                                  Nation nation, MoveService moveService) {
        this(unit, target, distance, nation, moveService, false, false);
    }

    public MoveUnitToExpandAction(Unit unit, SectorCoords target, int distance,
                                  Nation nation, MoveService moveService, boolean isHomeIsland) {
        this(unit, target, distance, nation, moveService, isHomeIsland, false);
    }

    public MoveUnitToExpandAction(Unit unit, SectorCoords target, int distance,
                                  Nation nation, MoveService moveService, boolean isHomeIsland, boolean isForeignIsland) {
        this.unit = unit;
        this.target = target;
        this.distance = distance;
        this.nation = nation;
        this.moveService = moveService;
        this.isHomeIsland = isHomeIsland;
        this.isForeignIsland = isForeignIsland;
    }

    @Override
    public BotActionCategory getCategory() {
        return BotActionCategory.EXPANSION;
    }

    @Override
    public double computeUtility(BotWorldState state, BotWeights weights) {
        double utility = weights.expansionBaseWeight;

        // Personality-specific expansion overrides
        BotPersonality personality = state.getNation().getBotPersonality();
        if (personality != null) {
            // TECH and TURTLE engineers need high expansion utility to reach build sites
            if (unit.getType() == UnitType.ENGINEER
                    && (personality == BotPersonality.TECH || personality == BotPersonality.TURTLE)) {
                utility = 3.0;
            }
            // RUSH infantry should explore home island aggressively to find neutral cities
            if (unit.getType() == UnitType.INFANTRY && personality == BotPersonality.RUSH && isHomeIsland) {
                utility = 2.0;
            }
        }

        // Distance penalty: multiplicative to avoid zeroing out
        utility /= (1.0 + distance * weights.distancePenalty);
        // Home island exploration bonus
        if (isHomeIsland) {
            utility += weights.homeIslandExplorationBonus;
        }
        // Foreign island exploration bonus — push units toward enemy cities
        if (isForeignIsland) {
            utility += weights.foreignIslandExplorationBonus;
        }
        return utility;
    }

    @Override
    public boolean execute() {
        SIUnit siUnit = new SIUnit(unit);
        Result<MoveCost> result = moveService.move(nation, Collections.singletonList(siUnit), target);
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
        return "Move " + unit.toMyString() + " from " + unit.getCoords() + " toward " + target + " (expand)";
    }
}
