package com.kenstevens.stratinit.server.service;

import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Player;
import com.kenstevens.stratinit.client.util.GameScheduleHelper;
import com.kenstevens.stratinit.config.IServerConfig;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.NationDao;
import com.kenstevens.stratinit.dao.PlayerDao;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.event.svc.GameStartupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BotService {
    private static final String[] BOT_NAMES = {
            "Bot Alpha", "Bot Bravo", "Bot Charlie", "Bot Delta",
            "Bot Echo", "Bot Foxtrot", "Bot Golf", "Bot Hotel",
            "Bot India", "Bot Juliet"
    };

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PlayerDao playerDao;
    @Autowired
    private GameDao gameDao;
    @Autowired
    private NationDao nationDao;
    @Autowired
    private GameService gameService;
    @Autowired
    private GameStartupService gameStartupService;
    @Autowired
    private IServerConfig serverConfig;

    public Result<Nation> addBotToGame(int gameId) {
        Game game = gameDao.findGame(gameId);
        if (game == null) {
            return new Result<>("No game with id [" + gameId + "].", false);
        }
        if (game.isMapped()) {
            return new Result<>("Game has already started.", false);
        }
        if (game.isFullyBooked()) {
            return new Result<>("Game is full.", false);
        }

        int botIndex = findNextBotIndex(game);
        if (botIndex < 0) {
            return new Result<>("No more bot slots available.", false);
        }

        String botName = BOT_NAMES[botIndex];
        Player botPlayer = getOrCreateBotPlayer(botName);
        Result<Nation> result = gameService.joinGame(botPlayer, gameId, false);
        if (!result.isSuccess()) {
            return result;
        }

        logger.info("Bot '{}' joined game #{}", botName, gameId);

        // Reload game after join (player count may have changed)
        game = gameDao.findGame(gameId);

        // Once we have enough players, start the bot game immediately
        // (skip the normal scheduling delay)
        if (game.getPlayers() >= serverConfig.getMinPlayersToSchedule() && !game.isMapped()) {
            startBotGameImmediately(game);
        }

        return result;
    }

    private void startBotGameImmediately(Game game) {
        logger.info("Starting bot game #{} immediately", game.getId());
        // Set start time to now so the game starts right away
        java.util.Date now = new java.util.Date();
        GameScheduleHelper.setStartTime(game, now);
        game.setLastUpdated(now);
        gameDao.merge(game);
        // Map the game (creates the world, assigns islands)
        gameService.mapGame(game);
        // Start the game (schedules all game events including BotTurnEvent)
        gameStartupService.startGame(game, true);
    }

    private int findNextBotIndex(Game game) {
        List<Nation> nations = nationDao.getNations(game);
        for (int i = 0; i < BOT_NAMES.length; i++) {
            String botName = BOT_NAMES[i];
            boolean alreadyInGame = nations.stream()
                    .anyMatch(n -> botName.equals(n.getPlayer().getUsername()));
            if (!alreadyInGame) {
                return i;
            }
        }
        return -1;
    }

    private Player getOrCreateBotPlayer(String botName) {
        Player existing = playerDao.find(botName);
        if (existing != null) {
            return existing;
        }
        Player botPlayer = Player.makeBotPlayer(botName);
        playerDao.save(botPlayer);
        logger.info("Created bot player '{}'", botName);
        return botPlayer;
    }

    public boolean gameHasBots(Game game) {
        List<Nation> nations = nationDao.getNations(game);
        return nations.stream().anyMatch(n -> n.getPlayer().isBot());
    }
}
