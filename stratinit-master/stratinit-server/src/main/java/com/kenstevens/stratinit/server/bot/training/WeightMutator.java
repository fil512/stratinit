package com.kenstevens.stratinit.server.bot.training;

import com.kenstevens.stratinit.server.bot.BotWeights;
import com.kenstevens.stratinit.server.bot.PhasedBotWeights;

import java.lang.reflect.Field;
import java.util.Random;

public class WeightMutator {
    private static final double MIN_WEIGHT = 0.01;
    private static final double MAX_WEIGHT = 3.0;
    private static final double THRESHOLD_MUTATION_PROBABILITY = 0.3;

    private final Random random;

    public WeightMutator(Random random) {
        this.random = random;
    }

    public WeightMutator() {
        this(new Random());
    }

    /**
     * Mutate all weights with the given standard deviation.
     * Every weight is perturbed (no per-weight probability gate) since
     * variable sigma across challengers provides exploration diversity.
     */
    public PhasedBotWeights mutate(PhasedBotWeights parent, double sigma) {
        PhasedBotWeights child = new PhasedBotWeights();
        child.phases = new com.kenstevens.stratinit.server.bot.BotWeights[parent.phases.length];
        for (int i = 0; i < parent.phases.length; i++) {
            child.phases[i] = mutateWeights(parent.phases[i], sigma);
        }
        child.thresholds = parent.thresholds.clone();
        for (int i = 0; i < child.thresholds.length; i++) {
            if (random.nextDouble() < THRESHOLD_MUTATION_PROBABILITY) {
                child.thresholds[i] += random.nextGaussian() * sigma * 2.0;
                child.thresholds[i] = Math.max(0.5, Math.min(15.0, child.thresholds[i]));
            }
        }
        java.util.Arrays.sort(child.thresholds);
        return child;
    }

    /**
     * Legacy mutate with default sigma (backward compatibility).
     */
    public PhasedBotWeights mutate(PhasedBotWeights parent) {
        return mutate(parent, 0.2);
    }

    private BotWeights mutateWeights(BotWeights parent, double sigma) {
        BotWeights child = copyWeights(parent);
        for (Field field : BotWeights.class.getDeclaredFields()) {
            if (field.getType() != double.class) {
                continue;
            }
            try {
                double value = field.getDouble(child);
                value += random.nextGaussian() * sigma;
                value = Math.max(MIN_WEIGHT, Math.min(MAX_WEIGHT, value));
                field.setDouble(child, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to mutate field " + field.getName(), e);
            }
        }
        return child;
    }

    private BotWeights copyWeights(BotWeights source) {
        BotWeights copy = new BotWeights();
        for (Field field : BotWeights.class.getDeclaredFields()) {
            if (field.getType() != double.class) {
                continue;
            }
            try {
                field.setDouble(copy, field.getDouble(source));
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to copy field " + field.getName(), e);
            }
        }
        return copy;
    }
}
