package com.kenstevens.stratinit.remote;

import com.kenstevens.stratinit.dto.*;
import com.kenstevens.stratinit.dto.news.SINewsLogsDay;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;

import java.util.List;
import java.util.Properties;

public interface StratInit {
	// TODO Return incremental battle logs and nation with all commands
	SIResponseEntity<String> getVersion();

	SIResponseEntity<List<SIUnitBase>> getUnitBases();

	SIResponseEntity<Properties> getServerConfig();

	SIResponseEntity<None> setGame(int gameId, boolean noAlliances);

	SIResponseEntity<Nation> joinGame(int gameId, boolean noAlliances);

	SIResponseEntity<List<SIGame>> getJoinedGames();

	SIResponseEntity<List<SIGame>> getUnjoinedGames();

	SIResponseEntity<List<SISector>> getSectors();

	SIResponseEntity<List<SIUnit>> getUnits();

	SIResponseEntity<List<SINation>> getNations();

	SIResponseEntity<List<SICity>> getCities();

	SIResponseEntity<List<SIBattleLog>> getBattleLog();

	SIResponseEntity<SICity> updateCity(SICity sicity, UpdateCityField field);

	SIResponseEntity<SIUpdate> getUpdate();

	SIResponseEntity<SIUpdate> moveUnits(List<SIUnit> units, SectorCoords target);

	SIResponseEntity<List<SIUnit>> getSeenUnits();

	SIResponseEntity<List<SICity>> getSeenCities();

	SIResponseEntity<List<SIRelation>> getRelations();

	SIResponseEntity<SIRelation> setRelation(int nationId, RelationType relationType);

	SIResponseEntity<SIUpdate> cedeUnits(List<SIUnit> units, int nationId);

	SIResponseEntity<SIUpdate> cedeCity(SICity city, int nationId);

	SIResponseEntity<List<SIMessage>> getMessages();

	SIResponseEntity<Integer> sendMessage(SIMessage message);

	SIResponseEntity<Integer> postAnnouncement(String subject, String body);

	SIResponseEntity<List<SISatellite>> getSattelites();

	SIResponseEntity<List<SIMessage>> getMail();

	SIResponseEntity<List<SIMessage>> getSentMail();

	SIResponseEntity<List<SIMessage>> getAnnouncements();

	SIResponseEntity<List<SINewsLogsDay>> getNewsLogs();

	SIResponseEntity<List<SIUnitBuilt>> getUnitsBuilt();

	SIResponseEntity<SINation> getMyNation();

	SIResponseEntity<Nation> joinGame(Player player, int gameId, boolean noAlliances);

	SIResponseEntity<None> shutdown();

	SIResponseEntity<SIUpdate> disbandUnits(List<SIUnit> siUnits);

	SIResponseEntity<List<SITeam>> getTeams();

	SIResponseEntity<SIUpdate> concede();

	SIResponseEntity<Integer> submitError(String subject, String stackTrace);

	SIResponseEntity<SIUpdate> buildCity(List<SIUnit> siunits);

	SIResponseEntity<SIUpdate> switchTerrain(List<SIUnit> siunits);

	SIResponseEntity<SIUpdate> cancelMoveOrder(List<SIUnit> siunits);

	// TODO OPT Add timestamp to units and cities and only send back data that has changed.
}

