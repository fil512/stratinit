package com.kenstevens.stratinit.controller;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.kenstevens.stratinit.dto.*;
import com.kenstevens.stratinit.dto.news.SINewsLogsDay;
import com.kenstevens.stratinit.model.UnitBase;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.*;
import com.kenstevens.stratinit.rest.IStratInitServer;
import com.kenstevens.stratinit.rest.SIRestPaths;
import com.kenstevens.stratinit.server.rest.helper.ErrorProcessor;
import com.kenstevens.stratinit.server.rest.request.RequestFactory;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.UnitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;

@RestController
@RequestMapping("/stratinit")
public class StratInitController implements IStratInitServer {
    @Autowired
    private RequestFactory requestFactory;
    @Autowired
    private ErrorProcessor errorProcessor;

    /*
     * Non database requests
     */

    // TODO Return incremental battle logs and nation with all commands
    @GetMapping(value = SIRestPaths.VERSION)
    @Override
    public Result<String> getVersion() {
        return Result.make(Constants.SERVER_VERSION);
    }

    @GetMapping(value = SIRestPaths.UNIT_BASE)
    @Override
    public Result<List<SIUnitBase>> getUnitBases() {
        List<SIUnitBase> result = Lists.newArrayList(Collections2.transform(
                Lists.newArrayList(UnitType.values()),
                new Function<UnitType, SIUnitBase>() {
                    public SIUnitBase apply(UnitType unitType) {
                        return new SIUnitBase(UnitBase.getUnitBase(unitType));
                    }
                }));
        return Result.make(result);
    }

