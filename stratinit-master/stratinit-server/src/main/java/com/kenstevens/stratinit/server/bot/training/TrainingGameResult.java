package com.kenstevens.stratinit.server.bot.training;

import com.kenstevens.stratinit.server.bot.BotWeights;

import java.util.Map;

public record TrainingGameResult(
        Map<String, Double> scores,
        Map<String, BotWeights> weightsUsed,
        int turnsPlayed
) {
}
