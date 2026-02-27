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

public class BoardTransportAction implements BotAction {
    private final Unit landUnit;
    private final SectorCoords transportCoords;
    private final Nation nation;
    private final MoveService moveService;

    public BoardTransportAction(Unit landUnit, SectorCoords transportCoords, Nation nation, MoveService moveService) {
        this.landUnit = landUnit;
        this.transportCoords = transportCoords;
        this.nation = nation;
        this.moveService = moveService;
    }

    @Override
    public BotActionCategory getCategory() {
        return BotActionCategory.EXPANSION;
    }

    @Override
    public double computeUtility(BotWorldState state, BotWeights weights) {
        double utility = weights.navalBaseWeight * weights.transportLoadDesire;
        if (state.getGameTimePercent() < 0.3) {
            utility *= (1.0 + weights.earlyExpansionBonus);
        }
        // Boarding is high priority — unit is already adjacent to the transport
        utility *= 2.0;
        return utility;
    }

    @Override
    public boolean execute() {
        SIUnit siUnit = new SIUnit(landUnit);
        Result<MoveCost> result = moveService.move(nation, Collections.singletonList(siUnit), transportCoords);
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
        return "Board transport at " + transportCoords + " with " + landUnit.toMyString();
    }
}
