package com.kenstevens.stratinit.rest;

import com.kenstevens.stratinit.dto.*;
import com.kenstevens.stratinit.dto.news.SINewsLogsDay;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.MoveUnitsJson;
import com.kenstevens.stratinit.remote.request.SetGameJson;
import com.kenstevens.stratinit.remote.request.SetRelationJson;
import com.kenstevens.stratinit.remote.request.UpdateCityJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestStratInitClient implements IStratInitServer {
    @Autowired
    RestClient restClient;

    @Override
    public Result<String> getVersion() {
        return restClient.get(SIRestPaths.VERSION, String.class);
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
    public Result<SIUpdate> cedeUnits(List<SIUnit> units, int nationId) {
        return null;
    }

    @Override
    public Result<SIUpdate> cedeCity(SICity city, int nationId) {
        return null;
    }

    @Override
    public Result<List<SIMessage>> getMessages() {
        return restClient.getList(SIRestPaths.MESSAGE, SIMessage.class);
    }

    @Override
    public Result<Integer> sendMessage(SIMessage message) {
        return null;
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
    public Result<SIUpdate> disbandUnits(List<SIUnit> siUnits) {
        return null;
    }

    @Override
    public Result<List<SITeam>> getTeams() {
        return restClient.getList(SIRestPaths.TEAM, SITeam.class);
    }

    @Override
    public Result<SIUpdate> concede() {
        return null;
    }

    @Override
    public Result<Integer> submitError(String subject, String stackTrace) {
        return null;
    }

    @Override
    public Result<SIUpdate> buildCity(List<SIUnit> siunits) {
        return null;
    }

    @Override
    public Result<SIUpdate> switchTerrain(List<SIUnit> siunits) {
        return null;
    }

    @Override
    public Result<SIUpdate> cancelMoveOrder(List<SIUnit> siunits) {
        return null;
    }
}
