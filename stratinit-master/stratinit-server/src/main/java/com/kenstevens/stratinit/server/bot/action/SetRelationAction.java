package com.kenstevens.stratinit.server.bot.action;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Relation;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.bot.BotWeights;
import com.kenstevens.stratinit.server.bot.BotWorldState;
import com.kenstevens.stratinit.server.service.RelationService;
import com.kenstevens.stratinit.type.RelationType;

public class SetRelationAction implements BotAction {
    private final Nation myNation;
    private final Nation targetNation;
    private final RelationType newRelation;
    private final RelationService relationService;

    public SetRelationAction(Nation myNation, Nation targetNation, RelationType newRelation,
                             RelationService relationService) {
        this.myNation = myNation;
        this.targetNation = targetNation;
        this.newRelation = newRelation;
        this.relationService = relationService;
    }

    @Override
    public BotActionCategory getCategory() {
        return BotActionCategory.DIPLOMACY;
    }

    @Override
    public double computeUtility(BotWorldState state, BotWeights weights) {
        if (newRelation == RelationType.WAR) {
            // Proactive war declaration scored by military intent
            return weights.militaryBaseWeight * weights.attackWeakDesire;
        }
        return weights.diplomacyBaseWeight;
    }

    @Override
    public boolean execute() {
        Result<Relation> result = relationService.setRelation(myNation, targetNation, newRelation, false);
        return result.isSuccess();
    }

    @Override
    public int getCommandPointCost() {
        return 0;
    }

    @Override
    public String describe() {
        return "Set relation to " + targetNation.getName() + " to " + newRelation;
    }
}
