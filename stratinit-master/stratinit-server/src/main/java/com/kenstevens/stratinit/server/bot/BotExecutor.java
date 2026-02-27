package com.kenstevens.stratinit.server.bot;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.server.bot.action.BotAction;
import com.kenstevens.stratinit.server.bot.training.TrainingActionLog;
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
        executeTurn(nation, overrideWeights, simulatedTimeMillis, null);
    }

    public void executeTurn(Nation nation, BotWeights overrideWeights, long simulatedTimeMillis, TrainingActionLog log) {
        BotWorldState state = worldStateBuilder.build(nation, simulatedTimeMillis);

        if (!state.getGame().hasStarted() || state.getGame().hasEnded()) {
            return;
        }

        String nationName = nation.getName();
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
        Set<String> usedCityKeys = new HashSet<>();
        int actionsExecuted = 0;

        for (ScoredAction sa : scored) {
            BotAction action = sa.action();
            int cost = action.getCommandPointCost();

            if (cost > 0 && !state.hasEnoughCP(cost)) {
                if (log != null) {
                    log.recordSkip(nationName, "no_cp");
                }
                continue;
            }

            Integer unitId = action.getInvolvedUnitId();
            if (unitId != null && usedUnitIds.contains(unitId)) {
                if (log != null) {
                    log.recordSkip(nationName, "unit_used");
                }
                continue;
            }

            String cityKey = action.getInvolvedCityKey();
            if (cityKey != null && usedCityKeys.contains(cityKey)) {
                if (log != null) {
                    log.recordSkip(nationName, "city_used");
                }
                continue;
            }

            try {
                boolean success = action.execute();
                if (success) {
                    actionsExecuted++;
                    if (log != null) {
                        log.recordAction(nationName, action.getActionType());
                    }
                    if (unitId != null) {
                        usedUnitIds.add(unitId);
                    }
                    if (cityKey != null) {
                        usedCityKeys.add(cityKey);
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
