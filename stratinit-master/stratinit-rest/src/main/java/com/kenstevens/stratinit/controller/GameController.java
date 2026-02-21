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
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.ErrorJson;
import com.kenstevens.stratinit.remote.request.SetGameJson;
import com.kenstevens.stratinit.server.service.GameService;
import com.kenstevens.stratinit.server.rest.request.RequestProcessor;
import com.kenstevens.stratinit.server.rest.request.WriteProcessor;
import com.kenstevens.stratinit.server.rest.session.StratInitSessionManager;
import com.kenstevens.stratinit.server.rest.svc.ErrorProcessor;
import com.kenstevens.stratinit.server.rest.svc.NationSvc;
import com.kenstevens.stratinit.server.rest.svc.PlayerWorldViewUpdate;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.UnitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = SIRestPaths.BASE_PATH)
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
    private PlayerWorldViewUpdate playerWorldViewUpdate;
    @Autowired
    private DataCache dataCache;
    @Autowired
    private StratInitSessionManager sessionManager;

    @GetMapping(path = SIRestPaths.VERSION)
    public Result<String> getVersion() {
        return Result.make(Constants.SERVER_VERSION);
    }

    @GetMapping(path = SIRestPaths.UNIT_BASE)
    public Result<List<SIUnitBase>> getUnitBases() {
        List<SIUnitBase> result = Arrays.stream(UnitType.values())
                .map(unitType -> new SIUnitBase(UnitBase.getUnitBase(unitType)))
                .collect(Collectors.toList());
        return Result.make(result);
    }

    @GetMapping(path = SIRestPaths.SERVER_CONFIG)
    public Result<Properties> getServerConfig() {
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
        return Result.make(properties);
    }

    @PostMapping(path = SIRestPaths.SET_GAME)
    public Result<None> setGame(@RequestBody SetGameJson request) {
        return writeProcessor.processForGame(request.gameId, session -> {
            GameCache gameCache = dataCache.getGameCache(request.gameId);
            if (gameCache == null) {
                return new Result<>("The game " + request.gameId + " does not exist.", false);
            }
            Game game = gameCache.getGame();
            if (game == null) {
                return new Result<>("The game " + request.gameId + " does not exist.", false);
            }
            if (game.hasEnded()) {
                return new Result<>("The game " + request.gameId + " has ended.", false);
            }
            if (!game.isMapped()) {
                return new Result<>("The game " + request.gameId + " is not open yet.", false);
            }
            Nation nation = sessionManager.setNation(session.getPlayer(), request.gameId);
            if (nation == null || dataCache.getGameCache(nation.getGame()) == null) {
                return new Result<>("You have not joined game #" + request.gameId + " (This error should never happen!)", false);
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
    public Result<SINation> joinGame(@RequestBody SetGameJson request) {
        return writeProcessor.processForGame(request.gameId, session -> {
            Result<Nation> result = gameService.joinGame(session.getPlayer(), request.gameId, request.noAlliances);
            Nation nation = result.getValue();
            SINation siNation = nationSvc.nationToSINation(nation, nation, false, false);
            return new Result<>(result, siNation);
        });
    }

    @GetMapping(path = SIRestPaths.GAME_JOINED)
    public Result<List<SIGame>> getJoinedGames() {
        return requestProcessor.processNoGame(player -> nationSvc.getJoinedGames(player));
    }

    @GetMapping(path = SIRestPaths.GAME_UNJOINED)
    public Result<List<SIGame>> getUnjoinedGames() {
        return requestProcessor.processNoGame(player ->
                gameService.getUnjoinedGames(player).stream()
                        .map(game -> new SIGame(game, false))
                        .collect(Collectors.toList()));
    }

    @GetMapping(path = SIRestPaths.UPDATE)
    public Result<SIUpdate> getUpdate() {
        return requestProcessor.process(nation -> playerWorldViewUpdate.getWorldViewUpdate(nation));
    }

    @GetMapping(path = SIRestPaths.CONCEDE)
    public Result<SIUpdate> concede() {
        return writeProcessor.process(nation -> nationSvc.concede(nation), 0);
    }

    @PostMapping(path = SIRestPaths.SUBMIT_ERROR)
    public Result<Integer> submitError(@RequestBody ErrorJson request) {
        return errorProcessor.processError(request.subject, request.stackTrace);
    }
}
