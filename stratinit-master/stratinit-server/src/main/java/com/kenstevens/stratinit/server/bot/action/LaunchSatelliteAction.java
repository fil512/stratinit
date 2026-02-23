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

public class LaunchSatelliteAction implements BotAction {
    private final Unit satellite;
    private final SectorCoords target;
    private final Nation nation;
    private final MoveService moveService;

    public LaunchSatelliteAction(Unit satellite, SectorCoords target, Nation nation, MoveService moveService) {
        this.satellite = satellite;
        this.target = target;
        this.nation = nation;
        this.moveService = moveService;
    }

    @Override
    public BotActionCategory getCategory() {
        return BotActionCategory.EXPANSION;
    }

    @Override
    public double computeUtility(BotWorldState state, BotWeights weights) {
        return weights.economyBaseWeight * weights.satelliteLaunchDesire;
    }

    @Override
    public boolean execute() {
        SIUnit siUnit = new SIUnit(satellite);
        Result<MoveCost> result = moveService.move(nation, Collections.singletonList(siUnit), target);
        return result.isSuccess();
    }

    @Override
    public int getCommandPointCost() {
        return Constants.COMMAND_COST_LAUNCH_SATELLITE;
    }

    @Override
    public Integer getInvolvedUnitId() {
        return satellite.getId();
    }

    @Override
    public String describe() {
        return "Launch satellite " + satellite.toMyString() + " toward " + target;
    }
}
