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
    public Result<String> getVersion() {
        return Result.make(Constants.SERVER_VERSION);
    }

    @GetMapping(value = SIRestPaths.UNIT_BASE)
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
    public Result<None> setGame(@RequestBody SetGameJson request) {
        return requestFactory.getSetGameRequest(request.gameId, request.noAlliances).process(request.gameId);
    }

    /*
     * Read-only database requests
     */

    @GetMapping(value = SIRestPaths.GAME_JOINED)
    public Result<List<SIGame>> getJoinedGames() {
        return requestFactory.getGetJoinedGamesRequest().processNoGame();
    }

    @GetMapping(value = SIRestPaths.GAME_UNJOINED)
    public Result<List<SIGame>> getUnjoinedGames() {
        return requestFactory.getGetUnjoinedGamesRequest().processNoGame();
    }

    @GetMapping(value = SIRestPaths.NATION)
    public Result<List<SINation>> getNations() {
        return requestFactory.getGetNationsRequest().process();
    }

    @GetMapping(value = SIRestPaths.SECTOR)
    public Result<List<SISector>> getSectors() {
        return requestFactory.getGetSectorsRequest().process();
    }

    @GetMapping(value = SIRestPaths.UNIT)
    public Result<List<SIUnit>> getUnits() {
        return requestFactory.getGetUnitsRequest().process();
    }

    @GetMapping(value = SIRestPaths.UNIT_SEEN)
    public Result<List<SIUnit>> getSeenUnits() {
        return requestFactory.getGetSeenUnitsRequest().process();
    }

    @GetMapping(value = SIRestPaths.NATION_ME)
    public Result<SINation> getMyNation() {
        return requestFactory.getGetMyNationRequest().process();
    }

    @GetMapping(value = SIRestPaths.CITY)
    public Result<List<SICity>> getCities() {
        return requestFactory.getGetCitiesRequest().process();
    }

    @GetMapping(value = SIRestPaths.CITY_SEEN)
    public Result<List<SICity>> getSeenCities() {
        return requestFactory.getGetSeenCitiesRequest().process();
    }

    @GetMapping(value = SIRestPaths.UPDATE)
    public Result<SIUpdate> getUpdate() {
        return requestFactory.getGetUpdateRequest().process();
    }

    @GetMapping(value = SIRestPaths.BATTLE_LOG)
    public Result<List<SIBattleLog>> getBattleLog() {
        return requestFactory.getGetBattleLogRequest().process();
    }

    @GetMapping(value = SIRestPaths.MESSAGE_MAIL)
    public Result<List<SIMessage>> getMail() {
        return requestFactory.getGetMailRequest().process();
    }

    @GetMapping(value = SIRestPaths.RELATION)
    public Result<List<SIRelation>> getRelations() {
        return requestFactory.getGetRelationsRequest().process();
    }

    @GetMapping(value = SIRestPaths.MESSAGE)
    public Result<List<SIMessage>> getMessages() {
        return requestFactory.getGetMessagesRequest().process();
    }

    @GetMapping(value = SIRestPaths.MESSAGE_SENT)
    public Result<List<SIMessage>> getSentMail() {
        return requestFactory.getGetSentMailRequest().process();
    }

    @GetMapping(value = SIRestPaths.MESSAGE_ANNOUNCEMENT)
    public Result<List<SIMessage>> getAnnouncements() {
        return requestFactory.getGetAnnouncementsRequest().process();
    }

    @GetMapping(value = SIRestPaths.UNIT_BUILT)
    public Result<List<SIUnitBuilt>> getUnitsBuilt() {
        return requestFactory.getGetUnitsBuiltRequest().process();
    }

    /*
     * Database update requests
     */

    @PostMapping(value = SIRestPaths.UPDATE_CITY)
    public Result<SICity> updateCity(@RequestBody UpdateCityJson updateCityJson) {
        return requestFactory.getUpdateCityRequest(updateCityJson.sicity, updateCityJson.field).process();
    }

    @PostMapping(value = SIRestPaths.MOVE_UNITS)
    public Result<SIUpdate> moveUnits(@RequestBody MoveUnitsJson moveUnitsJson) {
        return requestFactory.getMoveUnitsRequest(moveUnitsJson.units, moveUnitsJson.target).process();
    }

    @PostMapping(value = SIRestPaths.CEDE_UNITS)
    public Result<SIUpdate> cedeUnits(@RequestBody CedeUnitsJson request) {
        return requestFactory.getCedeUnitsRequest(request.siunits, request.nationId).process();
    }

    @PostMapping(value = SIRestPaths.CEDE_CITY)
    public Result<SIUpdate> cedeCity(@RequestBody CedeCityJson request) {
        return requestFactory.getCedeCityRequest(request.city, request.nationId).process();
    }

    // FIXME spring mvc test these
    @PostMapping(value = SIRestPaths.SET_RELATION)
    public Result<SIRelation> setRelation(@RequestBody SetRelationJson request) {
        return requestFactory.getSetRelationRequest(request.nationId, request.relationType)
                .process();
    }

    @PostMapping(value = SIRestPaths.SEND_MESSAGE)
    public Result<Integer> sendMessage(@RequestBody SIMessage simessage) {
        return requestFactory.getSendMessageRequest(simessage).process();
    }

    @PostMapping(value = SIRestPaths.JOIN_GAME)
    public Result<SINation> joinGame(@RequestBody SetGameJson request) {
        return requestFactory.getJoinGameRequest(null, request.gameId, request.noAlliances).process(request.gameId);
    }

    @PostMapping(value = SIRestPaths.DISBAND_UNITS)
    public Result<SIUpdate> disbandUnits(@RequestBody SIUnitListJson request) {
        return requestFactory.getDisbandUnitRequest(request.siunits).process();
    }

    public Result<SIUpdate> cancelMoveOrder(List<SIUnit> siunits) {
        return requestFactory.getCancelMoveOrderRequest(siunits).process();
    }

    public Result<SIUpdate> buildCity(List<SIUnit> siunits) {
        return requestFactory.getBuildCityRequest(siunits).process();
    }

    public Result<SIUpdate> switchTerrain(List<SIUnit> siunits) {
        return requestFactory.getSwitchTerrainRequest(siunits).process();
    }

    @GetMapping(value = SIRestPaths.NEWS_LOG)
    public Result<List<SINewsLogsDay>> getNewsLogs() {
        return requestFactory.getGetLogsRequest().process();
    }

    @GetMapping(value = SIRestPaths.TEAM)
    public Result<List<SITeam>> getTeams() {
        return requestFactory.getGetTeamsRequest().process();
    }

    public Result<SIUpdate> concede() {
        return requestFactory.getConcedeRequest().process();
    }

    public Result<Integer> submitError(String subject, String stackTrace) {
        return errorProcessor.processError(subject, stackTrace);
    }
}
