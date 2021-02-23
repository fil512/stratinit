package com.kenstevens.stratinit.remote;

import com.kenstevens.stratinit.dto.*;
import com.kenstevens.stratinit.dto.news.SINewsLogsDay;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.remote.request.SetGameRequest;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Properties;

public interface StratInit {
	String VERSION = "/version";
	String SERVER_CONFIG = "/serverConfig";
	String JOINED_GAMES = "/joinedGames";
	String UNJOINED_GAMES = "/unjoinedGames";
	String SET_GAME = "/setGame";
	String JOIN_GAME = "/joinGame";

	// TODO Return incremental battle logs and nation with all commands
	@GetMapping(value = VERSION)
	Result<String> getVersion();

	Result<List<SIUnitBase>> getUnitBases();

	@GetMapping(value = SERVER_CONFIG)
	Result<Properties> getServerConfig();

	@PreAuthorize(Roles.USER)
	@PostMapping(value = SET_GAME)
	Result<None> setGame(@RequestBody SetGameRequest request);

	@PreAuthorize(Roles.USER)
	@PostMapping(value = JOIN_GAME)
	Result<Nation> joinGame(@RequestBody SetGameRequest request);

	@PreAuthorize(Roles.USER)
	@GetMapping(value = JOINED_GAMES)
	Result<List<SIGame>> getJoinedGames();

	@PreAuthorize(Roles.USER)
	@GetMapping(value = UNJOINED_GAMES)
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

	Result<SIRelation> setRelation(int nationId, RelationType relationType);

	Result<SIUpdate> cedeUnits(List<SIUnit> units, int nationId);

	Result<SIUpdate> cedeCity(SICity city, int nationId);

	Result<List<SIMessage>> getMessages();

	Result<Integer> sendMessage(SIMessage message);

	Result<Integer> postAnnouncement(String subject, String body);

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

