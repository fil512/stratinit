package com.kenstevens.stratinit.server.bot;

import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.server.bot.action.BotAction;
import com.kenstevens.stratinit.server.bot.training.TrainingActionLog;
import com.kenstevens.stratinit.type.BotPersonality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class BotExecutor {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    // Timing accumulators for profiling (reset per simulation via resetTimers)
    private final AtomicLong buildStateNs = new AtomicLong();
    private final AtomicLong generateNs = new AtomicLong();
    private final AtomicLong scoreNs = new AtomicLong();
    private final AtomicLong executeNs = new AtomicLong();

    @Autowired
    private BotWorldStateBuilder worldStateBuilder;
    @Autowired
    private BotActionGenerator actionGenerator;
    @Autowired
    private PhasedBotWeights phasedWeights;

    public void resetTimers() {
        buildStateNs.set(0);
        generateNs.set(0);
        scoreNs.set(0);
        executeNs.set(0);
    }

    public void logTimers() {
        logger.info("  BotExecutor breakdown:");
        logger.info("    buildState:  {} ms", buildStateNs.get() / 1_000_000);
        logger.info("    generate:    {} ms", generateNs.get() / 1_000_000);
        logger.info("    score+sort:  {} ms", scoreNs.get() / 1_000_000);
        logger.info("    execute:     {} ms", executeNs.get() / 1_000_000);
    }

    public void executeTurn(Nation nation) {
        BotPersonality personality = nation.getBotPersonality();
        if (personality != null) {
            executeTurn(nation, PhasedBotWeights.forPersonality(personality));
        } else {
            executeTurn(nation, phasedWeights);
        }
    }

    public void executeTurn(Nation nation, PhasedBotWeights overrideWeights) {
        executeTurn(nation, overrideWeights, System.currentTimeMillis());
    }

    public void executeTurn(Nation nation, PhasedBotWeights overrideWeights, long simulatedTimeMillis) {
        executeTurn(nation, overrideWeights, simulatedTimeMillis, null);
    }

    public void executeTurn(Nation nation, PhasedBotWeights overrideWeights, long simulatedTimeMillis, TrainingActionLog log) {
        PreparedTurn prepared = prepareTurn(nation, overrideWeights, simulatedTimeMillis);
        if (prepared == null) {
            return;
        }
        executeActions(prepared, log);
    }

    /**
     * Read-only phase: builds world state, generates candidate actions, scores and sorts them.
     * Thread-safe — can be called in parallel for different nations.
     * Returns null if the game hasn't started or has ended.
     */
    public PreparedTurn prepareTurn(Nation nation, PhasedBotWeights overrideWeights, long simulatedTimeMillis) {
        long t0 = System.nanoTime();
        BotWorldState state = worldStateBuilder.build(nation, simulatedTimeMillis);
        long t1 = System.nanoTime();
        buildStateNs.addAndGet(t1 - t0);

        if (!state.getGame().hasStarted() || state.getGame().hasEnded()) {
            return null;
        }

        // Resolve tech-phase weights
        BotWeights weights = overrideWeights.resolve(state.getTech());

        List<BotAction> candidates = actionGenerator.generateActions(state, weights);
        long t2 = System.nanoTime();
        generateNs.addAndGet(t2 - t1);

        // Score all actions
        List<ScoredAction> scored = new ArrayList<>();
        for (BotAction action : candidates) {
            double utility = action.computeUtility(state, weights);
            if (utility > 0) {
                scored.add(new ScoredAction(action, utility));
            }
        }

        // Sort by utility descending
        scored.sort(Comparator.comparingDouble(ScoredAction::utility).reversed());
        long t3 = System.nanoTime();
        scoreNs.addAndGet(t3 - t2);

        return new PreparedTurn(nation, nation.getName(), scored, state);
    }

    /**
     * Write phase: greedily executes top actions from a prepared turn until CP exhausted.
     * Must be called sequentially (not thread-safe due to game state mutations).
     */
    public void executeActions(PreparedTurn prepared, TrainingActionLog log) {
        long t3 = System.nanoTime();
        Nation nation = prepared.nation();
        String nationName = prepared.nationName();
        BotWorldState state = prepared.state();

        Set<Integer> usedUnitIds = new HashSet<>();
        Set<String> usedCityKeys = new HashSet<>();
        int actionsExecuted = 0;

        for (ScoredAction sa : prepared.scoredActions()) {
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
        long t4 = System.nanoTime();
        executeNs.addAndGet(t4 - t3);

        if (actionsExecuted > 0) {
            logger.info("Bot {} executed {} actions in game #{}",
                    nation.getName(), actionsExecuted, nation.getGameId());
        }
    }

    record ScoredAction(BotAction action, double utility) {}

    public record PreparedTurn(Nation nation, String nationName, List<ScoredAction> scoredActions, BotWorldState state) {}
}
