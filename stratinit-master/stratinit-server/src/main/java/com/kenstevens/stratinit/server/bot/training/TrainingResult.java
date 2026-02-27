package com.kenstevens.stratinit.server.bot.training;

import com.kenstevens.stratinit.server.bot.BotWeights;

import java.util.List;

public record TrainingResult(
        BotWeights bestWeights,
        List<Double> scoreHistory,
        int totalGamesPlayed,
        TrainingAnalysisSummary analysis
) {
}
