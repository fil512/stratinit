package com.kenstevens.stratinit.server.bot.training;

import com.kenstevens.stratinit.server.bot.PhasedBotWeights;

import java.util.List;

public record TrainingResult(
        PhasedBotWeights bestWeights,
        List<Double> scoreHistory,
        int totalGamesPlayed,
        TrainingAnalysisSummary analysis
) {
}
