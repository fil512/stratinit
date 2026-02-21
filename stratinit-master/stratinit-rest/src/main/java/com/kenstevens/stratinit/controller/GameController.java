package com.kenstevens.stratinit.controller;

import com.kenstevens.stratinit.client.model.UnitBase;
import com.kenstevens.stratinit.client.rest.SIRestPaths;
import com.kenstevens.stratinit.config.IServerConfig;
import com.kenstevens.stratinit.config.RunModeEnum;
import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.dto.SIUnitBase;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.ErrorJson;
import com.kenstevens.stratinit.remote.request.SetGameJson;
import com.kenstevens.stratinit.server.daoservice.GameDaoService;
import com.kenstevens.stratinit.server.rest.request.RequestFactory;
import com.kenstevens.stratinit.server.rest.request.RequestProcessor;
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
    private RequestFactory requestFactory;
    @Autowired
    private RequestProcessor requestProcessor;
    @Autowired
    private ErrorProcessor errorProcessor;
    @Autowired
    private IServerConfig serverConfig;
    @Autowired
    private NationSvc nationSvc;
    @Autowired
    private GameDaoService gameDaoService;
    @Autowired
    private PlayerWorldViewUpdate playerWorldViewUpdate;

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
        return requestFactory.getSetGameRequest(request.gameId, request.noAlliances).process(request.gameId);
    }

    @PostMapping(path = SIRestPaths.JOIN_GAME)
    public Result<SINation> joinGame(@RequestBody SetGameJson request) {
        return requestFactory.getJoinGameRequest(null, request.gameId, request.noAlliances).process(request.gameId);
    }

    @GetMapping(path = SIRestPaths.GAME_JOINED)
    public Result<List<SIGame>> getJoinedGames() {
        return requestProcessor.processNoGame(player -> nationSvc.getJoinedGames(player));
    }

    @GetMapping(path = SIRestPaths.GAME_UNJOINED)
    public Result<List<SIGame>> getUnjoinedGames() {
        return requestProcessor.processNoGame(player ->
                gameDaoService.getUnjoinedGames(player).stream()
                        .map(game -> new SIGame(game, false))
                        .collect(Collectors.toList()));
    }

    @GetMapping(path = SIRestPaths.UPDATE)
    public Result<SIUpdate> getUpdate() {
        return requestProcessor.process(nation -> playerWorldViewUpdate.getWorldViewUpdate(nation));
    }

    @GetMapping(path = SIRestPaths.CONCEDE)
    public Result<SIUpdate> concede() {
        return requestFactory.getConcedeRequest().process();
    }

    @PostMapping(path = SIRestPaths.SUBMIT_ERROR)
    public Result<Integer> submitError(@RequestBody ErrorJson request) {
        return errorProcessor.processError(request.subject, request.stackTrace);
    }
}
