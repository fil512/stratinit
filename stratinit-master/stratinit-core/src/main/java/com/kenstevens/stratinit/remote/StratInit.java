package com.kenstevens.stratinit.remote;

import com.kenstevens.stratinit.dto.*;
import com.kenstevens.stratinit.dto.news.SINewsLogsDay;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.remote.request.SetGameRequest;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Properties;

public interface StratInit {
	String VERSION = "/version";
	String SERVER_CONFIG = "/serverConfig";
	// FIXME collapse into one /game call with a parameter
	String GAME_JOINED = "/joinedGames";
	String GAME_UNJOINED = "/unjoinedGames";
	String SET_GAME = "/setGame";
	String JOIN_GAME = "/joinGame";
	String SECTOR = "/sector";
	String UNIT_BASE = "/unitbase";
	// FIXME consolidate
	String UNIT = "/unit";
	String UNIT_SEEN = "/unit-seen";
	// FIXME consolidate
	String NATION = "/nation";
	String NATION_ME = "/nation-me";
	// FIXME consolidate
	String CITY = "/city";
	String CITY_SEEN = "/city-seen";
	String BATTLE_LOG = "/battlelog";
	String UPDATE = "/update";
	String RELATION = "/relation";
	// FIXME consolidate with params
	String MESSAGE = "/message";
	String MESSAGE_MAIL = "/message-mail";
	String MESSAGE_SENT = "/message-sent";
	String MESSAGE_ANNOUNCEMENT = "/message-announcement";
	String NEWS_LOG = "/newslog";
	String UNIT_BUILT = "/unitbuilt";
	String TEAM = "/team";

	// TODO Return incremental battle logs and nation with all commands
	@GetMapping(value = VERSION)
	Result<String> getVersion();

	@GetMapping(value = UNIT_BASE)
	Result<List<SIUnitBase>> getUnitBases();

	@GetMapping(value = SERVER_CONFIG)
	Result<Properties> getServerConfig();

	@PostMapping(value = SET_GAME)
	Result<None> setGame(@RequestBody SetGameRequest request);

	@PostMapping(value = JOIN_GAME)
	Result<Nation> joinGame(@RequestBody SetGameRequest request);

	@GetMapping(value = GAME_JOINED)
	Result<List<SIGame>> getJoinedGames();

	@GetMapping(value = GAME_UNJOINED)
	Result<List<SIGame>> getUnjoinedGames();

	@GetMapping(value = SECTOR)
	Result<List<SISector>> getSectors();

	@GetMapping(value = UNIT)
	Result<List<SIUnit>> getUnits();

	@GetMapping(value = NATION)
	Result<List<SINation>> getNations();

	@GetMapping(value = CITY)
	Result<List<SICity>> getCities();

	@GetMapping(value = BATTLE_LOG)
	Result<List<SIBattleLog>> getBattleLog();

	Result<SICity> updateCity(SICity sicity, UpdateCityField field);

	@GetMapping(value = UPDATE)
	Result<SIUpdate> getUpdate();

	Result<SIUpdate> moveUnits(List<SIUnit> units, SectorCoords target);

	@GetMapping(value = UNIT_SEEN)
	Result<List<SIUnit>> getSeenUnits();

	// FIXME collapse with city
	@GetMapping(value = CITY_SEEN)
	Result<List<SICity>> getSeenCities();

	@GetMapping(value = RELATION)
	Result<List<SIRelation>> getRelations();

	// FIXME finish the rest of the puts
	Result<SIRelation> setRelation(int nationId, RelationType relationType);

	Result<SIUpdate> cedeUnits(List<SIUnit> units, int nationId);

	Result<SIUpdate> cedeCity(SICity city, int nationId);

	@GetMapping(value = MESSAGE)
	Result<List<SIMessage>> getMessages();

	Result<Integer> sendMessage(SIMessage message);

	Result<Integer> postAnnouncement(String subject, String body);

	@GetMapping(value = MESSAGE_MAIL)
	Result<List<SIMessage>> getMail();

	@GetMapping(value = MESSAGE_SENT)
	Result<List<SIMessage>> getSentMail();

	@GetMapping(value = MESSAGE_ANNOUNCEMENT)
	Result<List<SIMessage>> getAnnouncements();

	@GetMapping(value = NEWS_LOG)
	Result<List<SINewsLogsDay>> getNewsLogs();

	@GetMapping(value = UNIT_BUILT)
	Result<List<SIUnitBuilt>> getUnitsBuilt();

	@GetMapping(value = NATION_ME)
	Result<SINation> getMyNation();

	Result<Nation> joinGame(Player player, int gameId, boolean noAlliances);

	// FIXME move to admin service
	Result<None> shutdown();

	Result<SIUpdate> disbandUnits(List<SIUnit> siUnits);

	@GetMapping(value = TEAM)
	Result<List<SITeam>> getTeams();

	Result<SIUpdate> concede();

	Result<Integer> submitError(String subject, String stackTrace);

	Result<SIUpdate> buildCity(List<SIUnit> siunits);

	Result<SIUpdate> switchTerrain(List<SIUnit> siunits);

	Result<SIUpdate> cancelMoveOrder(List<SIUnit> siunits);

	// TODO OPT Add timestamp to units and cities and only send back data that has changed.
}

