package com.kenstevens.stratinit.server.bot.action;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.model.MoveCost;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.bot.BotWeights;
import com.kenstevens.stratinit.server.bot.BotWorldState;
import com.kenstevens.stratinit.server.svc.MoveService;
import com.kenstevens.stratinit.type.Constants;

import java.util.Collections;

public class LaunchICBMAction implements BotAction {
    private final Unit icbm;
    private final City targetCity;
    private final Nation nation;
    private final MoveService moveService;

    public LaunchICBMAction(Unit icbm, City targetCity, Nation nation, MoveService moveService) {
        this.icbm = icbm;
        this.targetCity = targetCity;
        this.nation = nation;
        this.moveService = moveService;
    }

    @Override
    public BotActionCategory getCategory() {
        return BotActionCategory.MILITARY;
    }

    @Override
    public double computeUtility(BotWorldState state, BotWeights weights) {
        double utility = weights.militaryBaseWeight * weights.icbmLaunchDesire;

        utility *= state.getGameTimePercent();

        // Higher utility for larger blast radius ICBMs
        int blastRadius = icbm.getUnitBase().getBlastRadius();
        utility *= (1.0 + blastRadius * 0.2);

        return utility;
    }

    @Override
    public boolean execute() {
        SIUnit siUnit = new SIUnit(icbm);
        Result<MoveCost> result = moveService.move(nation, Collections.singletonList(siUnit), targetCity.getCoords());
        return result.isSuccess();
    }

    @Override
    public int getCommandPointCost() {
        return Constants.COMMAND_COST_LAUNCH_ICBM;
    }

    @Override
    public Integer getInvolvedUnitId() {
        return icbm.getId();
    }

    @Override
    public String describe() {
        return "Launch " + icbm.toMyString() + " at city " + targetCity.getCoords();
    }
}
