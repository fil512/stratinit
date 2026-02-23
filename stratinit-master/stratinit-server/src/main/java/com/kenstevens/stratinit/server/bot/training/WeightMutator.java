package com.kenstevens.stratinit.server.bot.training;

import com.kenstevens.stratinit.server.bot.BotWeights;

import java.lang.reflect.Field;
import java.util.Random;

public class WeightMutator {
    private static final double MUTATION_PROBABILITY = 0.3;
    private static final double MUTATION_STDDEV = 0.2;
    private static final double MIN_WEIGHT = 0.01;
    private static final double MAX_WEIGHT = 3.0;

    private final Random random;

    public WeightMutator(Random random) {
        this.random = random;
    }

    public WeightMutator() {
        this(new Random());
    }

    public BotWeights mutate(BotWeights parent) {
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

    public BotWeights crossover(BotWeights a, BotWeights b) {
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
