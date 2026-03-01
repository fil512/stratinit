package com.kenstevens.stratinit.server.bot.training;

import com.kenstevens.stratinit.server.bot.PhasedBotWeights;

import java.util.Map;

public record TrainingGameResult(
        Map<String, Double> scores,
        Map<String, PhasedBotWeights> weightsUsed,
        int turnsPlayed,
        TrainingActionLog actionLog
) {
}
