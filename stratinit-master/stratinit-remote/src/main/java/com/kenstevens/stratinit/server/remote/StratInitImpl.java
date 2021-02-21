package com.kenstevens.stratinit.server.remote;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.kenstevens.stratinit.dto.*;
import com.kenstevens.stratinit.dto.news.SINewsLogsDay;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.model.UnitBase;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.SIResponseEntity;
import com.kenstevens.stratinit.remote.StratInit;
import com.kenstevens.stratinit.remote.UpdateCityField;
import com.kenstevens.stratinit.server.remote.helper.ErrorProcessor;
import com.kenstevens.stratinit.server.remote.request.RequestFactory;
import com.kenstevens.stratinit.server.remote.state.ServerStatus;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;

@RestController
@RequestMapping("/stratinit")
//@Component("stratInit")
public class StratInitImpl implements StratInit {
	@Autowired
	private RequestFactory requestFactory;
	@Autowired
	private ServerManager serverManager;
	@Autowired
	private ServerStatus serverStatus;
	@Autowired
	private ErrorProcessor errorProcessor;

	/*
	 * Non database requests
	 */

	@Override
	@GetMapping(value = "/version")
	public SIResponseEntity<String> getVersion() {
		return SIResponseEntity.make(Constants.SERVER_VERSION);
	}

	@Override
	public SIResponseEntity<List<SIUnitBase>> getUnitBases() {
		List<SIUnitBase> result = Lists.newArrayList(Collections2.transform(
				Lists.newArrayList(UnitType.values()),
				new Function<UnitType, SIUnitBase>() {
					@Override
					public SIUnitBase apply(UnitType unitType) {
						return new SIUnitBase(UnitBase.getUnitBase(unitType));
					}
				}));
		return SIResponseEntity.make(result);
	}

	@Override
	@GetMapping(value = "/serverConfig")
	public SIResponseEntity<Properties> getServerConfig() {
		Properties properties = new Properties();
		Class<Constants> clazz = Constants.class;
		for (Field field : clazz.getFields()) {
			try {
				properties.setProperty(field.getName(), field.get(clazz)
						.toString());
			} catch (IllegalAccessException ex) {
				// won't happen
			}
		}
		return SIResponseEntity.make(properties);
	}

	// FIXME Log this
	// FIXME What does "Log this" mean?
	public SIResponseEntity<None> setGame(int gameId, boolean noAlliances) {
		return requestFactory.getSetGameRequest(gameId, noAlliances).process(gameId);
	}

	/*
	 * Read-only database requests
	 */

	@GetMapping(value = "/joinedGames")
	public SIResponseEntity<List<SIGame>> getJoinedGames() {
		return requestFactory.getGetJoinedGamesRequest().processNoGame();
	}

	public SIResponseEntity<List<SIGame>> getUnjoinedGames() {
		return requestFactory.getGetUnjoinedGamesRequest().processNoGame();
	}

	public SIResponseEntity<List<SINation>> getNations() {
		return requestFactory.getGetNationsRequest().process();
	}

	public SIResponseEntity<List<SISector>> getSectors() {
		return requestFactory.getGetSectorsRequest().process();
	}

	@Override
	public SIResponseEntity<List<SIUnit>> getUnits() {
		return requestFactory.getGetUnitsRequest().process();
	}

	@Override
	public SIResponseEntity<List<SIUnit>> getSeenUnits() {
		return requestFactory.getGetSeenUnitsRequest().process();
	}

	@Override
	public SIResponseEntity<SINation> getMyNation() {
		return requestFactory.getGetMyNationRequest().process();
	}

	@Override
	public SIResponseEntity<List<SICity>> getCities() {
		return requestFactory.getGetCitiesRequest().process();
	}

	@Override
	public SIResponseEntity<List<SICity>> getSeenCities() {
		return requestFactory.getGetSeenCitiesRequest().process();
	}

	@Override
	public SIResponseEntity<SIUpdate> getUpdate() {
		return requestFactory.getGetUpdateRequest().process();
	}

	@Override
	public SIResponseEntity<List<SIBattleLog>> getBattleLog() {
		return requestFactory.getGetBattleLogRequest().process();
	}

	@Override
	public SIResponseEntity<List<SIMessage>> getMail() {
		return requestFactory.getGetMailRequest().process();
	}

