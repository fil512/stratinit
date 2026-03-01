package com.kenstevens.stratinit.server.bot.training;

import com.kenstevens.stratinit.server.bot.BotWeights;
import com.kenstevens.stratinit.server.bot.PhasedBotWeights;

import java.lang.reflect.Field;
import java.util.Random;

public class WeightMutator {
    private static final double MUTATION_PROBABILITY = 0.3;
    private static final double MUTATION_STDDEV = 0.2;
    private static final double MIN_WEIGHT = 0.01;
    private static final double MAX_WEIGHT = 3.0;
    private static final double THRESHOLD_MUTATION_PROBABILITY = 0.1;
    private static final double THRESHOLD_MUTATION_STDDEV = 0.5;

    private final Random random;

    public WeightMutator(Random random) {
        this.random = random;
    }

    public WeightMutator() {
        this(new Random());
    }

    public PhasedBotWeights mutate(PhasedBotWeights parent) {
        PhasedBotWeights child = new PhasedBotWeights();
        // Mutate each phase independently
        for (int i = 0; i < parent.phases.length; i++) {
            child.phases[i] = mutateWeights(parent.phases[i]);
        }
        // Optionally mutate thresholds (keep sorted)
        child.thresholds = parent.thresholds.clone();
        for (int i = 0; i < child.thresholds.length; i++) {
            if (random.nextDouble() < THRESHOLD_MUTATION_PROBABILITY) {
                child.thresholds[i] += random.nextGaussian() * THRESHOLD_MUTATION_STDDEV;
                child.thresholds[i] = Math.max(0.5, Math.min(15.0, child.thresholds[i]));
            }
        }
        // Ensure thresholds stay sorted
        java.util.Arrays.sort(child.thresholds);
        return child;
    }

    public PhasedBotWeights crossover(PhasedBotWeights a, PhasedBotWeights b) {
        PhasedBotWeights child = new PhasedBotWeights();
        // Per-phase crossover
        for (int i = 0; i < a.phases.length; i++) {
            child.phases[i] = crossoverWeights(a.phases[i], b.phases[i]);
        }
        // Pick thresholds from one parent
        child.thresholds = random.nextBoolean() ? a.thresholds.clone() : b.thresholds.clone();
        return child;
    }

    private BotWeights mutateWeights(BotWeights parent) {
        BotWeights child = copyWeights(parent);
        for (Field field : BotWeights.class.getDeclaredFields()) {
            if (field.getType() != double.class) {
                continue;
            }
            if (random.nextDouble() < MUTATION_PROBABILITY) {
                try {
                    double value = field.getDouble(child);
                    value += random.nextGaussian() * MUTATION_STDDEV;
                    value = Math.max(MIN_WEIGHT, Math.min(MAX_WEIGHT, value));
                    field.setDouble(child, value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to mutate field " + field.getName(), e);
                }
            }
        }
        return child;
    }

    private BotWeights crossoverWeights(BotWeights a, BotWeights b) {
        BotWeights child = new BotWeights();
        for (Field field : BotWeights.class.getDeclaredFields()) {
            if (field.getType() != double.class) {
                continue;
            }
            try {
                double value = random.nextBoolean() ? field.getDouble(a) : field.getDouble(b);
                field.setDouble(child, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to crossover field " + field.getName(), e);
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
