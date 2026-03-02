package com.kenstevens.stratinit.server.bot.training;

import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.client.util.GameScheduleHelper;
import com.kenstevens.stratinit.client.util.UpdateCalculator;
import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.dao.CacheDao;
import com.kenstevens.stratinit.dao.CityDao;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.NationDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.type.BotPersonality;
import com.kenstevens.stratinit.type.UnitType;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.bot.BotExecutor;
import com.kenstevens.stratinit.server.bot.PhasedBotWeights;
import com.kenstevens.stratinit.server.event.svc.GameStartupService;
import com.kenstevens.stratinit.server.service.GameService;
import com.kenstevens.stratinit.server.service.UnitService;
import com.kenstevens.stratinit.server.svc.CityBuilderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class TrainingGameSimulator {
    // 2880 ticks = full blitz game (2 hours real time, bot turn every 2.5 sec)
    private static final int MAX_TURNS = 2880;
    private static final int PLAYERS_PER_GAME = 8;
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
    private DataCache dataCache;
    @Autowired
    private com.kenstevens.stratinit.dao.PlayerDao playerDao;

    public TrainingGameResult simulate(Map<String, PhasedBotWeights> playerWeights) {
        return simulate(playerWeights, Collections.emptyMap());
    }

    public TrainingGameResult simulate(Map<String, PhasedBotWeights> playerWeights, Map<String, BotPersonality> playerPersonalities) {
        CacheDao.setTrainingMode(true);
        CacheDao.resetSyntheticIds();
        try {
            return doSimulate(playerWeights, playerPersonalities);
        } finally {
            CacheDao.setTrainingMode(false);
        }
    }

    private TrainingGameResult doSimulate(Map<String, PhasedBotWeights> playerWeights, Map<String, BotPersonality> playerPersonalities) {
        long simStartTime = System.nanoTime();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        String gameName = TRAINING_GAME_PREFIX + System.currentTimeMillis();

        // Create game and players
        Game game = new Game(gameName);
        game.setBlitz(true);
        game.setIslands(PLAYERS_PER_GAME);
        game.setGamesize(60);
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
            Nation nation = result.getValue();
            BotPersonality personality = playerPersonalities.get(name);
            if (personality != null) {
                nation.setBotPersonality(personality);
            }
            nations.put(name, nation);
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

        // Timing accumulators (nanoseconds)
        long cityBuildNs = 0, unitUpdateNs = 0, gameUpdateNs = 0, botTurnNs = 0, metricsNs = 0;
        long setupNs = System.nanoTime() - simStartTime;
        logger.info("  Setup time:   {} ms", setupNs / 1_000_000);
        botExecutor.resetTimers();

        for (int turn = 0; turn < MAX_TURNS; turn++) {
            simulatedTime += tickInterval;
            Date simDate = new Date(simulatedTime);

            long t0 = System.nanoTime();
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
            long t1 = System.nanoTime();
            cityBuildNs += t1 - t0;

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
            long t2 = System.nanoTime();
            unitUpdateNs += t2 - t1;

            // Update game (tech, command points)
            try {
                gameService.updateGame(game, simDate);
            } catch (Exception e) {
                logger.debug("Game update failed: {}", e.getMessage());
            }
            long t3 = System.nanoTime();
            gameUpdateNs += t3 - t2;

            // Prepare all bot turns in parallel (read-only phase)
            long tb0 = System.nanoTime();
            List<Map.Entry<String, Nation>> nationEntries = new ArrayList<>(nations.entrySet());
            List<CompletableFuture<BotExecutor.PreparedTurn>> futures = new ArrayList<>();
            for (Map.Entry<String, Nation> entry : nationEntries) {
                Nation nation = entry.getValue();
                PhasedBotWeights weights = playerWeights.get(entry.getKey());
                long simTimeCapture = simulatedTime;
                futures.add(CompletableFuture.supplyAsync(
                        () -> botExecutor.prepareTurn(nation, weights, simTimeCapture), executor));
            }
            List<BotExecutor.PreparedTurn> preparedTurns = futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());
            long tb1 = System.nanoTime();
            botTurnNs += tb1 - tb0;

            // Execute actions sequentially + collect metrics + detect milestones
            World simWorld = sectorDao.getWorld(game);
            for (int i = 0; i < nationEntries.size(); i++) {
                String playerName = nationEntries.get(i).getKey();
                Nation nation = nationEntries.get(i).getValue();
                BotExecutor.PreparedTurn prepared = preparedTurns.get(i);

                long tm0 = System.nanoTime();
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
                long tm1 = System.nanoTime();
                metricsNs += tm1 - tm0;

                long te0 = System.nanoTime();
                if (prepared != null) {
                    try {
                        botExecutor.executeActions(prepared, actionLog);
                    } catch (Exception e) {
                        logger.debug("Bot turn failed for {}: {}", playerName, e.getMessage());
                    }
                }
                long te1 = System.nanoTime();
                botTurnNs += te1 - te0;

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

        executor.shutdown();

        logger.info("=== Simulation Timing (1 game, {} turns) ===", turnsPlayed);
        logger.info("  City builds:  {} ms", cityBuildNs / 1_000_000);
        logger.info("  Unit updates: {} ms", unitUpdateNs / 1_000_000);
        logger.info("  Game updates: {} ms", gameUpdateNs / 1_000_000);
        logger.info("  Bot turns:    {} ms", botTurnNs / 1_000_000);
        logger.info("  Metrics:      {} ms", metricsNs / 1_000_000);
        logger.info("  Total sim:    {} ms", (cityBuildNs + unitUpdateNs + gameUpdateNs + botTurnNs + metricsNs) / 1_000_000);
        botExecutor.logTimers();

        // Score all nations using relative power ranking
        Map<String, Double> scores = scorer.scoreAll(nations);

        // Cleanup game from cache and database to prevent OOM across generations
        cleanup(game, playerNames);

        return new TrainingGameResult(scores, playerWeights, turnsPlayed, actionLog);
    }

    private long computeTickInterval(Game game) {
        // Use the bot turn interval as the tick — this is the most frequent scheduled event
        // Blitz shrink: 5 min real → 2.5 sec blitz
        long botPeriod = UpdateCalculator.shrinkTime(true,
                com.kenstevens.stratinit.type.Constants.BOT_TURN_INTERVAL_SECONDS * 1000L);
        return botPeriod;
    }

    public void cleanup(Game game, List<String> playerNames) {
        try {
            gameDao.remove(game);
            // Remove synthetic players from cache
            for (String name : playerNames) {
                Player player = playerDao.find(name);
                if (player != null && player.getId() < 0) {
                    dataCache.remove(player);
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to cleanup training game: {}", e.getMessage());
        }
    }
}
