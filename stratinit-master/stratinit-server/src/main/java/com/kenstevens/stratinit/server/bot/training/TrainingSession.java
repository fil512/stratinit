package com.kenstevens.stratinit.server.bot.training;

import com.kenstevens.stratinit.server.bot.PhasedBotWeights;
import com.kenstevens.stratinit.type.BotPersonality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class TrainingSession {
    private static final int PLAYERS_PER_GAME = 8;
    private static final int DEFAULT_GENERATIONS = 10;
    private static final int CROSSOVER_INTERVAL = 5;

    // 8 bots: 2 of each personality
    private static final BotPersonality[] TRAINING_PERSONALITIES = {
            BotPersonality.TECH, BotPersonality.TECH,
            BotPersonality.RUSH, BotPersonality.RUSH,
            BotPersonality.BOOM, BotPersonality.BOOM,
            BotPersonality.TURTLE, BotPersonality.TURTLE
    };

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TrainingGameSimulator simulator;
    @Autowired
    private TrainingAnalyzer analyzer;

    public TrainingResult train(PhasedBotWeights seedWeights) {
        return train(seedWeights, DEFAULT_GENERATIONS);
    }

    public TrainingResult train(PhasedBotWeights seedWeights, int generations) {
        List<Double> scoreHistory = new ArrayList<>();
        List<TrainingGameResult> allResults = new ArrayList<>();
        int totalGamesPlayed = 0;
        WeightMutator mutator = new WeightMutator();

        // Build player-weights and personality maps — deep copy from personality cache
        Map<String, PhasedBotWeights> playerWeights = new LinkedHashMap<>();
        Map<String, BotPersonality> playerPersonalities = new LinkedHashMap<>();
        // Track per-personality weights separately for evolution
        Map<BotPersonality, PhasedBotWeights> personalityWeights = new EnumMap<>(BotPersonality.class);
        for (BotPersonality p : BotPersonality.values()) {
            // Deep copy via mutate with zero perturbation — or just use mutator's copy behavior
            // Use toJson/fromJson for a clean deep copy to avoid corrupting the static cache
            PhasedBotWeights original = PhasedBotWeights.forPersonality(p);
            personalityWeights.put(p, PhasedBotWeights.fromJson(original.toJson()));
        }
        for (int i = 0; i < PLAYERS_PER_GAME; i++) {
            String playerName = "TrainBot-" + i;
            BotPersonality personality = TRAINING_PERSONALITIES[i];
            playerWeights.put(playerName, personalityWeights.get(personality));
            playerPersonalities.put(playerName, personality);
        }

        PhasedBotWeights bestWeightsOverall = null;
        double bestScoreOverall = Double.NEGATIVE_INFINITY;

        for (int gen = 0; gen < generations; gen++) {
            logger.info("=== Generation {} ===", gen + 1);

            // Run the game
            TrainingGameResult result = simulator.simulate(playerWeights, playerPersonalities);
            allResults.add(result);
            totalGamesPlayed++;

            // Compute average scores per personality
            Map<BotPersonality, List<Double>> personalityScores = new EnumMap<>(BotPersonality.class);
            int i = 0;
            List<String> playerNames = new ArrayList<>(result.scores().keySet());
            for (String name : playerNames) {
                BotPersonality p = TRAINING_PERSONALITIES[i];
                personalityScores.computeIfAbsent(p, k -> new ArrayList<>()).add(result.scores().get(name));
                i++;
            }

            Map<BotPersonality, Double> avgScores = new EnumMap<>(BotPersonality.class);
            double bestAvg = 0;
            BotPersonality bestPersonality = null;
            BotPersonality worstPersonality = null;
            double worstAvg = Double.MAX_VALUE;
            for (Map.Entry<BotPersonality, List<Double>> entry : personalityScores.entrySet()) {
                double avg = entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0);
                avgScores.put(entry.getKey(), avg);
                logger.info("  {} avg score: {}", entry.getKey(), String.format("%.2f", avg));
                if (avg > bestAvg) {
                    bestAvg = avg;
                    bestPersonality = entry.getKey();
                }
                if (avg < worstAvg) {
                    worstAvg = avg;
                    worstPersonality = entry.getKey();
                }
            }

            scoreHistory.add(bestAvg);
            logger.info("  Game scores={}, turns={}", result.scores(), result.turnsPlayed());

            // Track best weights across all generations
            if (bestPersonality != null && bestAvg > bestScoreOverall) {
                bestScoreOverall = bestAvg;
                bestWeightsOverall = PhasedBotWeights.fromJson(personalityWeights.get(bestPersonality).toJson());
            }

            // Evolve weights for next generation (skip after last generation)
            if (gen < generations - 1) {
                // Mutate all personalities except the best (elite preservation)
                for (BotPersonality p : BotPersonality.values()) {
                    if (p.equals(bestPersonality)) {
                        logger.info("  {} elite — preserved", p);
                        continue;
                    }
                    PhasedBotWeights mutated = mutator.mutate(personalityWeights.get(p));
                    personalityWeights.put(p, mutated);
                    logger.info("  {} mutated", p);
                }

                // Periodically crossover best into worst
                if ((gen + 1) % CROSSOVER_INTERVAL == 0 && bestPersonality != null
                        && worstPersonality != null && !bestPersonality.equals(worstPersonality)) {
                    PhasedBotWeights crossed = mutator.crossover(
                            personalityWeights.get(bestPersonality),
                            personalityWeights.get(worstPersonality));
                    personalityWeights.put(worstPersonality, crossed);
                    logger.info("  Crossover: {} x {} -> {}", bestPersonality, worstPersonality, worstPersonality);
                }

                // Update playerWeights map to point to the evolved personality weights
                for (Map.Entry<String, BotPersonality> entry : playerPersonalities.entrySet()) {
                    playerWeights.put(entry.getKey(), personalityWeights.get(entry.getValue()));
                }
            }
        }

        // Save results
        saveScoreHistory(scoreHistory);
        if (bestWeightsOverall != null) {
            saveBestWeights(bestWeightsOverall);
        }

        // Analyze bot behavior
        TrainingAnalysisSummary analysis = analyzer.analyze(allResults);

        return new TrainingResult(seedWeights, scoreHistory, totalGamesPlayed, analysis);
    }

    private void saveScoreHistory(List<Double> scoreHistory) {
        try {
            Path dir = Paths.get("training-results");
            Files.createDirectories(dir);
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < scoreHistory.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(String.format("%.2f", scoreHistory.get(i)));
            }
            sb.append("]");
            Files.writeString(dir.resolve("score-history.json"), sb.toString());
            logger.info("Saved score history to training-results/score-history.json");
        } catch (IOException e) {
            logger.warn("Failed to save score history: {}", e.getMessage());
        }
    }

    private void saveBestWeights(PhasedBotWeights weights) {
        try {
            Path dir = Paths.get("training-results");
            Files.createDirectories(dir);
            Files.writeString(dir.resolve("best-weights.json"), weights.toJson());
            logger.info("Saved best weights to training-results/best-weights.json");
        } catch (IOException e) {
            logger.warn("Failed to save best weights: {}", e.getMessage());
        }
    }

    public static PhasedBotWeights loadBestWeights() {
        try {
            Path path = Paths.get("training-results/best-weights.json");
            if (Files.exists(path)) {
                String json = Files.readString(path);
                // Handle both phased and flat formats
                if (json.contains("\"phases\"")) {
                    return PhasedBotWeights.fromJson(json);
                } else {
                    return PhasedBotWeights.fromFlat(com.kenstevens.stratinit.server.bot.BotWeights.fromJson(json));
                }
            }
        } catch (IOException e) {
            // Fall through to default
        }
        return new PhasedBotWeights();
    }
}
