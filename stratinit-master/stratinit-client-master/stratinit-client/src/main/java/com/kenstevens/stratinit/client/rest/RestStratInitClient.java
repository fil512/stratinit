package com.kenstevens.stratinit.client.rest;

import com.kenstevens.stratinit.dto.*;
import com.kenstevens.stratinit.dto.news.SINewsLogsDay;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

@Service
public class RestStratInitClient implements IStratInitServer {
    @Autowired
    RestClient restClient;

    @Override
    public Result<String> getVersion() {
        return restClient.get(SIRestPaths.VERSION, String.class);
    }

    @Override
    public Result<List<SIUnitBase>> getUnitBases() {
        return restClient.getList(SIRestPaths.UNIT_BASE, SIUnitBase.class);
    }

    @Override
    public Result<Properties> getServerConfig() {
        return restClient.get(SIRestPaths.SERVER_CONFIG, Properties.class);
    }

    @Override
    public Result<None> setGame(SetGameJson request) {
        return restClient.post(SIRestPaths.SET_GAME, request, None.class);
    }

    @Override
    public Result<SINation> joinGame(SetGameJson request) {
        return restClient.post(SIRestPaths.JOIN_GAME, request, SINation.class);
    }

    @Override
    public Result<List<SIGame>> getJoinedGames() {
        return restClient.getList(SIRestPaths.GAME_JOINED, SIGame.class);
    }

    @Override
    public Result<List<SIGame>> getUnjoinedGames() {
        return restClient.getList(SIRestPaths.GAME_UNJOINED, SIGame.class);

    }

    @Override
    public Result<List<SISector>> getSectors() {
        return restClient.getList(SIRestPaths.SECTOR, SISector.class);
    }

    @Override
    public Result<List<SIUnit>> getUnits() {
        return restClient.getList(SIRestPaths.UNIT, SIUnit.class);
    }

    @Override
    public Result<List<SINation>> getNations() {
        return restClient.getList(SIRestPaths.NATION, SINation.class);
    }

    @Override
    public Result<List<SICity>> getCities() {
        return restClient.getList(SIRestPaths.CITY, SICity.class);
    }

    @Override
    public Result<List<SIBattleLog>> getBattleLog() {
        return restClient.getList(SIRestPaths.BATTLE_LOG, SIBattleLog.class);
    }

    @Override
    public Result<SICity> updateCity(UpdateCityJson updateCityJson) {
        return restClient.get(SIRestPaths.UPDATE_CITY, SICity.class);
    }

    @Override
    public Result<SIUpdate> getUpdate() {
        return restClient.get(SIRestPaths.UPDATE, SIUpdate.class);
    }

    @Override
    public Result<SIUpdate> moveUnits(MoveUnitsJson moveUnitsJson) {
        return restClient.get(SIRestPaths.MOVE_UNITS, SIUpdate.class);
    }

    @Override
    public Result<List<SIUnit>> getSeenUnits() {
        return restClient.getList(SIRestPaths.UNIT_SEEN, SIUnit.class);
    }

    @Override
    public Result<List<SICity>> getSeenCities() {
        return restClient.getList(SIRestPaths.CITY_SEEN, SICity.class);
    }

    @Override
    public Result<List<SIRelation>> getRelations() {
        return restClient.getList(SIRestPaths.RELATION, SIRelation.class);
    }

    @Override
    public Result<SIRelation> setRelation(SetRelationJson request) {
        return restClient.post(SIRestPaths.SET_RELATION, request, SIRelation.class);
    }

    @Override
    public Result<SIUpdate> cedeUnits(CedeUnitsJson request) {
        return restClient.post(SIRestPaths.CEDE_UNITS, request, SIUpdate.class);
    }

    @Override
    public Result<SIUpdate> cedeCity(CedeCityJson request) {
        return restClient.post(SIRestPaths.CEDE_CITY, request, SIUpdate.class);
    }

    @Override
    public Result<List<SIMessage>> getMessages() {
        return restClient.getList(SIRestPaths.MESSAGE, SIMessage.class);
    }

    @Override
    public Result<Integer> sendMessage(SIMessage message) {
        return restClient.post(SIRestPaths.SEND_MESSAGE, message, Integer.class);
    }

    @Override
    public Result<List<SIMessage>> getMail() {
        return restClient.getList(SIRestPaths.MESSAGE_MAIL, SIMessage.class);
    }

    @Override
    public Result<List<SIMessage>> getSentMail() {
        return restClient.getList(SIRestPaths.MESSAGE_SENT, SIMessage.class);
    }

    @Override
    public Result<List<SIMessage>> getAnnouncements() {
        return restClient.getList(SIRestPaths.MESSAGE_ANNOUNCEMENT, SIMessage.class);
    }

    @Override
    public Result<List<SINewsLogsDay>> getNewsLogs() {
        return restClient.getList(SIRestPaths.NEWS_LOG, SINewsLogsDay.class);
    }

    @Override
    public Result<List<SIUnitBuilt>> getUnitsBuilt() {
        return restClient.getList(SIRestPaths.UNIT_BUILT, SIUnitBuilt.class);
    }

    @Override
    public Result<SINation> getMyNation() {
        return restClient.get(SIRestPaths.NATION_ME, SINation.class);
    }

    @Override
    public Result<SIUpdate> disbandUnits(SIUnitListJson request) {
        return restClient.post(SIRestPaths.DISBAND_UNITS, request, SIUpdate.class);
    }

    @Override
    public Result<List<SITeam>> getTeams() {
        return restClient.getList(SIRestPaths.TEAM, SITeam.class);
    }

    @Override
    public Result<SIUpdate> concede() {
        return restClient.get(SIRestPaths.CONCEDE, SIUpdate.class);
    }

    @Override
    public Result<Integer> submitError(ErrorJson request) {
        return restClient.post(SIRestPaths.SUBMIT_ERROR, request, Integer.class);
    }

    @Override
    public Result<SIUpdate> buildCity(SIUnitListJson request) {
        return restClient.post(SIRestPaths.BUILD_CITY, request, SIUpdate.class);
    }

    // FIXME lots of patterns here to consolidate
    @Override
    public Result<SIUpdate> switchTerrain(SIUnitListJson request) {
        return restClient.post(SIRestPaths.SWITCH_TERRAIN, request, SIUpdate.class);
    }

    @Override
    public Result<SIUpdate> cancelMove(SIUnitListJson request) {
        return restClient.post(SIRestPaths.CANCEL_MOVE, request, SIUpdate.class);
    }
}
