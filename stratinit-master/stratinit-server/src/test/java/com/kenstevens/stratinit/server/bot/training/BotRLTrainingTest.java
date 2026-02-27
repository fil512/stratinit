package com.kenstevens.stratinit.server.bot.training;

import com.kenstevens.stratinit.client.server.rest.StratInitDaoBase;
import com.kenstevens.stratinit.server.bot.BotWeights;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BotRLTrainingTest extends StratInitDaoBase {

    @Autowired
    private TrainingSession trainingSession;

    @Test
    void trainBotsAndVerifyImprovement() {
        // Load seed weights (from file if available, otherwise defaults)
        BotWeights seedWeights = TrainingSession.loadBestWeights();

        int generations = Integer.getInteger("training.generations", 3);
        TrainingResult result = trainingSession.train(seedWeights, generations);

        // Verify we got results
        assertNotNull(result.bestWeights());
        assertNotNull(result.scoreHistory());
        assertFalse(result.scoreHistory().isEmpty(), "Score history should not be empty");

        // Print results
        System.out.println("=== Training Complete ===");
        System.out.println("Total games played: " + result.totalGamesPlayed());
        System.out.println("Score history: " + result.scoreHistory());
        System.out.println("Best weights:");
        System.out.println(result.bestWeights().toJson());

        // Verify scores are non-negative
        for (double score : result.scoreHistory()) {
            assert score >= 0 : "Scores should be non-negative";
        }
    }
}
