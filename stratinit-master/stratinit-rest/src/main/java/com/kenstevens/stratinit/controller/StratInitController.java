package com.kenstevens.stratinit.controller;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.kenstevens.stratinit.client.model.UnitBase;
import com.kenstevens.stratinit.client.rest.IStratInitServer;
import com.kenstevens.stratinit.client.rest.SIRestPaths;
import com.kenstevens.stratinit.config.IServerConfig;
import com.kenstevens.stratinit.config.RunModeEnum;
import com.kenstevens.stratinit.dto.*;
import com.kenstevens.stratinit.dto.news.SINewsLogsDay;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.*;
import com.kenstevens.stratinit.server.rest.request.RequestFactory;
import com.kenstevens.stratinit.server.rest.svc.ErrorProcessor;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.UnitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;

@RestController
@RequestMapping(path = SIRestPaths.BASE_PATH)
public class StratInitController implements IStratInitServer {
    @Autowired
    private RequestFactory requestFactory;
    @Autowired
    private ErrorProcessor errorProcessor;
    @Autowired
    private IServerConfig serverConfig;

    /*
     * Non database requests
     */

    // TODO Return incremental battle logs and nation with all commands
    @GetMapping(path = SIRestPaths.VERSION)
    @Override
    public Result<String> getVersion() {
        return Result.make(Constants.SERVER_VERSION);
    }

    @GetMapping(path = SIRestPaths.UNIT_BASE)
    @Override
    public Result<List<SIUnitBase>> getUnitBases() {
        List<SIUnitBase> result = Lists.newArrayList(Collections2.transform(
                Lists.newArrayList(UnitType.values()),
                unitType -> new SIUnitBase(UnitBase.getUnitBase(unitType))));
        return Result.make(result);
    }

    @GetMapping(path = SIRestPaths.SERVER_CONFIG)
    @Override
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
    @Override
    public Result<None> setGame(@RequestBody SetGameJson request) {
        return requestFactory.getSetGameRequest(request.gameId, request.noAlliances).process(request.gameId);
    }

    /*
     * Read-only database requests
     */

    @GetMapping(path = SIRestPaths.GAME_JOINED)
    @Override
    public Result<List<SIGame>> getJoinedGames() {
        return requestFactory.getGetJoinedGamesRequest().processNoGame();
    }

    @GetMapping(path = SIRestPaths.GAME_UNJOINED)
    @Override
    public Result<List<SIGame>> getUnjoinedGames() {
        return requestFactory.getGetUnjoinedGamesRequest().processNoGame();
    }

    @GetMapping(path = SIRestPaths.NATION)
    @Override
    public Result<List<SINation>> getNations() {
        return requestFactory.getGetNationsRequest().process();
    }

    @GetMapping(path = SIRestPaths.SECTOR)
    @Override
    public Result<List<SISector>> getSectors() {
        return requestFactory.getGetSectorsRequest().process();
    }

    @GetMapping(path = SIRestPaths.UNIT)
    @Override
    public Result<List<SIUnit>> getUnits() {
        return requestFactory.getGetUnitsRequest().process();
    }

    @GetMapping(path = SIRestPaths.UNIT_SEEN)
    @Override
    public Result<List<SIUnit>> getSeenUnits() {
        return requestFactory.getGetSeenUnitsRequest().process();
    }

    @GetMapping(path = SIRestPaths.NATION_ME)
    @Override
    public Result<SINation> getMyNation() {
        return requestFactory.getGetMyNationRequest().process();
    }

    @GetMapping(path = SIRestPaths.CITY)
    @Override
    public Result<List<SICity>> getCities() {
        return requestFactory.getGetCitiesRequest().process();
    }

    @GetMapping(path = SIRestPaths.CITY_SEEN)
    @Override
    public Result<List<SICity>> getSeenCities() {
        return requestFactory.getGetSeenCitiesRequest().process();
    }

    @GetMapping(path = SIRestPaths.UPDATE)
    @Override
    public Result<SIUpdate> getUpdate() {
        return requestFactory.getGetUpdateRequest().process();
    }

    @GetMapping(path = SIRestPaths.BATTLE_LOG)
    @Override
    public Result<List<SIBattleLog>> getBattleLog() {
        return requestFactory.getGetBattleLogRequest().process();
    }

    @GetMapping(path = SIRestPaths.MESSAGE_MAIL)
    @Override
    public Result<List<SIMessage>> getMail() {
        return requestFactory.getGetMailRequest().process();
    }

    @GetMapping(path = SIRestPaths.RELATION)
    @Override
    public Result<List<SIRelation>> getRelations() {
        return requestFactory.getGetRelationsRequest().process();
    }

