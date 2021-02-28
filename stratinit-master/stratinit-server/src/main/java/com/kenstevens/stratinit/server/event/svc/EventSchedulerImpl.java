package com.kenstevens.stratinit.server.event.svc;

import com.google.common.annotations.VisibleForTesting;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.event.EventScheduler;
import com.kenstevens.stratinit.model.*;
import com.kenstevens.stratinit.server.daoservice.*;
import com.kenstevens.stratinit.server.rest.state.ServerStatus;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.RunMode;
import com.kenstevens.stratinit.util.UpdateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.channels.FileLock;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class EventSchedulerImpl implements EventScheduler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private EventQueue eventQueue;
    @Autowired
    private SectorDao sectorDao;
    @Autowired
    private GameDaoService gameDaoService;
    @Autowired
    private UnitDaoService unitDaoService;
    @Autowired
    private SectorDaoService sectorDaoService;
    @Autowired
    private UnitDao unitDao;
    @Autowired
    private GameDao gameDao;
    @Autowired
    private GameEnder gameEnder;
    @Autowired
    private ServerStatus serverStatus;
    @Autowired
    private GameCreator gameCreator;
    @Autowired
    private CityBuilderService cityBuilderService;
    @Autowired
    private IntegrityCheckerService integrityCheckerService;
    @Autowired
    private EventTimer eventTimer;

    private FileLock fileLock;

    @PostConstruct
    public void scheduleInitialEvents() {
        // FIXME switch to Spring Profile
        Constants.setRunMode(RunMode.TEST);

        ServerLocker serverLocker = new ServerLocker();
        fileLock = serverLocker.getLock();
        if (fileLock == null) {
            if (eventTimer.isStarted()) {
                logger.warn("EventScheduler already running.  Skipping startup.\n");
            } else {
                logger.error("Unable to start scheduler.  Is another server already running?");
            }
            return;
        }
        updateGamesAndStartTimer();
    }

    @VisibleForTesting
    @Override
    public void updateGamesAndStartTimer() {
        if (eventTimer.isStarted()) {
            logger.info("EventScheduler already running.  Skipping startup.\n");
            return;
        }
        logger.info("Server Lock File locked.\n");
        logger.info("Event Queue starting up.");
        updateGames();
        eventQueue.scheduleFlushCache();
        gameCreator.createGameIfAllMapped();
        logger.info("All events scheduled.  Server started.");
        serverStatus.setRunning();
        eventTimer.start();
    }

    private void updateGames() {
        List<Game> games = gameDao.getAllGames();
        for (Game game : games) {
            if (game.hasEnded()) {
                logger.info("Game " + game + " that started on "
                        + game.getStartTimeString()
                        + " was scheduled to end on " + game.getEndsString()
                        + ".  Ending game.");
                gameEnder.endGame(game);
                continue;
            }
            if (!game.hasStarted()) {
                eventQueue.schedule(game, false);
                continue;
            }
            logger.info("Updating technology.");

            UpdateManager updateManager = new UpdateManager(game);
            while (updateManager.missedUpdates() > 0) {
                Date nextMissedBuildTime = updateManager
                        .getNextMissedBuildTime();
                gameDaoService.updateGame(game, nextMissedBuildTime);
            }
            startGame(game, false);
        }
    }

    public void startGame(Game game, boolean fromEvent) {
        logger.info("Starting up game " + game + ".");
        integrityCheck(game);
        updateCities(game, fromEvent);
        updateUnits(game);
        updateRelations(game);
        updateUnitSeen(game);
        survey(game);
        gameDaoService.setNoAlliances(game);
        eventQueue.schedule(game, true);
    }

    private void integrityCheck(Game game) {
        integrityCheckerService.checkAndFix(game);
    }

    private void survey(Game game) {
        List<Nation> nations = gameDao.getNations(game);
        for (Nation nation : nations) {
            sectorDaoService.survey(nation);
        }
    }

    private void updateCities(Game game, boolean fromEvent) {
        Collection<City> cities = sectorDao.getCities(game);
        logger.info("Updating " + cities.size() + " cities");
        for (City city : cities) {
            if (fromEvent) {
                city.setLastUpdated(game.getStartTime());
            } else {
                UpdateManager updateManager = new UpdateManager(city);
                while (updateManager.missedUpdates() > 0) {
                    Date nextMissedBuildTime = updateManager
                            .getNextMissedBuildTime();
                    cityBuilderService.buildUnit(city, nextMissedBuildTime);
                }
            }
            eventQueue.schedule(city);
        }
        logger.info(cities.size() + " cities scheduled.");
    }

    private void updateUnitSeen(Game game) {
        List<UnitSeen> unitsSeen = unitDao.getUnitsSeen(game);
        logger.info("Updating " + unitsSeen.size() + " units seen");
        int disabledCount = 0;
        for (UnitSeen unitSeen : unitsSeen) {
            if (badUnitSeen(unitSeen)) {
                ++disabledCount;
                unitDaoService.disable(unitSeen);
                continue;
            }
            eventQueue.schedule(unitSeen);
        }
        if (disabledCount > 0) {
            logger.info(disabledCount + " units seen disabled.");
        }
        logger.info(unitsSeen.size() + " unit disappearances scheduled.");
    }

    private boolean badUnitSeen(UnitSeen unitSeen) {
        Unit unit = unitSeen.getUnit();
        if (!unit.isAlive()) {
            return true;
        }
        return unit.getNation().equals(unitSeen.getNation());
    }

    private void updateRelations(Game game) {
        Collection<Relation> relations = gameDao.getAllChangingRelations(game);
        logger.info("Updating " + relations.size() + " relations");
        for (Relation relation : relations) {
            eventQueue.schedule(relation);
        }
        logger.info(relations.size() + " relation changes scheduled.");
    }

    private void updateUnits(Game game) {
        Collection<Unit> units = unitDao.getUnits(game);
        logger.info("Updating " + units.size() + " units");

        for (Unit unit : units) {
            UpdateManager updateManager = new UpdateManager(unit);
            while (updateManager.missedUpdates() > 0) {
                Date nextMissedBuildTime = updateManager
                        .getNextMissedBuildTime();
                unitDaoService.updateUnit(unit, nextMissedBuildTime);
            }
            eventQueue.schedule(unit);
        }
        logger.info(units.size() + " units scheduled.");
    }

    @PreDestroy
    public void shutdown() {
        if (fileLock != null) {
            ServerLocker serverLocker = new ServerLocker();
            serverLocker.releaseLock(fileLock);
            logger.info("Server Lock File released.\n");
        }
    }

}
