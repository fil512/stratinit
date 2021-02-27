package com.kenstevens.stratinit.controller;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.kenstevens.stratinit.dto.*;
import com.kenstevens.stratinit.dto.news.SINewsLogsDay;
import com.kenstevens.stratinit.model.UnitBase;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.UpdateCityField;
import com.kenstevens.stratinit.remote.request.SetGameRequest;
import com.kenstevens.stratinit.rest.SIRestPaths;
import com.kenstevens.stratinit.server.rest.helper.ErrorProcessor;
import com.kenstevens.stratinit.server.rest.request.RequestFactory;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;

@RestController
@RequestMapping("/stratinit")
public class StratInitController {
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
    public Result<None> setGame(@RequestBody SetGameRequest request) {
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

    public Result<SICity> updateCity(SICity sicity, UpdateCityField field) {
        return requestFactory.getUpdateCityRequest(sicity, field).process();
    }

    public Result<SIUpdate> moveUnits(List<SIUnit> units, SectorCoords target) {
        return requestFactory.getMoveUnitsRequest(units, target).process();
    }

    public Result<SIUpdate> cedeUnits(List<SIUnit> siunits, int nationId) {
        return requestFactory.getCedeUnitsRequest(siunits, nationId).process();
    }

    public Result<SIUpdate> cedeCity(SICity sicity, int nationId) {
        return requestFactory.getCedeCityRequest(sicity, nationId).process();
    }

    public Result<SIRelation> setRelation(int nationId,
                                          RelationType relationType) {
        return requestFactory.getSetRelationRequest(nationId, relationType)
                .process();
    }

    public Result<Integer> sendMessage(SIMessage simessage) {
        return requestFactory.getSendMessageRequest(simessage).process();
    }

    @PostMapping(value = SIRestPaths.JOIN_GAME)
    public Result<SINation> joinGame(@RequestBody SetGameRequest request) {
        return requestFactory.getJoinGameRequest(null, request.gameId, request.noAlliances).process(request.gameId);
    }

    public Result<SIUpdate> disbandUnits(List<SIUnit> siunits) {
        return requestFactory.getDisbandUnitRequest(siunits).process();
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
