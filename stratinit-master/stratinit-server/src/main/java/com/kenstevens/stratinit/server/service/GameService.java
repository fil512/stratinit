package com.kenstevens.stratinit.server.service;

import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Player;
import com.kenstevens.stratinit.client.model.World;
import com.kenstevens.stratinit.client.util.GameScheduleHelper;
import com.kenstevens.stratinit.config.IServerConfig;
import com.kenstevens.stratinit.client.model.audit.GameNationSnapshot;
import com.kenstevens.stratinit.dao.*;
import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.repo.GameNationSnapshotRepo;
import com.kenstevens.stratinit.type.GameEventType;
import com.kenstevens.stratinit.server.event.svc.EventQueue;
import com.kenstevens.stratinit.server.rest.mail.MailService;
import com.kenstevens.stratinit.server.rest.mail.MailTemplateLibrary;
import com.kenstevens.stratinit.server.svc.FogService;
import com.kenstevens.stratinit.server.svc.GameCreator;
import com.kenstevens.stratinit.server.svc.TeamCalculator;
import com.kenstevens.stratinit.server.svc.WorldManager;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.world.GameSizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameService {
    private final IServerConfig serverConfig;
    @Autowired
    private GameDao gameDao;
    @Autowired
    private NationDao nationDao;
    @Autowired
    private SectorDao sectorDao;
    @Autowired
    private CityDao cityDao;
    @Autowired
    private UnitDao unitDao;
    @Autowired
    private RelationService relationService;
    @Autowired
    private CityService cityService;
    @Autowired
    private UnitService unitService;
    @Autowired
    private EventQueue eventQueue;
    @Autowired
    private TeamCalculator teamCalculator;
    @Autowired
    private MailService mailService;
    @Autowired
    private WorldManager worldManager;
    @Autowired
    private GameCreator gameCreator;
    // FIXME too many collaborators
    @Autowired
    private PlayerDao playerDao;
    @Autowired
    private FogService fogService;
    @Autowired
    private GameNationSnapshotRepo gameNationSnapshotRepo;
    @Autowired
    private EventLogService eventLogService;

    @Autowired
    public GameService(IServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public Game createGame(String name) {
        Game game = new Game(name);
        gameDao.save(game);
        return game;
    }

    public Game createBlitzGame(String name, int islands) {
        Game game = new Game(name);
        game.setBlitz(true);
        game.setIslands(islands);
        gameDao.save(game);
        GameScheduleHelper.setStartTimeBasedOnNow(game, serverConfig.getScheduledToStartedMillis());
        mapGame(game);
        return game;
    }

    public void scheduleGame(Game game) {
        if (game.getStartTime() != null) {
            return;
        }
        GameScheduleHelper.setStartTimeBasedOnNow(game, serverConfig.getScheduledToStartedMillis());
        gameDao.merge(game);
        gameDao.flush(); // So the timer can read the game
        eventQueue.schedule(game, false);
        Date started = game.getStartTime();
        if (started != null) {
            for (Nation nation : nationDao.getNations(game)) {
                Player player = nation.getPlayer();
                if (!player.isBot()) {
                    mailService.sendEmail(player, MailTemplateLibrary.getGameScheduled(player, game, serverConfig));
                }
            }
        }
    }

    public void mapGame(Game game) {
        if (game.getStartTime() == null) {
            throw new IllegalStateException("Game " + game.getGamename()
                    + " can not be mapped.  It has not been started yet.");
        }
        if (game.isMapped() || game.getMapped() != null) {
            throw new IllegalStateException("Game " + game.getGamename()
                    + " is already mapped.  It cannot be mapped again.");
        }
        if (game.isBlitz()) {
            if (game.getIslands() <= 0) {
                throw new IllegalStateException("Blitz game " + game.getGamename()
                        + " may not be mapped.  It has no islands.");
            }
        } else {
            if (game.getPlayers() <= 0) {
                throw new IllegalStateException("Game " + game.getGamename()
                        + " may not be mapped.  It has no players.");
            }
        }
        if (!game.isBlitz() || game.getGamesize() <= 0) {
            GameSizer.setIslands(game);
            GameSizer.setSize(game);
        }
        World world = createWorld(game);
        sectorDao.save(world);
        game.setMapped();
        List<Nation> nations = nationDao.getNations(game);
        int island = 0;
        for (Nation nation : nations) {
            worldManager.addPlayerToMap(island, nation);
            ++island;
        }
        setNoAlliances(game);
        gameDao.merge(game);
        gameDao.flush();
        gameCreator.createGameIfAllMapped();
    }

    public Result<Nation> joinGame(Player player, int gameId, boolean noAlliances) {
        Game game = gameDao.findGame(gameId);
        if (game == null) {
            return new Result<>("no game with id [" + gameId + "].",
                    false);
        }
        Nation nation = nationDao.findNation(game, player);
        if (nation != null) {
            return new Result<>("already joined [" + gameId + "].", false);
        }
        if (game.isFullyBooked()) {
            return new Result<>("game is full.", false);
        }
        int nationId = game.addPlayer(noAlliances);
        nation = new Nation(game, player);
        nation.setNoAlliances(noAlliances);
        nation.setNationId(nationId);
        nationDao.save(nation);
        initCommandPointGain(nation);
        calculateAllianceVote(game);
        gameDao.merge(game);
        relationService.setRelations(nation);
        if (game.isMapped()) {
            worldManager.addPlayerToMap(nationId, nation);
            gameDao.flush();
        } else if (game.getPlayers() >= serverConfig.getMinPlayersToSchedule()) {
            scheduleGame(game);
        }
        return new Result<>(nation);
    }

    public void calculateAllianceVote(Game game) {
        int noAlliancesVote = 0;
        for (Nation nation : nationDao.getNations(game)) {
            if (nation.isNoAlliances()) {
                ++noAlliancesVote;
            }
        }
        game.setNoAlliancesVote(noAlliancesVote);
    }


    private World createWorld(Game game) {
        return worldManager.build(game);
    }

    public List<Game> getUnjoinedGames(Player player) {
        return gameDao.getUnjoinedGames(player);
    }

    public Game findGame(int gameId) {
        return gameDao.findGame(gameId);
    }

    public void removeGame(int gameId) {
        gameDao.removeGame(gameId);
    }


    private void updateTech(Game game, Date lastUpdated) {
        List<Nation> nations = nationDao.getNations(game);
        double maxTech = getMaxTech(nations);
        for (Nation nation : nations) {
            updateTech(maxTech, nation, lastUpdated);
        }

    }

    // FIXME move this and others to a NationService
    private void updateTech(double maxTech, Nation nation, Date lastUpdated) {
        int techCentres = (int) cityDao.getNumberOfTechCentres(nation);
        if (techCentres >= Constants.TECH_INCREASE_DAILY_BY_NUM_TECH_CENTRES.length) {
            techCentres = Constants.TECH_INCREASE_DAILY_BY_NUM_TECH_CENTRES.length - 1;
        }
        double allyTech = getMaxTech(relationService.getAllies(nation));
        double nationTech = nation.getTech();
        double otherTechBleed = (maxTech - nationTech)
                / Constants.OTHER_TECH_BLEED;
        double allyTechBleed = (allyTech - nationTech)
                / Constants.ALLY_TECH_BLEED;
        double dailyTechBleed = Math.max(otherTechBleed, allyTechBleed);
        nation.setDailyTechBleed(dailyTechBleed);
        double dailyTechGain = Constants.TECH_INCREASE_DAILY_BY_NUM_TECH_CENTRES[techCentres];
        nation.setDailyTechGain(dailyTechGain);
        double totalDailyTechGain = dailyTechBleed + dailyTechGain;
        int secondsPerDay = Constants.SECONDS_PER_DAY;
        double updatesPerDay = (double) secondsPerDay
                / Constants.TECH_UPDATE_INTERVAL_SECONDS;
        double netTechGain = totalDailyTechGain / updatesPerDay;
        nation.increaseTech(netTechGain);
        double techAfter = nation.getTech();
        nationDao.markCacheModified(nation);
        if (crossedThreshold(nationTech, techAfter)) {
            cityService
                    .switchCityBuildsFromTechChange(nation, lastUpdated);
            unitService.updateCarrierRadar(nation);
            fogService.survey(nation);
        }
    }

    private boolean crossedThreshold(double nationTech, double techAfter) {
        return Math.floor(nationTech) != Math.floor(techAfter);
    }

    private void setGameLastUpdated(Game game, Date lastUpdated) {
        game.setLastUpdated(lastUpdated);
        gameDao.merge(game);

    }

    private void initCommandPointGain(Nation nation) {
        int cpGain = capitalPointsToCommandPoints(getCapitalPoints(nation));
        nation.setHourlyCPGain(cpGain * 3600 / Constants.TECH_UPDATE_INTERVAL_SECONDS);
        nationDao.markCacheModified(nation);
    }

    private void updateCommandPoints(Game game) {
        List<Nation> nations = nationDao.getNations(game);
        for (Nation nation : nations) {
            int cpGain = capitalPointsToCommandPoints(getCapitalPoints(nation));
            nation.increaseCommandPoints(cpGain);
            nation.setHourlyCPGain(cpGain * 3600 / Constants.TECH_UPDATE_INTERVAL_SECONDS);
            nationDao.markCacheModified(nation);
        }
    }

    private int capitalPointsToCommandPoints(int capitalPoints) {
        int cpGain = capitalPoints / Constants.COMMAND_POINT_FACTOR;
        if (cpGain < Constants.MIN_COMMAND_POINTS_GAINED_PER_TICK) {
            cpGain = Constants.MIN_COMMAND_POINTS_GAINED_PER_TICK;
        }
        if (cpGain > Constants.MAX_COMMAND_POINTS_GAINED_PER_TICK) {
            cpGain = Constants.MAX_COMMAND_POINTS_GAINED_PER_TICK;
        }
        return cpGain;
    }


    public int getCapitalPoints(Nation nation) {
        int cities = cityDao.getNumberOfCities(nation);
        int capitalShips = unitDao.getNumberOfCapitalShips(nation);
        int bases = cityDao.getNumberOfBases(nation);
        return cities + capitalShips + bases * 2;
    }

    private double getMaxTech(Collection<Nation> collection) {
        double retval = 0.0;
        for (Nation nation : collection) {
            if (nation.getTech() > retval) {
                retval = nation.getTech();
            }
        }
        return retval;
    }

    public void disable(Game game) {
        eventQueue.cancel(game);
        game.setEnabled(false);
        gameDao.merge(game);
    }

    // TODO record more about the game
    public void score(Game game) {
        Map<Nation, Integer> score = new HashMap<>();
        int topScore = 0;
        List<Nation> nations = nationDao.getNations(game);
        for (Nation nation : nations) {
            if (score.get(nation) != null) {
                continue;
            }
            Collection<Nation> allies = relationService.getAllies(nation);
            int cities = 0;
            cities += cityDao.getNumberOfCities(nation);
            for (Nation ally : allies) {
                cities += cityDao.getNumberOfCities(ally);
            }
            score.put(nation, cities);
            for (Nation ally : allies) {
                score.put(ally, cities);
            }
            if (cities > topScore) {
                topScore = cities;
            }
        }

        for (Nation nation : nations) {
            Player player = playerDao.find(nation.getPlayer().getId());
            if (player.isBot()) {
                continue;
            }
            player.addPlayed();
            if (score.get(nation) == topScore) {
                player.addWins();
            }
            playerDao.saveAndUpdateNations(player);
        }
    }

    public void merge(Nation nation) {
        nationDao.markCacheModified(nation);
    }

    public void updateGame(Game game, Date lastUpdated) {
        updateCommandPoints(game);
        updateTech(game, lastUpdated);
        setGameLastUpdated(game, lastUpdated);
        takeSnapshots(game, lastUpdated);
        int tickNumber = computeTickNumber(game, lastUpdated);
        eventLogService.logServerEvent(game.getId(), null, GameEventType.TECH_UPDATE,
                "Tick " + tickNumber + " completed");
    }

    private void takeSnapshots(Game game, Date lastUpdated) {
        if (com.kenstevens.stratinit.dao.CacheDao.isTrainingMode()) {
            return;
        }
        int tickNumber = computeTickNumber(game, lastUpdated);
        List<Nation> nations = nationDao.getNations(game);
        for (Nation nation : nations) {
            int cities = cityDao.getNumberOfCities(nation);
            int power = unitService.getPower(nation);
            double tech = nation.getTech();
            int commandPoints = nation.getCommandPoints();
            int capitalPoints = getCapitalPoints(nation);
            GameNationSnapshot snapshot = new GameNationSnapshot(
                    game.getId(), nation.getName(), lastUpdated, tickNumber,
                    cities, power, tech, commandPoints, capitalPoints);
            gameNationSnapshotRepo.save(snapshot);
        }
    }

    private int computeTickNumber(Game game, Date lastUpdated) {
        Date startTime = game.getStartTime();
        if (startTime == null) {
            return 0;
        }
        long elapsed = lastUpdated.getTime() - startTime.getTime();
        return (int) (elapsed / (Constants.TECH_UPDATE_INTERVAL_SECONDS * 1000L));
    }

    public List<SITeam> getTeams(Game game) {
        return teamCalculator.getTeams(game);
    }

    public void setNoAlliances(Game game) {
        if (game.getNoAlliancesVote() > game.getPlayers() / 2) {
            game.setNoAlliances(true);
        }
        gameDao.merge(game);
    }

    public void merge(Game game) {
        gameDao.merge(game);
    }
}
