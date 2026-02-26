package com.kenstevens.stratinit.controller;

import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.cache.GameCache;
import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.UnitBase;
import com.kenstevens.stratinit.client.rest.SIRestPaths;
import com.kenstevens.stratinit.config.IServerConfig;
import com.kenstevens.stratinit.config.RunModeEnum;
import com.kenstevens.stratinit.dto.*;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.exception.StratInitException;
import com.kenstevens.stratinit.remote.request.ErrorJson;
import com.kenstevens.stratinit.remote.request.SetGameJson;
import com.kenstevens.stratinit.server.service.BotService;
import com.kenstevens.stratinit.server.service.GameService;
import com.kenstevens.stratinit.server.rest.request.RequestProcessor;
import com.kenstevens.stratinit.server.rest.request.WriteProcessor;
import com.kenstevens.stratinit.server.rest.session.StratInitSessionManager;
import com.kenstevens.stratinit.server.rest.svc.ErrorProcessor;
import com.kenstevens.stratinit.server.rest.svc.NationSvc;
import com.kenstevens.stratinit.server.rest.svc.PlayerWorldViewUpdate;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.UnitType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = SIRestPaths.BASE_PATH)
@Tag(name = "Game Management")
public class GameController {
    @Autowired
    private RequestProcessor requestProcessor;
    @Autowired
    private WriteProcessor writeProcessor;
    @Autowired
    private ErrorProcessor errorProcessor;
    @Autowired
    private IServerConfig serverConfig;
    @Autowired
    private NationSvc nationSvc;
    @Autowired
    private GameService gameService;
    @Autowired
    private BotService botService;
    @Autowired
    private PlayerWorldViewUpdate playerWorldViewUpdate;
    @Autowired
    private DataCache dataCache;
    @Autowired
    private StratInitSessionManager sessionManager;

    @Value("${stratinit.version:1.2.8}")
    private String serverVersion;

    @Autowired(required = false)
    private org.springframework.boot.info.GitProperties gitProperties;

    @GetMapping(path = SIRestPaths.VERSION)
    @Operation(summary = "Get server version")
    public String getVersion() {
        if (gitProperties == null) {
            return serverVersion;
        }
        String commitId = gitProperties.getShortCommitId();
        String branch = gitProperties.getBranch();
        String dirty = "true".equals(gitProperties.get("dirty")) ? "-dirty" : "";
        return serverVersion + " (" + branch + "@" + commitId + dirty + ")";
    }

    @GetMapping(path = SIRestPaths.UNIT_BASE)
    @Operation(summary = "Get all unit type definitions and stats")
    public List<SIUnitBase> getUnitBases() {
        return Arrays.stream(UnitType.values())
                .map(unitType -> new SIUnitBase(UnitBase.getUnitBase(unitType)))
                .collect(Collectors.toList());
    }

    @GetMapping(path = SIRestPaths.SERVER_CONFIG)
    @Operation(summary = "Get server configuration properties")
    public Properties getServerConfig() {
        Properties properties = new Properties();
        Class<Constants> clazz = Constants.class;
        for (Field field : clazz.getFields()) {
            try {
                properties.setProperty(field.getName(), field.get(clazz)
                        .toString());
            } catch (IllegalAccessException ex) {
                // won't happen
            }
        }
        properties.setProperty(RunModeEnum.class.getSimpleName(), serverConfig.getRunMode().toString());
        properties.setProperty("SERVER_VERSION", serverVersion);
        return properties;
    }

    @PostMapping(path = SIRestPaths.SET_GAME)
    @Operation(summary = "Set the active game for the current player")
    public void setGame(@Valid @RequestBody SetGameJson request) {
        writeProcessor.processForGame(request.gameId, session -> {
            GameCache gameCache = dataCache.getGameCache(request.gameId);
            if (gameCache == null) {
                throw new StratInitException("The game " + request.gameId + " does not exist.");
            }
            Game game = gameCache.getGame();
            if (game == null) {
                throw new StratInitException("The game " + request.gameId + " does not exist.");
            }
            if (game.hasEnded()) {
                throw new StratInitException("The game " + request.gameId + " has ended.");
            }
            if (!game.isMapped()) {
                throw new StratInitException("The game " + request.gameId + " is not open yet.");
            }
            Nation nation = sessionManager.setNation(session.getPlayer(), request.gameId);
            if (nation == null || dataCache.getGameCache(nation.getGame()) == null) {
                throw new StratInitException("You have not joined game #" + request.gameId + " (This error should never happen!)");
            }
            if (!game.hasStarted()) {
                nation.setNoAlliances(request.noAlliances);
                gameService.merge(nation);
                gameService.calculateAllianceVote(game);
                gameService.merge(game);
            }
            return Result.trueInstance();
        });
    }

