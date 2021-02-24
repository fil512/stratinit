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
public class RestClientStratInit implements StratInit {
    @Autowired
    RestClient restClient;

    @Override
    public Result<String> getVersion() {
        return restClient.get(StratInit.VERSION, String.class);
    }

    @Override
    public Result<List<SIUnitBase>> getUnitBases() {
        return restClient.getList(StratInit.UNIT_BASE, SIUnitBase.class);
    }

    @Override
    public Result<Properties> getServerConfig() {
        return restClient.get(StratInit.SERVER_CONFIG, Properties.class);
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
        return restClient.getList(StratInit.GAME_JOINED, SIGame.class);

    }

    @Override
    public Result<List<SIGame>> getUnjoinedGames() {
        return restClient.getList(StratInit.GAME_UNJOINED, SIGame.class);

    }

    @Override
    public Result<List<SISector>> getSectors() {
        return restClient.getList(StratInit.SECTOR, SISector.class);
    }

    @Override
    public Result<List<SIUnit>> getUnits() {
        return restClient.getList(StratInit.UNIT, SIUnit.class);
    }

    @Override
    public Result<List<SINation>> getNations() {
        return restClient.getList(StratInit.NATION, SINation.class);
    }

    @Override
    public Result<List<SICity>> getCities() {
        return restClient.getList(StratInit.CITY, SICity.class);
    }

    @Override
    public Result<List<SIBattleLog>> getBattleLog() {
        return restClient.getList(StratInit.BATTLE_LOG, SIBattleLog.class);
    }

    @Override
    public Result<SICity> updateCity(SICity sicity, UpdateCityField field) {
        return null;
    }

    @Override
    public Result<SIUpdate> getUpdate() {
        return restClient.get(StratInit.UPDATE, SIUpdate.class);
    }

    @Override
    public Result<SIUpdate> moveUnits(List<SIUnit> units, SectorCoords target) {
        return null;
    }

    @Override
    public Result<List<SIUnit>> getSeenUnits() {
        return restClient.getList(StratInit.UNIT_SEEN, SIUnit.class);
    }

    @Override
    public Result<List<SICity>> getSeenCities() {
        return restClient.getList(StratInit.CITY_SEEN, SICity.class);
    }

    @Override
    public Result<List<SIRelation>> getRelations() {
        return restClient.getList(StratInit.RELATION, SIRelation.class);
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
        return restClient.getList(StratInit.MESSAGE, SIMessage.class);
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
    public Result<List<SIMessage>> getMail() {
        return restClient.getList(StratInit.MESSAGE_MAIL, SIMessage.class);
    }

    @Override
    public Result<List<SIMessage>> getSentMail() {
        return restClient.getList(StratInit.MESSAGE_SENT, SIMessage.class);
    }

    @Override
    public Result<List<SIMessage>> getAnnouncements() {
        return restClient.getList(StratInit.MESSAGE_ANNOUNCEMENT, SIMessage.class);
    }

    @Override
    public Result<List<SINewsLogsDay>> getNewsLogs() {
        return restClient.getList(StratInit.NEWS_LOG, SINewsLogsDay.class);
    }

    @Override
    public Result<List<SIUnitBuilt>> getUnitsBuilt() {
        return restClient.getList(StratInit.UNIT_BUILT, SIUnitBuilt.class);
    }

    @Override
    public Result<SINation> getMyNation() {
        return restClient.get(StratInit.NATION_ME, SINation.class);
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
        return restClient.getList(StratInit.TEAM, SITeam.class);
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
