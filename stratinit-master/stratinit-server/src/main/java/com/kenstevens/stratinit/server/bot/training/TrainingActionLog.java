package com.kenstevens.stratinit.server.bot.training;

import java.util.*;

public class TrainingActionLog {
    private final Map<String, List<TurnSnapshot>> nationTurns = new LinkedHashMap<>();
    private final Map<String, Map<String, Integer>> nationMilestones = new LinkedHashMap<>();
    private final Map<String, TurnSnapshot> currentTurn = new HashMap<>();

    // Scoring milestone accumulators (per nation)
    private final Map<String, Boolean> expandedOffHomeIsland = new HashMap<>();
    private final Map<String, Integer> citiesOnNonHomeIslands = new HashMap<>();
    private final Map<String, Boolean> loadedTransport = new HashMap<>();
    private final Map<String, Integer> enemyUnitsKilled = new HashMap<>();
    private final Map<String, Integer> firstCityCapturedTurn = new HashMap<>();
    private final Map<String, Integer> firstTransportLoadedTurn = new HashMap<>();

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

    public void recordExpandedOffHomeIsland(String nationName) {
        expandedOffHomeIsland.put(nationName, true);
    }

    public void recordCitiesOnNonHomeIslands(String nationName, int count) {
        citiesOnNonHomeIslands.put(nationName, count);
    }

    public void recordTransportLoaded(String nationName, int turn) {
        loadedTransport.put(nationName, true);
        firstTransportLoadedTurn.putIfAbsent(nationName, turn);
    }

    public void recordEnemyKill(String nationName) {
        enemyUnitsKilled.merge(nationName, 1, Integer::sum);
    }

    public void recordFirstCityCapture(String nationName, int turn) {
        firstCityCapturedTurn.putIfAbsent(nationName, turn);
    }

    public boolean hasExpandedOffHomeIsland(String nationName) {
        return expandedOffHomeIsland.getOrDefault(nationName, false);
    }

    public int getCitiesOnNonHomeIslands(String nationName) {
        return citiesOnNonHomeIslands.getOrDefault(nationName, 0);
    }

    public boolean hasLoadedTransport(String nationName) {
        return loadedTransport.getOrDefault(nationName, false);
    }

    public int getEnemyUnitsKilled(String nationName) {
        return enemyUnitsKilled.getOrDefault(nationName, 0);
    }

    public int getFirstCityCapturedTurn(String nationName) {
        return firstCityCapturedTurn.getOrDefault(nationName, Integer.MAX_VALUE);
    }

    public int getFirstTransportLoadedTurn(String nationName) {
        return firstTransportLoadedTurn.getOrDefault(nationName, Integer.MAX_VALUE);
    }

    public Map<String, List<TurnSnapshot>> getNationTurns() {
        return nationTurns;
    }

    public Map<String, Map<String, Integer>> getNationMilestones() {
        return nationMilestones;
    }

    public record TurnStateMetrics(int cities, int units, int explored, double tech, boolean hasTransport, int nationsFound) {}

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