    @GetMapping(value = SIRestPaths.SERVER_CONFIG)
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
        return Result.make(properties);
    }

    @PostMapping(value = SIRestPaths.SET_GAME)
    @Override
    public Result<None> setGame(@RequestBody SetGameJson request) {
        return requestFactory.getSetGameRequest(request.gameId, request.noAlliances).process(request.gameId);
    }

    /*
     * Read-only database requests
     */

    @GetMapping(value = SIRestPaths.GAME_JOINED)
    @Override
    public Result<List<SIGame>> getJoinedGames() {
        return requestFactory.getGetJoinedGamesRequest().processNoGame();
    }

    @GetMapping(value = SIRestPaths.GAME_UNJOINED)
    @Override
    public Result<List<SIGame>> getUnjoinedGames() {
        return requestFactory.getGetUnjoinedGamesRequest().processNoGame();
    }

    @GetMapping(value = SIRestPaths.NATION)
    @Override
    public Result<List<SINation>> getNations() {
        return requestFactory.getGetNationsRequest().process();
    }

    @GetMapping(value = SIRestPaths.SECTOR)
    @Override
    public Result<List<SISector>> getSectors() {
        return requestFactory.getGetSectorsRequest().process();
    }

    @GetMapping(value = SIRestPaths.UNIT)
    @Override
    public Result<List<SIUnit>> getUnits() {
        return requestFactory.getGetUnitsRequest().process();
    }

    @GetMapping(value = SIRestPaths.UNIT_SEEN)
    @Override
    public Result<List<SIUnit>> getSeenUnits() {
        return requestFactory.getGetSeenUnitsRequest().process();
    }

    @GetMapping(value = SIRestPaths.NATION_ME)
    @Override
    public Result<SINation> getMyNation() {
        return requestFactory.getGetMyNationRequest().process();
    }

    @GetMapping(value = SIRestPaths.CITY)
    @Override
    public Result<List<SICity>> getCities() {
        return requestFactory.getGetCitiesRequest().process();
    }

    @GetMapping(value = SIRestPaths.CITY_SEEN)
    @Override
    public Result<List<SICity>> getSeenCities() {
        return requestFactory.getGetSeenCitiesRequest().process();
    }

    @GetMapping(value = SIRestPaths.UPDATE)
    @Override
    public Result<SIUpdate> getUpdate() {
        return requestFactory.getGetUpdateRequest().process();
    }

    @GetMapping(value = SIRestPaths.BATTLE_LOG)
    @Override
    public Result<List<SIBattleLog>> getBattleLog() {
        return requestFactory.getGetBattleLogRequest().process();
    }

    @GetMapping(value = SIRestPaths.MESSAGE_MAIL)
    @Override
    public Result<List<SIMessage>> getMail() {
        return requestFactory.getGetMailRequest().process();
    }

    @GetMapping(value = SIRestPaths.RELATION)
    @Override
    public Result<List<SIRelation>> getRelations() {
        return requestFactory.getGetRelationsRequest().process();
    }

    @GetMapping(value = SIRestPaths.MESSAGE)
    @Override
    public Result<List<SIMessage>> getMessages() {
        return requestFactory.getGetMessagesRequest().process();
    }

    @GetMapping(value = SIRestPaths.MESSAGE_SENT)
    @Override
    public Result<List<SIMessage>> getSentMail() {
        return requestFactory.getGetSentMailRequest().process();
    }

    @GetMapping(value = SIRestPaths.MESSAGE_ANNOUNCEMENT)
    @Override
    public Result<List<SIMessage>> getAnnouncements() {
        return requestFactory.getGetAnnouncementsRequest().process();
    }

    @GetMapping(value = SIRestPaths.UNIT_BUILT)
    @Override
    public Result<List<SIUnitBuilt>> getUnitsBuilt() {
        return requestFactory.getGetUnitsBuiltRequest().process();
    }

    /*
     * Database update requests
     */

    @PostMapping(value = SIRestPaths.UPDATE_CITY)
    @Override
    public Result<SICity> updateCity(@RequestBody UpdateCityJson updateCityJson) {
        return requestFactory.getUpdateCityRequest(updateCityJson.sicity, updateCityJson.field).process();
    }

    @PostMapping(value = SIRestPaths.MOVE_UNITS)
    @Override
    public Result<SIUpdate> moveUnits(@RequestBody MoveUnitsJson moveUnitsJson) {
        return requestFactory.getMoveUnitsRequest(moveUnitsJson.units, moveUnitsJson.target).process();
    }

    @PostMapping(value = SIRestPaths.CEDE_UNITS)
    @Override
    public Result<SIUpdate> cedeUnits(@RequestBody CedeUnitsJson request) {
        return requestFactory.getCedeUnitsRequest(request.siunits, request.nationId).process();
    }

    @PostMapping(value = SIRestPaths.CEDE_CITY)
    @Override
    public Result<SIUpdate> cedeCity(@RequestBody CedeCityJson request) {
        return requestFactory.getCedeCityRequest(request.city, request.nationId).process();
    }

    // FIXME spring mvc test these
    @PostMapping(value = SIRestPaths.SET_RELATION)
    @Override
    public Result<SIRelation> setRelation(@RequestBody SetRelationJson request) {
        return requestFactory.getSetRelationRequest(request.nationId, request.relationType)
                .process();
    }

    @PostMapping(value = SIRestPaths.SEND_MESSAGE)
    @Override
    public Result<Integer> sendMessage(@RequestBody SIMessage simessage) {
        return requestFactory.getSendMessageRequest(simessage).process();
    }

    @PostMapping(value = SIRestPaths.JOIN_GAME)
    @Override
    public Result<SINation> joinGame(@RequestBody SetGameJson request) {
        return requestFactory.getJoinGameRequest(null, request.gameId, request.noAlliances).process(request.gameId);
    }

    @PostMapping(value = SIRestPaths.DISBAND_UNITS)
    @Override
    public Result<SIUpdate> disbandUnits(@RequestBody SIUnitListJson request) {
        return requestFactory.getDisbandUnitRequest(request.siunits).process();
    }

    @PostMapping(value = SIRestPaths.CANCEL_MOVE)
    @Override
    public Result<SIUpdate> cancelMove(@RequestBody SIUnitListJson request) {
        return requestFactory.getCancelMoveOrderRequest(request.siunits).process();
    }

    @PostMapping(value = SIRestPaths.BUILD_CITY)
    @Override
    public Result<SIUpdate> buildCity(@RequestBody SIUnitListJson request) {
        return requestFactory.getBuildCityRequest(request.siunits).process();
    }

    @PostMapping(value = SIRestPaths.SWITCH_TERRAIN)
    @Override
    public Result<SIUpdate> switchTerrain(@RequestBody SIUnitListJson request) {
        return requestFactory.getSwitchTerrainRequest(request.siunits).process();
    }

    @GetMapping(value = SIRestPaths.NEWS_LOG)
    @Override
    public Result<List<SINewsLogsDay>> getNewsLogs() {
        return requestFactory.getGetLogsRequest().process();
    }

    @GetMapping(value = SIRestPaths.TEAM)
    @Override
    public Result<List<SITeam>> getTeams() {
        return requestFactory.getGetTeamsRequest().process();
    }

    @GetMapping(value = SIRestPaths.CONCEDE)
    @Override
    public Result<SIUpdate> concede() {
        return requestFactory.getConcedeRequest().process();
    }

    @PostMapping(value = SIRestPaths.SUBMIT_ERROR)
    @Override
    public Result<Integer> submitError(ErrorJson request) {
        return errorProcessor.processError(request.subject, request.stackTrace);
    }
}
