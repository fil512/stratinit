package com.kenstevens.stratinit.server.bot;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.server.bot.action.BotAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BotExecutor {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BotWorldStateBuilder worldStateBuilder;
    @Autowired
    private BotActionGenerator actionGenerator;
    @Autowired
    private BotWeights weights;

    public void executeTurn(Nation nation) {
        executeTurn(nation, weights);
    }

    public void executeTurn(Nation nation, BotWeights overrideWeights) {
        executeTurn(nation, overrideWeights, System.currentTimeMillis());
    }

    public void executeTurn(Nation nation, BotWeights overrideWeights, long simulatedTimeMillis) {
        BotWorldState state = worldStateBuilder.build(nation, simulatedTimeMillis);

        if (!state.getGame().hasStarted() || state.getGame().hasEnded()) {
            return;
        }

        List<BotAction> candidates = actionGenerator.generateActions(state, overrideWeights);

        // Score all actions
        List<ScoredAction> scored = new ArrayList<>();
        for (BotAction action : candidates) {
            double utility = action.computeUtility(state, overrideWeights);
            if (utility > 0) {
                scored.add(new ScoredAction(action, utility));
            }
        }

        // Sort by utility descending
        scored.sort(Comparator.comparingDouble(ScoredAction::utility).reversed());

        // Greedily execute top actions until CP exhausted
        Set<Integer> usedUnitIds = new HashSet<>();
        int actionsExecuted = 0;

        for (ScoredAction sa : scored) {
            BotAction action = sa.action();
            int cost = action.getCommandPointCost();

            if (cost > 0 && !state.hasEnoughCP(cost)) {
                continue;
            }

            Integer unitId = action.getInvolvedUnitId();
            if (unitId != null && usedUnitIds.contains(unitId)) {
                continue;
            }

            try {
                boolean success = action.execute();
                if (success) {
                    actionsExecuted++;
                    if (unitId != null) {
                        usedUnitIds.add(unitId);
                    }
                    if (cost > 0) {
                        nation.decreaseCommandPoints(cost);
                    }
                    logger.debug("Bot {} executed: {}", nation.getName(), action.describe());
                }
            } catch (Exception e) {
                logger.warn("Bot {} action failed: {} - {}", nation.getName(), action.describe(), e.getMessage());
            }
        }

        if (actionsExecuted > 0) {
            logger.info("Bot {} executed {} actions in game #{}",
                    nation.getName(), actionsExecuted, nation.getGameId());
        }
    }

    private record ScoredAction(BotAction action, double utility) {}
}
