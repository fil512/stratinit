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

        // Build player-weights and personality maps once (identical across generations)
        Map<String, PhasedBotWeights> playerWeights = new LinkedHashMap<>();
        Map<String, BotPersonality> playerPersonalities = new LinkedHashMap<>();
        for (int i = 0; i < PLAYERS_PER_GAME; i++) {
            String playerName = "TrainBot-" + i;
            BotPersonality personality = TRAINING_PERSONALITIES[i];
            playerWeights.put(playerName, PhasedBotWeights.forPersonality(personality));
            playerPersonalities.put(playerName, personality);
        }

        for (int gen = 0; gen < generations; gen++) {
            logger.info("=== Generation {} ===", gen + 1);

            // Run the game
            TrainingGameResult result = simulator.simulate(playerWeights, playerPersonalities);
            allResults.add(result);
            totalGamesPlayed++;

            // Log scores per personality
            Map<BotPersonality, List<Double>> personalityScores = new EnumMap<>(BotPersonality.class);
            int i = 0;
            List<String> playerNames = new ArrayList<>(result.scores().keySet());
            for (String name : playerNames) {
                BotPersonality p = TRAINING_PERSONALITIES[i];
                personalityScores.computeIfAbsent(p, k -> new ArrayList<>()).add(result.scores().get(name));
                i++;
            }

            // Compute average scores per personality
            double bestAvg = 0;
            for (Map.Entry<BotPersonality, List<Double>> entry : personalityScores.entrySet()) {
                double avg = entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0);
                logger.info("  {} avg score: {}", entry.getKey(), String.format("%.2f", avg));
                bestAvg = Math.max(bestAvg, avg);
            }

            scoreHistory.add(bestAvg);
            logger.info("  Game scores={}, turns={}", result.scores(), result.turnsPlayed());
        }

        // Save results
        saveScoreHistory(scoreHistory);

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