    @PostMapping(path = SIRestPaths.JOIN_GAME)
    @Operation(summary = "Join an existing game")
    public SINation joinGame(@Valid @RequestBody SetGameJson request) {
        return writeProcessor.processForGame(request.gameId, session -> {
            Result<Nation> result = gameService.joinGame(session.getPlayer(), request.gameId, request.noAlliances);
            if (!result.isSuccess()) {
                return new Result<SINation>(result);
            }
            Nation nation = result.getValue();
            SINation siNation = nationSvc.nationToSINation(nation, nation, false, false);
            return new Result<>(result, siNation);
        });
    }

    @GetMapping(path = SIRestPaths.GAME_JOINED)
    @Operation(summary = "Get games the current player has joined")
    public List<SIGame> getJoinedGames() {
        return requestProcessor.processNoGame(player -> nationSvc.getJoinedGames(player));
    }

    @GetMapping(path = SIRestPaths.GAME_UNJOINED)
    @Operation(summary = "Get available games the player can join")
    public List<SIGame> getUnjoinedGames() {
        return requestProcessor.processNoGame(player ->
                gameService.getUnjoinedGames(player).stream()
                        .map(game -> new SIGame(game, false))
                        .collect(Collectors.toList()));
    }

    @GetMapping(path = SIRestPaths.UPDATE)
    @Operation(summary = "Get full world view update for current game")
    public SIUpdate getUpdate() {
        return requestProcessor.process(nation -> playerWorldViewUpdate.getWorldViewUpdate(nation));
    }

    @GetMapping(path = SIRestPaths.CONCEDE)
    @Operation(summary = "Concede from the current game")
    public SIUpdate concede() {
        return writeProcessor.process(nation -> nationSvc.concede(nation), 0);
    }

    @PostMapping(path = SIRestPaths.SUBMIT_ERROR)
    @Operation(summary = "Submit a client-side error report")
    public Integer submitError(@Valid @RequestBody ErrorJson request) {
        return errorProcessor.processError(request.subject, request.stackTrace);
    }

    @PostMapping(path = SIRestPaths.CREATE_BLITZ)
    @Operation(summary = "Create a blitz game with 7 bots and start immediately")
    public SIGame createBlitz() {
        return requestProcessor.processNoGame(player -> {
            Game game = gameService.createGame("Blitz-" + player.getUsername());
            game.setBlitz(true);
            game.setIslands(8);
            gameService.merge(game);
            Result<Nation> joinResult = gameService.joinGame(player, game.getId(), false);
            if (!joinResult.isSuccess()) {
                throw new StratInitException("Failed to join game: " + joinResult.toString());
            }
            for (int i = 0; i < 7; i++) {
                Result<Nation> botResult = botService.addBotToGame(game.getId(), false);
                if (!botResult.isSuccess()) {
                    throw new StratInitException("Failed to add bot: " + botResult.toString());
                }
            }
            // Start the game after all bots have joined
            game = dataCache.getGameCache(game.getId()).getGame();
            botService.startBotGameImmediately(game);
            Game updatedGame = dataCache.getGameCache(game.getId()).getGame();
            return new SIGame(updatedGame, false);
        });
    }

    @PostMapping(path = SIRestPaths.ADD_BOT)
    @Operation(summary = "Add a bot player to a game lobby")
    public SINation addBot(@Valid @RequestBody SetGameJson request) {
        return writeProcessor.processForGame(request.gameId, session -> {
            Result<Nation> result = botService.addBotToGame(request.gameId);
            if (!result.isSuccess()) {
                return new Result<SINation>(result);
            }
            Nation nation = result.getValue();
            SINation siNation = nationSvc.nationToSINation(nation, nation, false, false);
            return new Result<>(result, siNation);
        });
    }
}
