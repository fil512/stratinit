package com.kenstevens.stratinit.server.bot.training;

import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.client.util.ExpungeSvc;
import com.kenstevens.stratinit.client.util.GameScheduleHelper;
import com.kenstevens.stratinit.client.util.UpdateCalculator;
import com.kenstevens.stratinit.dao.CityDao;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.NationDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.bot.BotExecutor;
import com.kenstevens.stratinit.server.bot.BotWeights;
import com.kenstevens.stratinit.server.event.svc.GameStartupService;
import com.kenstevens.stratinit.server.service.GameService;
import com.kenstevens.stratinit.server.service.UnitService;
import com.kenstevens.stratinit.server.svc.CityBuilderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TrainingGameSimulator {
    private static final int MAX_TURNS = 100;
    private static final int PLAYERS_PER_GAME = 4;
    private static final String TRAINING_GAME_PREFIX = "Training-";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private GameService gameService;
    @Autowired
    private GameStartupService gameStartupService;
    @Autowired
    private GameDao gameDao;
    @Autowired
    private NationDao nationDao;
    @Autowired
    private CityDao cityDao;
    @Autowired
    private UnitDao unitDao;
    @Autowired
    private UnitService unitService;
    @Autowired
    private CityBuilderService cityBuilderService;
    @Autowired
    private BotExecutor botExecutor;
    @Autowired
    private TrainingScorer scorer;
    @Autowired
    private DataCache dataCache;
    @Autowired
    private ExpungeSvc expungeSvc;
    @Autowired
    private com.kenstevens.stratinit.dao.PlayerDao playerDao;

    public TrainingGameResult simulate(Map<String, BotWeights> playerWeights) {
        String gameName = TRAINING_GAME_PREFIX + System.currentTimeMillis();

        // Create game and players
        Game game = new Game(gameName);
        game.setBlitz(true);
        game.setIslands(PLAYERS_PER_GAME);
        gameDao.save(game);

        List<String> playerNames = new ArrayList<>(playerWeights.keySet());
        Map<String, Nation> nations = new LinkedHashMap<>();

        // Create players and join game
        Date now = new Date();
        GameScheduleHelper.setStartTime(game, now);
        game.setLastUpdated(now);
        gameDao.merge(game);

        for (String name : playerNames) {
            Player player = playerDao.find(name);
            if (player == null) {
                player = Player.makeBotPlayer(name);
                playerDao.save(player);
            }
            Result<Nation> result = gameService.joinGame(player, game.getId(), false);
            if (!result.isSuccess()) {
                throw new RuntimeException("Failed to join game: " + result);
            }
            nations.put(name, result.getValue());
        }

        // Map the game
        gameService.mapGame(game);

        // Initialize game state without scheduling events
        gameStartupService.initializeGameState(game, true);

        // Set start time in the past so hasStarted() returns true
        Date pastStart = new Date(now.getTime() - 1000);
        game.setStartTime(pastStart);
        // Set ends far in the future so hasEnded() returns false during simulation
        game.setEnds(new Date(now.getTime() + 365L * 24 * 60 * 60 * 1000));
        game.setLastUpdated(pastStart);
        gameDao.merge(game);

        // Set initial lastUpdated on all cities and units
        Collection<City> allCities = cityDao.getCities(game);
        for (City city : allCities) {
            city.setLastUpdated(pastStart);
        }
        Collection<Unit> allUnits = unitDao.getUnits(game);
        for (Unit unit : allUnits) {
            unit.setLastUpdated(pastStart);
        }

        // Compute tick interval: use the smallest update period across units and cities
        long tickInterval = computeTickInterval(game);

        // Simulation loop
        long simulatedTime = pastStart.getTime();
        int turnsPlayed = 0;

        for (int turn = 0; turn < MAX_TURNS; turn++) {
            simulatedTime += tickInterval;
            Date simDate = new Date(simulatedTime);

            // Process city builds
            for (City city : cityDao.getCities(game)) {
                long period = UpdateCalculator.shrinkTime(true, city.getUpdatePeriodMilliseconds());
                if (period > 0 && city.getLastUpdated() != null) {
                    long elapsed = simulatedTime - city.getLastUpdated().getTime();
                    if (elapsed >= period) {
                        try {
                            cityBuilderService.buildUnit(city, simDate);
                        } catch (Exception e) {
                            logger.debug("City build failed: {}", e.getMessage());
                        }
                    }
                }
            }

            // Process unit updates
            for (Unit unit : unitDao.getUnits(game)) {
                if (!unit.isAlive()) continue;
                long period = UpdateCalculator.shrinkTime(true, unit.getUpdatePeriodMilliseconds());
                if (period > 0 && unit.getLastUpdated() != null) {
                    long elapsed = simulatedTime - unit.getLastUpdated().getTime();
                    if (elapsed >= period) {
                        try {
                            unitService.updateUnit(unit, simDate);
                        } catch (Exception e) {
                            logger.debug("Unit update failed: {}", e.getMessage());
                        }
                    }
                }
            }

            // Update game (tech, command points)
            try {
                gameService.updateGame(game, simDate);
            } catch (Exception e) {
                logger.debug("Game update failed: {}", e.getMessage());
            }

            // Execute bot turns
            for (Map.Entry<String, Nation> entry : nations.entrySet()) {
                String playerName = entry.getKey();
                Nation nation = entry.getValue();
                BotWeights weights = playerWeights.get(playerName);
                try {
                    botExecutor.executeTurn(nation, weights, simulatedTime);
                } catch (Exception e) {
                    logger.debug("Bot turn failed for {}: {}", playerName, e.getMessage());
                }
            }

            turnsPlayed++;
        }

        // Score all nations
        Map<String, Double> scores = new LinkedHashMap<>();
        for (Map.Entry<String, Nation> entry : nations.entrySet()) {
            scores.put(entry.getKey(), scorer.score(entry.getValue()));
        }

        // Cleanup
        try {
            dataCache.remove(game);
        } catch (Exception e) {
            logger.debug("Cache cleanup failed: {}", e.getMessage());
        }

        return new TrainingGameResult(scores, playerWeights, turnsPlayed);
    }

    private long computeTickInterval(Game game) {
        // Use the game's tech update interval (15 min real → ~1.5s blitz)
        // But for training, use a reasonable tick that captures both unit and city updates
        // Blitz shrink: 10 days → 2 hours, factor = 120
        long unitPeriod = UpdateCalculator.shrinkTime(true,
                com.kenstevens.stratinit.type.Constants.HOURS_BETWEEN_UNIT_UPDATES * org.apache.commons.lang3.time.DateUtils.MILLIS_PER_HOUR);
        long gamePeriod = UpdateCalculator.shrinkTime(true,
                com.kenstevens.stratinit.type.Constants.TECH_UPDATE_INTERVAL_SECONDS * 1000L);
        // Use the smaller of the two as our tick
        return Math.min(unitPeriod, gamePeriod);
    }

    public void cleanup(Game game) {
        try {
            dataCache.remove(game);
            gameDao.removeGame(game.getId());
        } catch (Exception e) {
            logger.warn("Failed to cleanup training game: {}", e.getMessage());
        }
    }
}
