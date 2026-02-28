package com.kenstevens.stratinit.server.bot.training;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class TrainingAnalyzer {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public TrainingAnalysisSummary analyze(List<TrainingGameResult> allResults) {
        TrainingAnalysisSummary summary = new TrainingAnalysisSummary();

        // Aggregate final-turn metrics across all games and nations
        Map<String, List<Double>> citiesByNation = new LinkedHashMap<>();
        Map<String, List<Double>> unitsByNation = new LinkedHashMap<>();
        Map<String, List<Double>> techByNation = new LinkedHashMap<>();
        Map<String, List<Double>> exploredByNation = new LinkedHashMap<>();
        Map<String, Integer> totalActionFrequency = new LinkedHashMap<>();
        Map<String, List<Integer>> milestoneTurns = new LinkedHashMap<>();
        List<String> failurePatterns = new ArrayList<>();
        Map<Integer, List<Map<String, Double>>> progressionByTurn = new LinkedHashMap<>();

        int totalNations = 0;
        // Track aggregate action counts for failure pattern detection
        Map<String, Integer> globalActionCounts = new LinkedHashMap<>();

        for (TrainingGameResult result : allResults) {
            TrainingActionLog log = result.actionLog();
            if (log == null) continue;

            for (Map.Entry<String, List<TrainingActionLog.TurnSnapshot>> entry : log.getNationTurns().entrySet()) {
                String nationName = entry.getKey();
                List<TrainingActionLog.TurnSnapshot> turns = entry.getValue();
                if (turns.isEmpty()) continue;

                totalNations++;

                // Final turn metrics
                TrainingActionLog.TurnSnapshot lastTurn = turns.get(turns.size() - 1);
                TrainingActionLog.TurnStateMetrics finalMetrics = lastTurn.getMetrics();
                citiesByNation.computeIfAbsent("all", k -> new ArrayList<>()).add((double) finalMetrics.cities());
                unitsByNation.computeIfAbsent("all", k -> new ArrayList<>()).add((double) finalMetrics.units());
                techByNation.computeIfAbsent("all", k -> new ArrayList<>()).add(finalMetrics.tech());
                exploredByNation.computeIfAbsent("all", k -> new ArrayList<>()).add((double) finalMetrics.explored());

                // Action frequency across all turns for this nation
                Map<String, Integer> nationActionCounts = new LinkedHashMap<>();
                for (TrainingActionLog.TurnSnapshot turn : turns) {
                    for (Map.Entry<String, Integer> actionEntry : turn.getExecutedCounts().entrySet()) {
                        totalActionFrequency.merge(actionEntry.getKey(), actionEntry.getValue(), Integer::sum);
                        nationActionCounts.merge(actionEntry.getKey(), actionEntry.getValue(), Integer::sum);
                        globalActionCounts.merge(actionEntry.getKey(), actionEntry.getValue(), Integer::sum);
                    }
                }

                // State progression at every 10th turn
                for (TrainingActionLog.TurnSnapshot turn : turns) {
                    int turnNum = turn.getTurnNumber();
                    if (turnNum % 10 == 0) {
                        Map<String, Double> metrics = new LinkedHashMap<>();
                        metrics.put("cities", (double) turn.getMetrics().cities());
                        metrics.put("units", (double) turn.getMetrics().units());
                        metrics.put("explored", (double) turn.getMetrics().explored());
                        metrics.put("tech", turn.getMetrics().tech());
                        progressionByTurn.computeIfAbsent(turnNum, k -> new ArrayList<>()).add(metrics);
                    }
                }

                // Per-nation failure pattern detection
                boolean builtTransport = nationActionCounts.containsKey("SetCityProduction:TRANSPORT");
                int loadCount = nationActionCounts.getOrDefault("LoadTransportAction", 0);
                int disembarkCount = nationActionCounts.getOrDefault("DisembarkUnitAction", 0);
                int moveToCoastCount = nationActionCounts.getOrDefault("MoveToCoastForPickupAction", 0);

                if (builtTransport && loadCount == 0) {
                    failurePatterns.add(nationName + ": Transport built but never loaded");
                }
                if (moveToCoastCount > 3 && loadCount == 0) {
                    failurePatterns.add(nationName + ": Units moved to coast but no transport loaded");
                }
                int swimCount = nationActionCounts.getOrDefault("SwimEngineerToIslandAction", 0);
                if (disembarkCount == 0 && swimCount == 0 && finalMetrics.cities() <= 4) {
                    failurePatterns.add(nationName + ": Never expanded off home island");
                }
                if (swimCount > 0) {
                    // Check if engineer swam but never built a city on new island
                    boolean builtCityAction = nationActionCounts.containsKey("BuildCityWithEngineerAction");
                    if (!builtCityAction && disembarkCount == 0 && finalMetrics.cities() <= 4) {
                        failurePatterns.add(nationName + ": Engineer swam but never built city on new island");
                    }
                }
                if (finalMetrics.explored() < 100) {
                    failurePatterns.add(nationName + ": Low exploration (" + finalMetrics.explored() + " hexes)");
                }
            }

            // Milestones
            for (Map.Entry<String, Map<String, Integer>> milestoneEntry : log.getNationMilestones().entrySet()) {
                for (Map.Entry<String, Integer> m : milestoneEntry.getValue().entrySet()) {
                    milestoneTurns.computeIfAbsent(m.getKey(), k -> new ArrayList<>()).add(m.getValue());
                }
            }
        }

        // Compute averages
        summary.setAverageCities(computeAverages(citiesByNation));
        summary.setAverageUnits(computeAverages(unitsByNation));
        summary.setAverageTech(computeAverages(techByNation));
        summary.setAverageExplored(computeAverages(exploredByNation));

        // Average scores from results
        Map<String, List<Double>> scoresByNation = new LinkedHashMap<>();
        for (TrainingGameResult result : allResults) {
            for (Map.Entry<String, Double> scoreEntry : result.scores().entrySet()) {
                scoresByNation.computeIfAbsent("all", k -> new ArrayList<>()).add(scoreEntry.getValue());
            }
        }
        summary.setAverageScore(computeAverages(scoresByNation));

        summary.setActionFrequency(totalActionFrequency);

        // Average milestones
        Map<String, Double> avgMilestones = new LinkedHashMap<>();
        for (Map.Entry<String, List<Integer>> entry : milestoneTurns.entrySet()) {
            avgMilestones.put(entry.getKey(), entry.getValue().stream()
                    .mapToInt(Integer::intValue).average().orElse(-1));
        }
        summary.setAverageMilestones(avgMilestones);

        // Deduplicate failure patterns by counting occurrences
        Map<String, Integer> patternCounts = new LinkedHashMap<>();
        for (String pattern : failurePatterns) {
            // Strip nation name prefix for aggregation
            String key = pattern.replaceFirst("^[^:]+: ", "");
            patternCounts.merge(key, 1, Integer::sum);
        }
        List<String> aggregatedPatterns = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : patternCounts.entrySet()) {
            aggregatedPatterns.add(entry.getKey() + " (" + entry.getValue() + "/" + totalNations + " nations)");
        }
        summary.setFailurePatterns(aggregatedPatterns);

        // State progression: average across all nations at each snapshot turn
        Map<Integer, Map<String, Double>> avgProgression = new LinkedHashMap<>();
        for (Map.Entry<Integer, List<Map<String, Double>>> entry : progressionByTurn.entrySet()) {
            Map<String, Double> avg = new LinkedHashMap<>();
            List<Map<String, Double>> snapshots = entry.getValue();
            for (String key : new String[]{"cities", "units", "explored", "tech"}) {
                double sum = snapshots.stream().mapToDouble(m -> m.getOrDefault(key, 0.0)).sum();
                avg.put(key, Math.round(sum / snapshots.size() * 100.0) / 100.0);
            }
            avgProgression.put(entry.getKey(), avg);
        }
        summary.setStateProgression(avgProgression);

        // Save to file
        saveAnalysis(summary);

        return summary;
    }

    private Map<String, Double> computeAverages(Map<String, List<Double>> data) {
        Map<String, Double> result = new LinkedHashMap<>();
        for (Map.Entry<String, List<Double>> entry : data.entrySet()) {
            double avg = entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0);
            result.put(entry.getKey(), Math.round(avg * 100.0) / 100.0);
        }
        return result;
    }

    private void saveAnalysis(TrainingAnalysisSummary summary) {
        try {
            Path dir = Paths.get("training-results");
            Files.createDirectories(dir);
            String json = objectMapper.writeValueAsString(summary);
            Files.writeString(dir.resolve("training-analysis.json"), json);
            logger.info("Saved training analysis to training-results/training-analysis.json");
        } catch (IOException e) {
            logger.warn("Failed to save training analysis: {}", e.getMessage());
        }
    }
}
