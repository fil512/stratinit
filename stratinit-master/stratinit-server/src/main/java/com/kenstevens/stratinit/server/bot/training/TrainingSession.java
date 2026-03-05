package com.kenstevens.stratinit.server.bot.training;

import com.kenstevens.stratinit.server.bot.PhasedBotWeights;
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
 * (1+7)-ES training: 1 champion + 7 challengers per generation.
 * Each generation plays 3 games with z-score normalization.
 * Champion is replaced only if a challenger achieves a higher average normalized score.
 */
@Service
public class TrainingSession {
    private static final int PLAYERS_PER_GAME = 8;
    private static final int DEFAULT_GENERATIONS = 10;
    private static final int GAMES_PER_GENERATION = 3;
    private static final double[] CHALLENGER_SIGMAS = {0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.4};

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

        // Champion starts as the seed weights
        PhasedBotWeights champion = PhasedBotWeights.fromJson(seedWeights.toJson());
        int championChanges = 0;

        for (int gen = 0; gen < generations; gen++) {
            logger.info("=== Generation {}/{} ===", gen + 1, generations);

            // Create 7 challengers by mutating champion with variable sigma
            PhasedBotWeights[] configs = new PhasedBotWeights[PLAYERS_PER_GAME];
            configs[0] = champion;
            for (int i = 0; i < CHALLENGER_SIGMAS.length; i++) {
                configs[i + 1] = mutator.mutate(champion, CHALLENGER_SIGMAS[i]);
            }

            // Build player-weights map: config 0 = champion, configs 1-7 = challengers
            // Average z-scores across GAMES_PER_GENERATION games
            double[] avgScores = new double[PLAYERS_PER_GAME];

            for (int gameNum = 0; gameNum < GAMES_PER_GENERATION; gameNum++) {
                Map<String, PhasedBotWeights> playerWeights = new LinkedHashMap<>();
                for (int i = 0; i < PLAYERS_PER_GAME; i++) {
                    playerWeights.put("TrainBot-" + i, configs[i]);
                }

                TrainingGameResult result = simulator.simulate(playerWeights,
                        gen + 1, gameNum + 1, metricsPublisher);
                allResults.add(result);
                totalGamesPlayed++;

                // Accumulate z-scores (already normalized by scoreAll)
                int idx = 0;
                for (String name : result.scores().keySet()) {
                    avgScores[idx] += result.scores().get(name);
                    idx++;
                }

                logger.info("  Game {}/{}: scores={}", gameNum + 1, GAMES_PER_GENERATION, result.scores());
            }

            // Average across games
            for (int i = 0; i < PLAYERS_PER_GAME; i++) {
                avgScores[i] /= GAMES_PER_GENERATION;
            }

            // Find best config
            double championScore = avgScores[0];
            int bestIdx = 0;
            double bestScore = championScore;
            for (int i = 1; i < PLAYERS_PER_GAME; i++) {
                if (avgScores[i] > bestScore) {
                    bestScore = avgScores[i];
                    bestIdx = i;
                }
            }

            scoreHistory.add(bestScore);

            if (bestIdx > 0) {
                champion = configs[bestIdx];
                championChanges++;
                logger.info("  NEW CHAMPION (sigma={}, score={} vs champion={})",
                        String.format("%.2f", CHALLENGER_SIGMAS[bestIdx - 1]),
                        String.format("%.3f", bestScore),
                        String.format("%.3f", championScore));
            } else {
                logger.info("  Champion retained (score={})", String.format("%.3f", championScore));
            }

            // Publish generation result to Redis
            double bestChallengerScore = 0;
            for (int i = 1; i < PLAYERS_PER_GAME; i++) {
                bestChallengerScore = Math.max(bestChallengerScore, avgScores[i]);
            }
            metricsPublisher.publishGeneration(gen + 1, generations, bestIdx > 0,
                    avgScores[0], bestChallengerScore, scoreHistory);

            // Log all scores
            StringBuilder sb = new StringBuilder("  Scores: champion=");
            sb.append(String.format("%.3f", avgScores[0]));
            for (int i = 1; i < PLAYERS_PER_GAME; i++) {
                sb.append(String.format(", s%.2f=%.3f", CHALLENGER_SIGMAS[i - 1], avgScores[i]));
            }
            logger.info(sb.toString());
        }

        logger.info("=== Training Complete: {} champion changes in {} generations ===",
                championChanges, generations);

        // Regression test: champion vs seed weights (5 games, 4v4)
        if (championChanges > 0) {
            int validationWins = runValidation(champion, seedWeights, 5);
            logger.info("=== Validation: champion won {}/5 games vs seed ===", validationWins);
            if (validationWins < 3) {
                logger.warn("Champion failed validation (won only {}/5). Reverting to seed weights.", validationWins);
                champion = PhasedBotWeights.fromJson(seedWeights.toJson());
            }
        }

        // Save results
        saveScoreHistory(scoreHistory);
        saveBestWeights(champion);

        // Analyze bot behavior
        TrainingAnalysisSummary analysis = analyzer.analyze(allResults);

        metricsPublisher.publishSessionStatus("completed", generations, ticksPerGame);
        metricsPublisher.close();

        return new TrainingResult(champion, scoreHistory, totalGamesPlayed, analysis);
    }

    /**
     * Run validation games: 4 bots with champion weights vs 4 bots with seed weights.
     * Returns the number of games where champion team's average z-score beats seed team's.
     */
    private int runValidation(PhasedBotWeights champion, PhasedBotWeights seed, int games) {
        int championWins = 0;
        for (int i = 0; i < games; i++) {
            Map<String, PhasedBotWeights> playerWeights = new LinkedHashMap<>();
            // First 4 = champion, last 4 = seed
            for (int j = 0; j < 4; j++) {
                playerWeights.put("Champion-" + j, champion);
            }
            for (int j = 0; j < 4; j++) {
                playerWeights.put("Seed-" + j, seed);
            }
            TrainingGameResult result = simulator.simulate(playerWeights);
            // Average z-scores for each team
            double championAvg = 0, seedAvg = 0;
            int idx = 0;
            for (double score : result.scores().values()) {
                if (idx < 4) championAvg += score;
                else seedAvg += score;
                idx++;
            }
            championAvg /= 4;
            seedAvg /= 4;
            if (championAvg > seedAvg) championWins++;
            logger.info("  Validation game {}: champion={} seed={}",
                    i + 1, String.format("%.3f", championAvg), String.format("%.3f", seedAvg));
        }
        return championWins;
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
