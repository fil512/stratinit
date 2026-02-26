package com.kenstevens.stratinit.server.event.svc;

import com.google.common.annotations.VisibleForTesting;
import com.kenstevens.stratinit.client.event.EventScheduler;
import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.util.UpdateManager;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.server.service.GameService;
import com.kenstevens.stratinit.server.rest.state.ServerStatus;
import com.kenstevens.stratinit.server.svc.GameCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.nio.channels.FileLock;
import java.util.Date;
import java.util.List;

@Service
public class EventSchedulerImpl implements EventScheduler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private EventQueue eventQueue;
    @Autowired
    private GameDao gameDao;
    @Autowired
    private GameService gameService;
    @Autowired
    private GameEnder gameEnder;
    @Autowired
    private ServerStatus serverStatus;
    @Autowired
    private GameCreator gameCreator;
    @Autowired
    private EventTimer eventTimer;
    @Autowired
    private GameStartupService gameStartupService;

    @Value("${stratinit.scheduler.enabled}")
    private Boolean schedulerEnabled;

    private FileLock fileLock;

    @PostConstruct
    public void scheduleInitialEvents() {
        if (!schedulerEnabled) {
            logger.info("Scheduler disabled.");
            serverStatus.setRunning();
            return;
        }

        ServerLocker serverLocker = new ServerLocker();
        fileLock = serverLocker.getLock();
        if (fileLock == null) {
            if (eventTimer.isStarted()) {
                logger.warn("EventScheduler already running.  Skipping startup.\n");
            } else {
                logger.error("Unable to start scheduler.  Is another server already running?");
            }
            serverStatus.setRunning();
            return;
        }

        if (eventTimer.isStarted()) {
            logger.info("EventScheduler already running.  Skipping startup.\n");
            return;
        }
        logger.info("Server Lock File locked.\n");

        updateGamesAndStartTimer();
    }

    @VisibleForTesting
    @Override
    public void updateGamesAndStartTimer() {
        logger.debug("Event Queue starting up.");
        updateGames();
        eventQueue.scheduleFlushCache();
        gameCreator.createGameIfAllMapped();
        logger.debug("All events scheduled.  Server started.");
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
            logger.debug("Updating technology.");

            UpdateManager updateManager = new UpdateManager(game);
            while (updateManager.missedUpdates() > 0) {
                Date nextMissedBuildTime = updateManager
                        .getNextMissedBuildTime();
                gameService.updateGame(game, nextMissedBuildTime);
            }
            gameStartupService.startGame(game, false);
        }
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
