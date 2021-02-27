package com.kenstevens.stratinit.server;

import com.kenstevens.stratinit.dto.*;
import com.kenstevens.stratinit.dto.news.SINewsLogsDay;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.UpdateCityField;
import com.kenstevens.stratinit.remote.request.SetGameRequest;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface StratInitServer {

	Result<String> getVersion();

	Result<None> setGame(@RequestBody SetGameRequest request);

	Result<SINation> joinGame(@RequestBody SetGameRequest request);

	Result<List<SIGame>> getJoinedGames();

	Result<List<SIGame>> getUnjoinedGames();

	Result<List<SISector>> getSectors();

	Result<List<SIUnit>> getUnits();

	Result<List<SINation>> getNations();

	Result<List<SICity>> getCities();

	Result<List<SIBattleLog>> getBattleLog();

	Result<SICity> updateCity(SICity sicity, UpdateCityField field);

	Result<SIUpdate> getUpdate();

	Result<SIUpdate> moveUnits(List<SIUnit> units, SectorCoords target);

	Result<List<SIUnit>> getSeenUnits();

	Result<List<SICity>> getSeenCities();

	Result<List<SIRelation>> getRelations();

	// FIXME finish the rest of the puts
	Result<SIRelation> setRelation(int nationId, RelationType relationType);

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

	Result<SINation> joinGame(Player player, int gameId, boolean noAlliances);

	Result<SIUpdate> disbandUnits(List<SIUnit> siUnits);

	Result<List<SITeam>> getTeams();

	Result<SIUpdate> concede();

	Result<Integer> submitError(String subject, String stackTrace);

	Result<SIUpdate> buildCity(List<SIUnit> siunits);

	Result<SIUpdate> switchTerrain(List<SIUnit> siunits);

	Result<SIUpdate> cancelMoveOrder(List<SIUnit> siunits);

	// TODO OPT Add timestamp to units and cities and only send back data that has changed.
}

