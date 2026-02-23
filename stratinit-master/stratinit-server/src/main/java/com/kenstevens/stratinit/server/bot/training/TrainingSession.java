package com.kenstevens.stratinit.server.bot.training;

import com.kenstevens.stratinit.server.bot.BotWeights;
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
    private static final int POPULATION_SIZE = 8;
    private static final int SURVIVORS = 4;
    private static final int GAMES_PER_GENERATION = 2;
    private static final int PLAYERS_PER_GAME = 4;
    private static final int DEFAULT_GENERATIONS = 10;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TrainingGameSimulator simulator;

    private final WeightMutator mutator = new WeightMutator();
    private final Random random = new Random();

    public TrainingResult train(BotWeights seedWeights) {
        return train(seedWeights, DEFAULT_GENERATIONS);
    }

    public TrainingResult train(BotWeights seedWeights, int generations) {
        // Initialize population: seed + mutations
        List<BotWeights> population = new ArrayList<>();
        population.add(seedWeights);
        for (int i = 1; i < POPULATION_SIZE; i++) {
            population.add(mutator.mutate(seedWeights));
        }

        List<Double> scoreHistory = new ArrayList<>();
        int totalGamesPlayed = 0;

        for (int gen = 0; gen < generations; gen++) {
            logger.info("=== Generation {} ===", gen + 1);

            // Track cumulative scores for each population member
            double[] cumulativeScores = new double[POPULATION_SIZE];
            int[] gamesPlayedBy = new int[POPULATION_SIZE];

            for (int gameNum = 0; gameNum < GAMES_PER_GENERATION; gameNum++) {
                // Pick 4 random distinct population indices
                List<Integer> indices = pickRandomIndices(POPULATION_SIZE, PLAYERS_PER_GAME);

                // Build player-weights map
                Map<String, BotWeights> playerWeights = new LinkedHashMap<>();
                for (int idx : indices) {
                    String playerName = "TrainBot-" + idx;
                    playerWeights.put(playerName, population.get(idx));
                }

                // Run the game
                TrainingGameResult result = simulator.simulate(playerWeights);
                totalGamesPlayed++;

                // Accumulate scores
                int i = 0;
                for (Map.Entry<String, Double> entry : result.scores().entrySet()) {
                    int popIdx = indices.get(i);
                    cumulativeScores[popIdx] += entry.getValue();
                    gamesPlayedBy[popIdx]++;
                    i++;
                }

                logger.info("  Game {}: scores={}, turns={}",
                        gameNum + 1, result.scores(), result.turnsPlayed());
            }

            // Average scores
            double[] avgScores = new double[POPULATION_SIZE];
            for (int i = 0; i < POPULATION_SIZE; i++) {
                avgScores[i] = gamesPlayedBy[i] > 0 ? cumulativeScores[i] / gamesPlayedBy[i] : 0;
            }

            // Sort population by score (descending)
            Integer[] sortedIndices = new Integer[POPULATION_SIZE];
            for (int i = 0; i < POPULATION_SIZE; i++) sortedIndices[i] = i;
            Arrays.sort(sortedIndices, (a, b) -> Double.compare(avgScores[b], avgScores[a]));

            // Keep top survivors, replace bottom with mutations/crossovers of top
            List<BotWeights> newPopulation = new ArrayList<>();
            for (int i = 0; i < SURVIVORS; i++) {
                newPopulation.add(population.get(sortedIndices[i]));
            }
            for (int i = SURVIVORS; i < POPULATION_SIZE; i++) {
                int parentA = random.nextInt(SURVIVORS);
                int parentB = random.nextInt(SURVIVORS);
                BotWeights child;
                if (parentA == parentB) {
                    child = mutator.mutate(newPopulation.get(parentA));
                } else {
                    child = mutator.mutate(mutator.crossover(
                            newPopulation.get(parentA), newPopulation.get(parentB)));
                }
                newPopulation.add(child);
            }
            population = newPopulation;

            double bestScore = avgScores[sortedIndices[0]];
            scoreHistory.add(bestScore);
            logger.info("  Best score: {}", bestScore);
        }

        BotWeights bestWeights = population.get(0);

        // Save results
        saveBestWeights(bestWeights);
        saveScoreHistory(scoreHistory);

        return new TrainingResult(bestWeights, scoreHistory, totalGamesPlayed);
    }

    private List<Integer> pickRandomIndices(int populationSize, int count) {
        List<Integer> all = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) all.add(i);
        Collections.shuffle(all, random);
        return all.subList(0, count);
    }

    private void saveBestWeights(BotWeights weights) {
        try {
            Path dir = Paths.get("training-results");
            Files.createDirectories(dir);
            Files.writeString(dir.resolve("best-weights.json"), weights.toJson());
            logger.info("Saved best weights to training-results/best-weights.json");
        } catch (IOException e) {
            logger.warn("Failed to save best weights: {}", e.getMessage());
        }
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

    public static BotWeights loadBestWeights() {
        try {
            Path path = Paths.get("training-results/best-weights.json");
            if (Files.exists(path)) {
                String json = Files.readString(path);
                return BotWeights.fromJson(json);
            }
        } catch (IOException e) {
            // Fall through to default
        }
        return new BotWeights();
    }
}
