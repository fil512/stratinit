package com.kenstevens.stratinit.server.bot.action;

import com.kenstevens.stratinit.server.bot.BotWorldState;
import com.kenstevens.stratinit.server.bot.BotWeights;

public interface BotAction {
    BotActionCategory getCategory();
    double computeUtility(BotWorldState state, BotWeights weights);
    boolean execute();
    int getCommandPointCost();
    String describe();

    default Integer getInvolvedUnitId() {
        return null;
    }

    default String getInvolvedCityKey() {
        return null;
    }

    default String getActionType() {
        return getClass().getSimpleName();
    }

    default void onSuccess(BotWorldState state) {
        // Override in actions that affect shared state (e.g., bombing marks cities)
    }
}