	@Override
	public SIResponseEntity<List<SIRelation>> getRelations() {
		return requestFactory.getGetRelationsRequest().process();
	}

	@Override
	public SIResponseEntity<List<SIMessage>> getMessages() {
		return requestFactory.getGetMessagesRequest().process();
	}

	@Override
	public SIResponseEntity<List<SIMessage>> getSentMail() {
		return requestFactory.getGetSentMailRequest().process();
	}

	@Override
	public SIResponseEntity<List<SIMessage>> getAnnouncements() {
		return requestFactory.getGetAnnouncementsRequest().process();
	}

	@Override
	public SIResponseEntity<List<SISatellite>> getSattelites() {
		return requestFactory.getGetSattelitesRequest().process();
	}

	@Override
	public SIResponseEntity<List<SIUnitBuilt>> getUnitsBuilt() {
		return requestFactory.getGetUnitsBuiltRequest().process();
	}

	/*
	 * Database update requests
	 */

	@Override
	public SIResponseEntity<SICity> updateCity(SICity sicity, UpdateCityField field) {
		return requestFactory.getUpdateCityRequest(sicity, field).process();
	}

	@Override
	public SIResponseEntity<SIUpdate> moveUnits(List<SIUnit> units, SectorCoords target) {
		return requestFactory.getMoveUnitsRequest(units, target).process();
	}

	@Override
	public SIResponseEntity<SIUpdate> cedeUnits(List<SIUnit> siunits, int nationId) {
		return requestFactory.getCedeUnitsRequest(siunits, nationId).process();
	}

	@Override
	public SIResponseEntity<SIUpdate> cedeCity(SICity sicity, int nationId) {
		return requestFactory.getCedeCityRequest(sicity, nationId).process();
	}

	@Override
	public SIResponseEntity<SIRelation> setRelation(int nationId,
													RelationType relationType) {
		return requestFactory.getSetRelationRequest(nationId, relationType)
				.process();
	}

	@Override
	public SIResponseEntity<Integer> sendMessage(SIMessage simessage) {
		return requestFactory.getSendMessageRequest(simessage).process();
	}

	@Override
	public SIResponseEntity<Nation> joinGame(Player player, int gameId, boolean noAlliances) {
		return requestFactory.getJoinGameRequest(player, gameId, noAlliances).process(
				gameId);
	}

	@Override
	public synchronized SIResponseEntity<None> shutdown() {
		// TODO SEC block access to this method to admin only
		if (!serverStatus.isRunning()) {
			return SIResponseEntity.failure("The server is not running.");
		}
		serverManager.shutdown();
		return SIResponseEntity.success("SERVER SHUTDOWN COMPLETE");
	}

	@Override
	public SIResponseEntity<Nation> joinGame(int gameId, boolean noAlliances) {
		return requestFactory.getJoinGameRequest(null, gameId, noAlliances).process(
				gameId);
	}

	@Override
	public SIResponseEntity<SIUpdate> disbandUnits(List<SIUnit> siunits) {
		return requestFactory.getDisbandUnitRequest(siunits).process();
	}

	@Override
	public SIResponseEntity<SIUpdate> cancelMoveOrder(List<SIUnit> siunits) {
		return requestFactory.getCancelMoveOrderRequest(siunits).process();
	}

	@Override
	public SIResponseEntity<SIUpdate> buildCity(List<SIUnit> siunits) {
		return requestFactory.getBuildCityRequest(siunits).process();
	}

	@Override
	public SIResponseEntity<SIUpdate> switchTerrain(List<SIUnit> siunits) {
		return requestFactory.getSwitchTerrainRequest(siunits).process();
	}

	@Override
	public SIResponseEntity<List<SINewsLogsDay>> getNewsLogs() {
		return requestFactory.getGetLogsRequest().process();
	}

	@Override
	public SIResponseEntity<Integer> postAnnouncement(String subject, String body) {
		return requestFactory.getPostAnnouncementRequest(subject, body).processNoGame();
	}

	@Override
	public SIResponseEntity<List<SITeam>> getTeams() {
		return requestFactory.getGetTeamsRequest().process();
	}

	@Override
	public SIResponseEntity<SIUpdate> concede() {
		return requestFactory.getConcedeRequest().process();
	}

	@Override
	public SIResponseEntity<Integer> submitError(String subject, String stackTrace) {
		return errorProcessor.processError(subject, stackTrace);
	}
}
