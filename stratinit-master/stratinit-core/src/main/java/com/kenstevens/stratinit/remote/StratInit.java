package com.kenstevens.stratinit.remote;

import com.kenstevens.stratinit.dto.*;
import com.kenstevens.stratinit.dto.news.SINewsLogsDay;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;

import javax.jws.WebService;
import java.util.List;
import java.util.Properties;

// FIXME get rid of @WebService so we can bump to Java 11
@WebService
public interface StratInit {
	// TODO Return incremental battle logs and nation with all commands
	Result<String> getVersion();

	Result<List<SIUnitBase>> getUnitBases();

	Result<Properties> getServerConfig();

	Result<None> setGame(int gameId, boolean noAlliances);

	Result<Nation> joinGame(int gameId, boolean noAlliances);

	Result<List<SIGame>> getJoinedGames();

	Result<List<SIGame>> getUnjoinedGames();

	Result<List<SISector>> getSectors();

	Result<List<SIUnit>> getUnits();

	Result<List<SINation>> getNations();

	Result<List<SICity>> getCities();

	Result<List<SIBattleLog>> getBattleLog();

	Result<SICity> updateCity(SICity sicity, UpdateCityField field);

	Result<SIUpdate> getUpdate();

	Result<SIUpdate> moveUnits(List<SIUnit>units, SectorCoords target);

	Result<List<SIUnit>> getSeenUnits();

	Result<List<SICity>> getSeenCities();

	Result<List<SIRelation>> getRelations();

	Result<SIRelation> setRelation(int nationId, RelationType relationType);

	Result<SIUpdate> cedeUnits(List<SIUnit> units, int nationId);

	Result<SIUpdate> cedeCity(SICity city, int nationId);

	Result<List<SIMessage>> getMessages();

	Result<Integer>sendMessage(SIMessage message);

	Result<Integer>postAnnouncement(String subject, String body);

	Result<List<SISatellite>> getSattelites();

	Result<List<SIMessage>> getMail();

	Result<List<SIMessage>> getSentMail();

	Result<List<SIMessage>> getAnnouncements();

	Result<List<SINewsLogsDay>> getNewsLogs();

	Result<List<SIUnitBuilt>> getUnitsBuilt();

	Result<SINation> getMyNation();

	Result<Nation> joinGame(Player player, int gameId, boolean noAlliances);

	Result<None> shutdown();

	Result<SIUpdate> disbandUnits(List<SIUnit> siUnits);

	Result<List<SITeam>> getTeams();

	Result<SIUpdate> concede();

	Result<Integer> submitError(String subject, String stackTrace);

	Result<SIUpdate> buildCity(List<SIUnit> siunits);

	Result<SIUpdate> switchTerrain(List<SIUnit> siunits);

	Result<SIUpdate> cancelMoveOrder(List<SIUnit> siunits);

	// TODO OPT Add timestamp to units and cities and only send back data that has changed.
}

