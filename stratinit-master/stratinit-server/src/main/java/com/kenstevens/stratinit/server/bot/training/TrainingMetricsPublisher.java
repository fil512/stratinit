package com.kenstevens.stratinit.server.bot.training;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Publishes training metrics to Redis pub/sub channels for live dashboard consumption.
 * Falls back gracefully if Redis is unavailable.
 */
public class TrainingMetricsPublisher {
    private static final Logger logger = LoggerFactory.getLogger(TrainingMetricsPublisher.class);
    private static final String CHANNEL_TICK = "training:tick";
    private static final String CHANNEL_GAME = "training:game-result";
    private static final String CHANNEL_GENERATION = "training:generation";
    private static final String CHANNEL_SESSION = "training:session";
    private static final int TICK_SUBSAMPLE = 10;

    private final ObjectMapper mapper = new ObjectMapper();
    private JedisPool pool;
    private boolean available;

    public TrainingMetricsPublisher() {
        String host = System.getProperty("redis.host", "localhost");
        int port = Integer.getInteger("redis.port", 6379);
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(4);
            config.setMaxIdle(2);
            pool = new JedisPool(config, host, port, 2000);
            // Test connection
            try (Jedis jedis = pool.getResource()) {
                jedis.ping();
            }
            available = true;
            logger.info("Redis metrics publisher connected to {}:{}", host, port);
        } catch (JedisConnectionException e) {
            available = false;
            logger.info("Redis not available at {}:{} — training metrics will not be published", host, port);
        }
    }

    public boolean isAvailable() {
        return available;
    }

    public void publishTick(int generation, int gameNum, int tick, String nationName,
                            TrainingActionLog.TurnStateMetrics metrics,
                            Map<String, Integer> executedCounts) {
        if (!available || tick % TICK_SUBSAMPLE != 0) return;

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("type", "tick");
        payload.put("generation", generation);
        payload.put("gameNum", gameNum);
        payload.put("tick", tick);
        payload.put("nation", nationName);
        payload.put("cities", metrics.cities());
        payload.put("units", metrics.units());
        payload.put("explored", metrics.explored());
        payload.put("tech", metrics.tech());
        payload.put("hasTransport", metrics.hasTransport());
        if (executedCounts != null && !executedCounts.isEmpty()) {
            payload.put("actions", executedCounts);
        }
        publish(CHANNEL_TICK, payload);
    }

    public void publishGameResult(int generation, int gameNum, int turnsPlayed,
                                  Map<String, Double> scores,
                                  Map<String, Map<String, Integer>> milestones) {
        if (!available) return;

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("type", "game-result");
        payload.put("generation", generation);
        payload.put("gameNum", gameNum);
        payload.put("turnsPlayed", turnsPlayed);
        payload.put("scores", scores);
        if (milestones != null) {
            payload.put("milestones", milestones);
        }
        publish(CHANNEL_GAME, payload);
    }

    public void publishGeneration(int generation, int totalGenerations, boolean championChanged,
                                  double championScore, double bestChallengerScore,
                                  List<Double> scoreHistory) {
        if (!available) return;

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("type", "generation");
        payload.put("generation", generation);
        payload.put("totalGenerations", totalGenerations);
        payload.put("championChanged", championChanged);
        payload.put("championScore", championScore);
        payload.put("bestChallengerScore", bestChallengerScore);
        payload.put("scoreHistory", scoreHistory);
        publish(CHANNEL_GENERATION, payload);
    }

    public void publishSessionStatus(String status, int generations, int ticksPerGame) {
        if (!available) return;

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("type", "session");
        payload.put("status", status);
        payload.put("generations", generations);
        payload.put("ticksPerGame", ticksPerGame);
        payload.put("timestamp", System.currentTimeMillis());
        publish(CHANNEL_SESSION, payload);
    }

    private void publish(String channel, Map<String, Object> payload) {
        try (Jedis jedis = pool.getResource()) {
            String json = mapper.writeValueAsString(payload);
            jedis.publish(channel, json);
        } catch (Exception e) {
            logger.debug("Failed to publish to {}: {}", channel, e.getMessage());
        }
    }

    public void close() {
        if (pool != null) {
            pool.close();
        }
    }
}
