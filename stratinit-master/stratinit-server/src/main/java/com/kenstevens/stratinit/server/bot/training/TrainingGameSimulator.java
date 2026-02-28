package com.kenstevens.stratinit.server.bot.training;

import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.client.util.GameScheduleHelper;
import com.kenstevens.stratinit.client.util.UpdateCalculator;
import com.kenstevens.stratinit.dao.CityDao;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.NationDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.type.UnitType;
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
    private SectorDao sectorDao;
    @Autowired
    private UnitService unitService;
    @Autowired
    private CityBuilderService cityBuilderService;
    @Autowired
    private BotExecutor botExecutor;
    @Autowired
    private TrainingScorer scorer;
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

        // Set ends to match simulation duration so gameTimePercent progresses 0→1
        game.setEnds(new Date(pastStart.getTime() + MAX_TURNS * tickInterval));
        game.setLastUpdated(pastStart);
        gameDao.merge(game);

        // Simulation loop
        long simulatedTime = pastStart.getTime();
        int turnsPlayed = 0;
        TrainingActionLog actionLog = new TrainingActionLog();

        // Track previous city counts per nation for milestone detection
        Map<String, Integer> prevCityCounts = new LinkedHashMap<>();
        for (Map.Entry<String, Nation> entry : nations.entrySet()) {
            prevCityCounts.put(entry.getKey(), cityDao.getNumberOfCities(entry.getValue()));
        }

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

            // Process unit updates (copy to avoid ConcurrentModificationException)
            for (Unit unit : new ArrayList<>(unitDao.getUnits(game))) {
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

            // Execute bot turns with logging
            for (Map.Entry<String, Nation> entry : nations.entrySet()) {
                String playerName = entry.getKey();
                Nation nation = entry.getValue();
                BotWeights weights = playerWeights.get(playerName);

                // Record turn start metrics
                int cities = cityDao.getNumberOfCities(nation);
                List<Unit> nationUnits = unitDao.getUnits(nation);
                long aliveUnits = nationUnits.stream().filter(Unit::isAlive).count();
                int explored = sectorDao.getSectorsSeen(nation).size();
                double tech = nation.getTech();
                boolean hasTransport = nationUnits.stream()
                        .anyMatch(u -> u.isAlive() && u.getType() == UnitType.TRANSPORT);

                actionLog.recordTurnStart(playerName, turn,
                        new TrainingActionLog.TurnStateMetrics(cities, (int) aliveUnits, explored, tech, hasTransport));

                try {
                    botExecutor.executeTurn(nation, weights, simulatedTime, actionLog);
                } catch (Exception e) {
                    logger.debug("Bot turn failed for {}: {}", playerName, e.getMessage());
                }

                // Detect milestones
                int newCities = cityDao.getNumberOfCities(nation);
                if (newCities > prevCityCounts.getOrDefault(playerName, 0)) {
                    actionLog.recordMilestone(playerName, "firstNonHomeCityCapture", turn);
                }
                prevCityCounts.put(playerName, newCities);

                if (hasTransport) {
                    actionLog.recordMilestone(playerName, "firstTransportBuilt", turn);
                }

                // Detect engineer swim milestone: any live engineer on a water sector
                World simWorld = sectorDao.getWorld(game);
                boolean hasEngineerSwimming = nationUnits.stream()
                        .filter(u -> u.isAlive() && u.getType() == UnitType.ENGINEER)
                        .anyMatch(u -> {
                            Sector s = simWorld.getSectorOrNull(u.getCoords());
                            return s != null && s.isWater();
                        });
                if (hasEngineerSwimming) {
                    actionLog.recordMilestone(playerName, "firstEngineerSwim", turn);
                }
            }

            turnsPlayed++;
        }

        // Score all nations
        Map<String, Double> scores = new LinkedHashMap<>();
        for (Map.Entry<String, Nation> entry : nations.entrySet()) {
            scores.put(entry.getKey(), scorer.score(entry.getValue(), actionLog));
        }

        // Cleanup game from cache and database to prevent OOM across generations
        cleanup(game);

        return new TrainingGameResult(scores, playerWeights, turnsPlayed, actionLog);
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
            gameDao.remove(game);
        } catch (Exception e) {
            logger.warn("Failed to cleanup training game: {}", e.getMessage());
        }
    }
}
