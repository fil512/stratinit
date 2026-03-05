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
import com.kenstevens.stratinit.type.SectorCoords;

import java.util.Collections;

public class CaptureEnemyCityAction implements BotAction {
    private final Unit attacker;
    private final City targetCity;
    private final int distance;
    private final Nation nation;
    private final MoveService moveService;

    public CaptureEnemyCityAction(Unit attacker, City targetCity, int distance, Nation nation, MoveService moveService) {
        this.attacker = attacker;
        this.targetCity = targetCity;
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
        // Capturing enemy cities is very high value — cities are the primary victory condition
        double utility = weights.militaryBaseWeight * weights.attackWeakDesire * 2.0;

        // Bonus when air/naval has been softening the target
        if (state.countAirUnitsInRangeOf(targetCity.getCoords()) > 0) {
            utility += weights.airSupportBonus;
        }

        // Coordination bonus: multiple ground units converging
        int alliesInRange = state.countLandUnitsInRangeOf(targetCity.getCoords()) - 1;
        if (alliesInRange > 0) {
            utility += alliesInRange * weights.coordinationBonus;
        }

        // Distance penalty
        utility /= (1.0 + distance * weights.distancePenalty);

        return utility;
    }

    @Override
    public boolean execute() {
        SIUnit siUnit = new SIUnit(attacker);
        SectorCoords targetCoords = targetCity.getCoords();
        Result<MoveCost> result = moveService.move(nation, Collections.singletonList(siUnit), targetCoords);
        return result.isSuccess();
    }

    @Override
    public int getCommandPointCost() {
        return Constants.COMMAND_COST_ATTACK;
    }

    @Override
    public Integer getInvolvedUnitId() {
        return attacker.getId();
    }

    @Override
    public String describe() {
        return "Capture enemy city at " + targetCity.getCoords() + " with " + attacker.toMyString();
    }
}