    @GetMapping(path = SIRestPaths.MESSAGE)
    @Override
    public Result<List<SIMessage>> getMessages() {
        return requestFactory.getGetMessagesRequest().process();
    }

    @GetMapping(path = SIRestPaths.MESSAGE_SENT)
    @Override
    public Result<List<SIMessage>> getSentMail() {
        return requestFactory.getGetSentMailRequest().process();
    }

    @GetMapping(path = SIRestPaths.MESSAGE_ANNOUNCEMENT)
    @Override
    public Result<List<SIMessage>> getAnnouncements() {
        return requestFactory.getGetAnnouncementsRequest().process();
    }

    @GetMapping(path = SIRestPaths.UNIT_BUILT)
    @Override
    public Result<List<SIUnitBuilt>> getUnitsBuilt() {
        return requestFactory.getGetUnitsBuiltRequest().process();
    }

    /*
     * Database update requests
     */

    @PostMapping(path = SIRestPaths.UPDATE_CITY)
    @Override
    public Result<SICity> updateCity(@RequestBody UpdateCityJson updateCityJson) {
        return requestFactory.getUpdateCityRequest(updateCityJson.sicity, updateCityJson.field).process();
    }

    @PostMapping(path = SIRestPaths.MOVE_UNITS)
    @Override
    public Result<SIUpdate> moveUnits(@RequestBody MoveUnitsJson moveUnitsJson) {
        return requestFactory.getMoveUnitsRequest(moveUnitsJson.units, moveUnitsJson.target).process();
    }

    @PostMapping(path = SIRestPaths.CEDE_UNITS)
    @Override
    public Result<SIUpdate> cedeUnits(@RequestBody CedeUnitsJson request) {
        return requestFactory.getCedeUnitsRequest(request.siunits, request.nationId).process();
    }

    @PostMapping(path = SIRestPaths.CEDE_CITY)
    @Override
    public Result<SIUpdate> cedeCity(@RequestBody CedeCityJson request) {
        return requestFactory.getCedeCityRequest(request.city, request.nationId).process();
    }

    @PostMapping(path = SIRestPaths.SET_RELATION)
    @Override
    public Result<SIRelation> setRelation(@RequestBody SetRelationJson request) {
        return requestFactory.getSetRelationRequest(request.nationId, request.relationType)
                .process();
    }

    @PostMapping(path = SIRestPaths.SEND_MESSAGE)
    @Override
    public Result<Integer> sendMessage(@RequestBody SIMessage simessage) {
        return requestFactory.getSendMessageRequest(simessage).process();
    }

    @PostMapping(path = SIRestPaths.JOIN_GAME)
    @Override
    public Result<SINation> joinGame(@RequestBody SetGameJson request) {
        return requestFactory.getJoinGameRequest(null, request.gameId, request.noAlliances).process(request.gameId);
    }

    @PostMapping(path = SIRestPaths.DISBAND_UNITS)
    @Override
    public Result<SIUpdate> disbandUnits(@RequestBody SIUnitListJson request) {
        return requestFactory.getDisbandUnitRequest(request.siunits).process();
    }

    @PostMapping(path = SIRestPaths.CANCEL_MOVE)
    @Override
    public Result<SIUpdate> cancelMove(@RequestBody SIUnitListJson request) {
        return requestFactory.getCancelMoveOrderRequest(request.siunits).process();
    }

    @PostMapping(path = SIRestPaths.BUILD_CITY)
    @Override
    public Result<SIUpdate> buildCity(@RequestBody SIUnitListJson request) {
        return requestFactory.getBuildCityRequest(request.siunits).process();
    }

    @PostMapping(path = SIRestPaths.SWITCH_TERRAIN)
    @Override
    public Result<SIUpdate> switchTerrain(@RequestBody SIUnitListJson request) {
        return requestFactory.getSwitchTerrainRequest(request.siunits).process();
    }

    @GetMapping(path = SIRestPaths.NEWS_LOG)
    @Override
    public Result<List<SINewsLogsDay>> getNewsLogs() {
        return requestFactory.getGetLogsRequest().process();
    }

    @GetMapping(path = SIRestPaths.TEAM)
    @Override
    public Result<List<SITeam>> getTeams() {
        return requestFactory.getGetTeamsRequest().process();
    }

    @GetMapping(path = SIRestPaths.CONCEDE)
    @Override
    public Result<SIUpdate> concede() {
        return requestFactory.getConcedeRequest().process();
    }

    @PostMapping(path = SIRestPaths.SUBMIT_ERROR)
    @Override
    public Result<Integer> submitError(ErrorJson request) {
        return errorProcessor.processError(request.subject, request.stackTrace);
    }
}
