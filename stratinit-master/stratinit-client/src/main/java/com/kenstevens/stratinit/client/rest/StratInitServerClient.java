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
public class StratInitServerClient implements IStratInitServer {
    @Autowired
    StratInitRestClient stratInitRestClient;

    @Override
    public Result<String> getVersion() {
        return stratInitRestClient.get(SIRestPaths.VERSION, String.class);
    }

    @Override
    public Result<List<SIUnitBase>> getUnitBases() {
        return stratInitRestClient.getList(SIRestPaths.UNIT_BASE, SIUnitBase.class);
    }

    @Override
    public Result<Properties> getServerConfig() {
        return stratInitRestClient.get(SIRestPaths.SERVER_CONFIG, Properties.class);
    }

    @Override
    public Result<None> setGame(SetGameJson request) {
        return stratInitRestClient.post(SIRestPaths.SET_GAME, request, None.class);
    }

    @Override
    public Result<SINation> joinGame(SetGameJson request) {
        return stratInitRestClient.post(SIRestPaths.JOIN_GAME, request, SINation.class);
    }

    @Override
    public Result<List<SIGame>> getJoinedGames() {
        return stratInitRestClient.getList(SIRestPaths.GAME_JOINED, SIGame.class);
    }

    @Override
    public Result<List<SIGame>> getUnjoinedGames() {
        return stratInitRestClient.getList(SIRestPaths.GAME_UNJOINED, SIGame.class);

    }

    @Override
    public Result<List<SISector>> getSectors() {
        return stratInitRestClient.getList(SIRestPaths.SECTOR, SISector.class);
    }

    @Override
    public Result<List<SIUnit>> getUnits() {
        return stratInitRestClient.getList(SIRestPaths.UNIT, SIUnit.class);
    }

    @Override
    public Result<List<SINation>> getNations() {
        return stratInitRestClient.getList(SIRestPaths.NATION, SINation.class);
    }

    @Override
    public Result<List<SICityUpdate>> getCities() {
        return stratInitRestClient.getList(SIRestPaths.CITY, SICityUpdate.class);
    }

    @Override
    public Result<List<SIBattleLog>> getBattleLog() {
        return stratInitRestClient.getList(SIRestPaths.BATTLE_LOG, SIBattleLog.class);
    }

    @Override
    public Result<SICityUpdate> updateCity(UpdateCityJson updateCityJson) {
        return stratInitRestClient.post(SIRestPaths.UPDATE_CITY, updateCityJson, SICityUpdate.class);
    }

    @Override
    public Result<SIUpdate> getUpdate() {
        return stratInitRestClient.get(SIRestPaths.UPDATE, SIUpdate.class);
    }

    @Override
    public Result<SIUpdate> moveUnits(MoveUnitsJson moveUnitsJson) {
        return stratInitRestClient.post(SIRestPaths.MOVE_UNITS, moveUnitsJson, SIUpdate.class);
    }

    @Override
    public Result<List<SIUnit>> getSeenUnits() {
        return stratInitRestClient.getList(SIRestPaths.UNIT_SEEN, SIUnit.class);
    }

    @Override
    public Result<List<SICityUpdate>> getSeenCities() {
        return stratInitRestClient.getList(SIRestPaths.CITY_SEEN, SICityUpdate.class);
    }

    @Override
    public Result<List<SIRelation>> getRelations() {
        return stratInitRestClient.getList(SIRestPaths.RELATION, SIRelation.class);
    }

    @Override
    public Result<SIRelation> setRelation(SetRelationJson request) {
        return stratInitRestClient.post(SIRestPaths.SET_RELATION, request, SIRelation.class);
    }

    @Override
    public Result<SIUpdate> cedeUnits(CedeUnitsJson request) {
        return stratInitRestClient.post(SIRestPaths.CEDE_UNITS, request, SIUpdate.class);
    }

    @Override
    public Result<SIUpdate> cedeCity(CedeCityJson request) {
        return stratInitRestClient.post(SIRestPaths.CEDE_CITY, request, SIUpdate.class);
    }

    @Override
    public Result<List<SIMessage>> getMessages() {
        return stratInitRestClient.getList(SIRestPaths.MESSAGE, SIMessage.class);
    }

    @Override
    public Result<Integer> sendMessage(SIMessage message) {
        return stratInitRestClient.post(SIRestPaths.SEND_MESSAGE, message, Integer.class);
    }

    @Override
    public Result<List<SIMessage>> getMail() {
        return stratInitRestClient.getList(SIRestPaths.MESSAGE_MAIL, SIMessage.class);
    }

    @Override
    public Result<List<SIMessage>> getSentMail() {
        return stratInitRestClient.getList(SIRestPaths.MESSAGE_SENT, SIMessage.class);
    }

    @Override
    public Result<List<SIMessage>> getAnnouncements() {
        return stratInitRestClient.getList(SIRestPaths.MESSAGE_ANNOUNCEMENT, SIMessage.class);
    }

    @Override
    public Result<List<SINewsLogsDay>> getNewsLogs() {
        return stratInitRestClient.getList(SIRestPaths.NEWS_LOG, SINewsLogsDay.class);
    }

    @Override
    public Result<List<SIUnitBuilt>> getUnitsBuilt() {
        return stratInitRestClient.getList(SIRestPaths.UNIT_BUILT, SIUnitBuilt.class);
    }

    @Override
    public Result<SINation> getMyNation() {
        return stratInitRestClient.get(SIRestPaths.NATION_ME, SINation.class);
    }

    @Override
    public Result<SIUpdate> disbandUnits(SIUnitListJson request) {
        return stratInitRestClient.post(SIRestPaths.DISBAND_UNITS, request, SIUpdate.class);
    }

    @Override
    public Result<List<SITeam>> getTeams() {
        return stratInitRestClient.getList(SIRestPaths.TEAM, SITeam.class);
    }

    @Override
    public Result<SIUpdate> concede() {
        return stratInitRestClient.get(SIRestPaths.CONCEDE, SIUpdate.class);
    }

    @Override
    public Result<Integer> submitError(ErrorJson request) {
        return stratInitRestClient.post(SIRestPaths.SUBMIT_ERROR, request, Integer.class);
    }

    @Override
    public Result<SIUpdate> buildCity(SIUnitListJson request) {
        return stratInitRestClient.post(SIRestPaths.BUILD_CITY, request, SIUpdate.class);
    }

    @Override
    public Result<SIUpdate> switchTerrain(SIUnitListJson request) {
        return stratInitRestClient.post(SIRestPaths.SWITCH_TERRAIN, request, SIUpdate.class);
    }

    @Override
    public Result<SIUpdate> cancelMove(SIUnitListJson request) {
        return stratInitRestClient.post(SIRestPaths.CANCEL_MOVE, request, SIUpdate.class);
    }
}
