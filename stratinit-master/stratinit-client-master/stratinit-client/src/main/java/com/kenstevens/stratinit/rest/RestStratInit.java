package com.kenstevens.stratinit.rest;

import com.kenstevens.stratinit.dto.*;
import com.kenstevens.stratinit.dto.news.SINewsLogsDay;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.StratInit;
import com.kenstevens.stratinit.remote.UpdateCityField;
import com.kenstevens.stratinit.remote.request.SetGameRequest;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

@Service
public class RestStratInit implements StratInit {
    @Autowired
    RestClient restClient;

    @Override
    public Result<String> getVersion() {
        return restClient.get(StratInit.VERSION, String.class);
    }

    @Override
    public Result<List<SIUnitBase>> getUnitBases() {
        return null;
    }

    @Override
    public Result<Properties> getServerConfig() {
        return null;
    }

    @Override
    public Result<None> setGame(SetGameRequest request) {
        return restClient.post(StratInit.SET_GAME, request, None.class);
    }

    @Override
    public Result<Nation> joinGame(SetGameRequest request) {
        return restClient.post(StratInit.JOIN_GAME, request, Nation.class);
    }

    @Override
    public Result<List<SIGame>> getJoinedGames() {
        return restClient.getList(StratInit.JOINED_GAMES, SIGame.class);

    }

    @Override
    public Result<List<SIGame>> getUnjoinedGames() {
        return restClient.getList(StratInit.UNJOINED_GAMES, SIGame.class);

    }

    @Override
    public Result<List<SISector>> getSectors() {
        return null;
    }

    @Override
    public Result<List<SIUnit>> getUnits() {
        return null;
    }

    @Override
    public Result<List<SINation>> getNations() {
        return null;
    }

    @Override
    public Result<List<SICity>> getCities() {
        return null;
    }

    @Override
    public Result<List<SIBattleLog>> getBattleLog() {
        return null;
    }

    @Override
    public Result<SICity> updateCity(SICity sicity, UpdateCityField field) {
        return null;
    }

    @Override
    public Result<SIUpdate> getUpdate() {
        return null;
    }

    @Override
    public Result<SIUpdate> moveUnits(List<SIUnit> units, SectorCoords target) {
        return null;
    }

    @Override
    public Result<List<SIUnit>> getSeenUnits() {
        return null;
    }

    @Override
    public Result<List<SICity>> getSeenCities() {
        return null;
    }

    @Override
    public Result<List<SIRelation>> getRelations() {
        return null;
    }

    @Override
    public Result<SIRelation> setRelation(int nationId, RelationType relationType) {
        return null;
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
        return null;
    }

    @Override
    public Result<Integer> sendMessage(SIMessage message) {
        return null;
    }

    @Override
    public Result<Integer> postAnnouncement(String subject, String body) {
        return null;
    }

    @Override
    public Result<List<SISatellite>> getSattelites() {
        return null;
    }

    @Override
    public Result<List<SIMessage>> getMail() {
        return null;
    }

    @Override
    public Result<List<SIMessage>> getSentMail() {
        return null;
    }

    @Override
    public Result<List<SIMessage>> getAnnouncements() {
        return null;
    }

    @Override
    public Result<List<SINewsLogsDay>> getNewsLogs() {
        return null;
    }

    @Override
    public Result<List<SIUnitBuilt>> getUnitsBuilt() {
        return null;
    }

    @Override
    public Result<SINation> getMyNation() {
        return null;
    }

    @Override
    public Result<Nation> joinGame(Player player, int gameId, boolean noAlliances) {
        return null;
    }

    @Override
    public Result<None> shutdown() {
        return null;
    }

    @Override
    public Result<SIUpdate> disbandUnits(List<SIUnit> siUnits) {
        return null;
    }

    @Override
    public Result<List<SITeam>> getTeams() {
        return null;
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
