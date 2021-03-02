package com.kenstevens.stratinit.rest;

import com.kenstevens.stratinit.dto.*;
import com.kenstevens.stratinit.dto.news.SINewsLogsDay;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.MoveUnitsJson;
import com.kenstevens.stratinit.remote.request.SetGameJson;
import com.kenstevens.stratinit.remote.request.SetRelationJson;
import com.kenstevens.stratinit.remote.request.UpdateCityJson;

import java.util.List;

public interface IStratInitServer {

	Result<String> getVersion();

	Result<None> setGame(SetGameJson request);

	Result<SINation> joinGame(SetGameJson request);

	Result<List<SIGame>> getJoinedGames();

	Result<List<SIGame>> getUnjoinedGames();

	Result<List<SISector>> getSectors();

	Result<List<SIUnit>> getUnits();

	Result<List<SINation>> getNations();

	Result<List<SICity>> getCities();

	Result<List<SIBattleLog>> getBattleLog();

	Result<SICity> updateCity(UpdateCityJson request);

	Result<SIUpdate> getUpdate();

	Result<SIUpdate> moveUnits(MoveUnitsJson request);

	Result<List<SIUnit>> getSeenUnits();

	Result<List<SICity>> getSeenCities();

	Result<List<SIRelation>> getRelations();

	// FIXME finish the rest of the puts
	Result<SIRelation> setRelation(SetRelationJson request);

	Result<SIUpdate> cedeUnits(List<SIUnit> units, int nationId);

	Result<SIUpdate> cedeCity(SICity city, int nationId);

	Result<List<SIMessage>> getMessages();

	Result<Integer> sendMessage(SIMessage message);

	Result<List<SIMessage>> getMail();

	Result<List<SIMessage>> getSentMail();

	Result<List<SIMessage>> getAnnouncements();

	Result<List<SINewsLogsDay>> getNewsLogs();

	Result<List<SIUnitBuilt>> getUnitsBuilt();

	Result<SINation> getMyNation();

	Result<SIUpdate> disbandUnits(List<SIUnit> siUnits);

	Result<List<SITeam>> getTeams();

	Result<SIUpdate> concede();

	Result<Integer> submitError(String subject, String stackTrace);

	Result<SIUpdate> buildCity(List<SIUnit> siunits);

	Result<SIUpdate> switchTerrain(List<SIUnit> siunits);

	Result<SIUpdate> cancelMoveOrder(List<SIUnit> siunits);

	// TODO OPT Add timestamp to units and cities and only send back data that has changed.
}

