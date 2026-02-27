package com.kenstevens.stratinit.server.bot.training;

import java.util.*;

public class TrainingActionLog {
    private final Map<String, List<TurnSnapshot>> nationTurns = new LinkedHashMap<>();
    private final Map<String, Map<String, Integer>> nationMilestones = new LinkedHashMap<>();
    private final Map<String, TurnSnapshot> currentTurn = new HashMap<>();

    public void recordTurnStart(String nationName, int turnNumber, TurnStateMetrics metrics) {
        TurnSnapshot snapshot = new TurnSnapshot(turnNumber, metrics);
        currentTurn.put(nationName, snapshot);
        nationTurns.computeIfAbsent(nationName, k -> new ArrayList<>()).add(snapshot);
    }

    public void recordAction(String nationName, String actionType) {
        TurnSnapshot snapshot = currentTurn.get(nationName);
        if (snapshot != null) {
            snapshot.executedCounts.merge(actionType, 1, Integer::sum);
        }
    }

    public void recordSkip(String nationName, String reason) {
        TurnSnapshot snapshot = currentTurn.get(nationName);
        if (snapshot != null) {
            snapshot.skippedCounts.merge(reason, 1, Integer::sum);
        }
    }

    public void recordMilestone(String nationName, String milestoneName, int turnNumber) {
        nationMilestones.computeIfAbsent(nationName, k -> new LinkedHashMap<>())
                .putIfAbsent(milestoneName, turnNumber);
    }

    public Map<String, List<TurnSnapshot>> getNationTurns() {
        return nationTurns;
    }

    public Map<String, Map<String, Integer>> getNationMilestones() {
        return nationMilestones;
    }

    public record TurnStateMetrics(int cities, int units, int explored, double tech, boolean hasTransport) {}

    public static class TurnSnapshot {
        private final int turnNumber;
        private final TurnStateMetrics metrics;
        private final Map<String, Integer> executedCounts = new LinkedHashMap<>();
        private final Map<String, Integer> skippedCounts = new LinkedHashMap<>();

        public TurnSnapshot(int turnNumber, TurnStateMetrics metrics) {
            this.turnNumber = turnNumber;
            this.metrics = metrics;
        }

        public int getTurnNumber() { return turnNumber; }
        public TurnStateMetrics getMetrics() { return metrics; }
        public Map<String, Integer> getExecutedCounts() { return executedCounts; }
        public Map<String, Integer> getSkippedCounts() { return skippedCounts; }
    }
}
