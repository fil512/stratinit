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

/**
 * Personality-based training: 4 personalities (Tech, Rush, Boom, Turtle) x 2 bots each.
 * Each personality has its own champion weights evolved via (1+1)-ES.
 * Each generation plays 3 games with z-score normalization.
 * Per-personality: champion is replaced if its challenger scores higher on average.
 */
@Service
public class TrainingSession {
    private static final int PLAYERS_PER_GAME = 8;
    private static final int DEFAULT_GENERATIONS = 10;
    private static final int GAMES_PER_GENERATION = 3;
    private static final BotPersonality[] PERSONALITIES = BotPersonality.values();
    // Cycle through mutation strengths across generations
    private static final double[] SIGMA_CYCLE = {0.05, 0.1, 0.15, 0.2, 0.25, 0.15, 0.1};

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
        TrainingMetricsPublisher metricsPublisher = new TrainingMetricsPublisher();
        int ticksPerGame = Integer.getInteger("training.ticks", 1440);
        metricsPublisher.publishSessionStatus("started", generations, ticksPerGame);

        // Initialize champion weights per personality from their designed profiles
        Map<BotPersonality, PhasedBotWeights> champions = new EnumMap<>(BotPersonality.class);
        for (BotPersonality p : PERSONALITIES) {
            champions.put(p, PhasedBotWeights.forPersonality(p));
        }
        int totalChampionChanges = 0;

        for (int gen = 0; gen < generations; gen++) {
            logger.info("=== Generation {}/{} ===", gen + 1, generations);
            double sigma = SIGMA_CYCLE[gen % SIGMA_CYCLE.length];

            // Create challenger for each personality by mutating its champion
            Map<BotPersonality, PhasedBotWeights> challengers = new EnumMap<>(BotPersonality.class);
            for (BotPersonality p : PERSONALITIES) {
                challengers.put(p, mutator.mutate(champions.get(p), sigma));
            }

            // Accumulate z-scores per bot name across games
            // Names: Tech-1 (champion), Tech-2 (challenger), Rush-1, Rush-2, etc.
            Map<String, Double> totalScores = new LinkedHashMap<>();

            for (int gameNum = 0; gameNum < GAMES_PER_GENERATION; gameNum++) {
                Map<String, PhasedBotWeights> playerWeights = new LinkedHashMap<>();
                Map<String, BotPersonality> playerPersonalities = new LinkedHashMap<>();

                for (BotPersonality p : PERSONALITIES) {
                    String champName = p.name().charAt(0) + p.name().substring(1).toLowerCase() + "-1";
                    String chalName = p.name().charAt(0) + p.name().substring(1).toLowerCase() + "-2";
                    playerWeights.put(champName, champions.get(p));
                    playerWeights.put(chalName, challengers.get(p));
                    playerPersonalities.put(champName, p);
                    playerPersonalities.put(chalName, p);
                }

                TrainingGameResult result = simulator.simulate(playerWeights, playerPersonalities,
                        gen + 1, gameNum + 1, metricsPublisher);
                allResults.add(result);
                totalGamesPlayed++;

                for (Map.Entry<String, Double> entry : result.scores().entrySet()) {
                    totalScores.merge(entry.getKey(), entry.getValue(), Double::sum);
                }

                logger.info("  Game {}/{}: scores={}", gameNum + 1, GAMES_PER_GENERATION, result.scores());
            }

            // Average across games
            Map<String, Double> avgScores = new LinkedHashMap<>();
            for (Map.Entry<String, Double> entry : totalScores.entrySet()) {
                avgScores.put(entry.getKey(), entry.getValue() / GAMES_PER_GENERATION);
            }

            // Per-personality: compare champion vs challenger
            double bestScore = Double.NEGATIVE_INFINITY;
            for (BotPersonality p : PERSONALITIES) {
                String champName = p.name().charAt(0) + p.name().substring(1).toLowerCase() + "-1";
                String chalName = p.name().charAt(0) + p.name().substring(1).toLowerCase() + "-2";
                double champScore = avgScores.getOrDefault(champName, 0.0);
                double chalScore = avgScores.getOrDefault(chalName, 0.0);

                if (chalScore > champScore) {
                    champions.put(p, challengers.get(p));
                    totalChampionChanges++;
                    logger.info("  {} NEW CHAMPION (sigma={}, challenger={} vs champion={})",
                            p.name(), String.format("%.2f", sigma),
                            String.format("%.3f", chalScore), String.format("%.3f", champScore));
                } else {
                    logger.info("  {} champion retained (champion={}, challenger={})",
                            p.name(), String.format("%.3f", champScore), String.format("%.3f", chalScore));
                }
                bestScore = Math.max(bestScore, Math.max(champScore, chalScore));
            }

            scoreHistory.add(bestScore);

            // Publish generation result to Redis
            metricsPublisher.publishGeneration(gen + 1, generations, false,
                    bestScore, 0, scoreHistory);

            // Log all scores
            logger.info("  All scores: {}", avgScores);
        }

        logger.info("=== Training Complete: {} champion changes in {} generations ===",
                totalChampionChanges, generations);

        // Use the best-performing personality's champion as the "best weights" for backward compat
        PhasedBotWeights bestOverall = champions.values().iterator().next();

        // Save results
        saveScoreHistory(scoreHistory);
        savePersonalityWeights(champions);
        saveBestWeights(bestOverall);

        // Analyze bot behavior
        TrainingAnalysisSummary analysis = analyzer.analyze(allResults);

        metricsPublisher.publishSessionStatus("completed", generations, ticksPerGame);
        metricsPublisher.close();

        return new TrainingResult(bestOverall, scoreHistory, totalGamesPlayed, analysis);
    }

    private void savePersonalityWeights(Map<BotPersonality, PhasedBotWeights> champions) {
        try {
            Path dir = Paths.get("training-results");
            Files.createDirectories(dir);
            for (Map.Entry<BotPersonality, PhasedBotWeights> entry : champions.entrySet()) {
                String filename = "best-weights-" + entry.getKey().name().toLowerCase() + ".json";
                Files.writeString(dir.resolve(filename), entry.getValue().toJson());
            }
            logger.info("Saved personality weights to training-results/best-weights-*.json");
        } catch (IOException e) {
            logger.warn("Failed to save personality weights: {}", e.getMessage());
        }
    }

    private void saveScoreHistory(List<Double> scoreHistory) {
        try {
            Path dir = Paths.get("training-results");
            Files.createDirectories(dir);
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < scoreHistory.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(String.format("%.4f", scoreHistory.get(i)));
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
        // Try training results first
        try {
            Path path = Paths.get("training-results/best-weights.json");
            if (Files.exists(path)) {
                String json = Files.readString(path);
                if (json.contains("\"phases\"")) {
                    return PhasedBotWeights.fromJson(json);
                } else {
                    return PhasedBotWeights.fromFlat(com.kenstevens.stratinit.server.bot.BotWeights.fromJson(json));
                }
            }
        } catch (IOException e) {
            // Fall through to classpath
        }
        // Fall back to production bot-weights.json from classpath
        try {
            var resource = new org.springframework.core.io.ClassPathResource("bot-weights.json");
            if (resource.exists()) {
                String json = new String(resource.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
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
