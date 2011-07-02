package com.kenstevens.stratinit.remote;

import java.util.List;
import java.util.Properties;

import javax.jws.WebService;

import com.kenstevens.stratinit.dto.SIBattleLog;
import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.dto.SIGame;
import com.kenstevens.stratinit.dto.SIMessage;
import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.dto.SIRelation;
import com.kenstevens.stratinit.dto.SISatellite;
import com.kenstevens.stratinit.dto.SISector;
import com.kenstevens.stratinit.dto.SITeam;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.dto.SIUnitBase;
import com.kenstevens.stratinit.dto.SIUnitBuilt;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.dto.news.SINewsLogsDay;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
@WebService
public interface StratInit {
// TODO * Return incremental battle logs and nation with all commands
	Result<String> getVersion();
	Result<List<SIUnitBase>> getUnitBases();
	Result<Properties> getServerConfig();
	Result<None> setGame(int gameId);
	Result<Nation> joinGame(int gameId);
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
	Result<Nation> joinGame(Player player, int gameId);
	Result<None> shutdown();
	Result<SIUpdate> disbandUnits(List<SIUnit> siUnits);
	Result<List<SITeam>> getTeams();
	Result<SIUpdate> concede();
	Result<None> submitError(String subject, Exception exception);
	Result<SIUpdate> buildCity(List<SIUnit> siunits);
	Result<SIUpdate> switchTerrain(List<SIUnit> siunits);
	Result<SIUpdate> cancelMoveOrder(List<SIUnit> siunits);

	// TODO OPT Add timestamp to units and cities and only send back data that has changed.
}

