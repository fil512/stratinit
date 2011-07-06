package com.kenstevens.stratinit.server.remote;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
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
import com.kenstevens.stratinit.main.Spring;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.model.UnitBase;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.StratInit;
import com.kenstevens.stratinit.remote.UpdateCityField;
import com.kenstevens.stratinit.server.remote.mail.SMTPService;
import com.kenstevens.stratinit.server.remote.request.GetAnnouncementsRequest;
import com.kenstevens.stratinit.server.remote.request.GetCitiesRequest;
import com.kenstevens.stratinit.server.remote.request.GetJoinedGamesRequest;
import com.kenstevens.stratinit.server.remote.request.GetLogsRequest;
import com.kenstevens.stratinit.server.remote.request.GetMessagesRequest;
import com.kenstevens.stratinit.server.remote.request.GetMyNationRequest;
import com.kenstevens.stratinit.server.remote.request.GetNationsRequest;
import com.kenstevens.stratinit.server.remote.request.GetRelationsRequest;
import com.kenstevens.stratinit.server.remote.request.GetSattelitesRequest;
import com.kenstevens.stratinit.server.remote.request.GetSectorsRequest;
import com.kenstevens.stratinit.server.remote.request.GetSeenCitiesRequest;
import com.kenstevens.stratinit.server.remote.request.GetSeenUnitsRequest;
import com.kenstevens.stratinit.server.remote.request.GetSentMailRequest;
import com.kenstevens.stratinit.server.remote.request.GetTeamsRequest;
import com.kenstevens.stratinit.server.remote.request.GetUnitsBuiltRequest;
import com.kenstevens.stratinit.server.remote.request.GetUnitsRequest;
import com.kenstevens.stratinit.server.remote.request.GetUnjoinedGamesRequest;
import com.kenstevens.stratinit.server.remote.request.GetUpdateRequest;
import com.kenstevens.stratinit.server.remote.request.write.BuildCityRequest;
import com.kenstevens.stratinit.server.remote.request.write.CancelMoveOrderRequest;
import com.kenstevens.stratinit.server.remote.request.write.CedeCityRequest;
import com.kenstevens.stratinit.server.remote.request.write.CedeUnitsRequest;
import com.kenstevens.stratinit.server.remote.request.write.ConcedeRequest;
import com.kenstevens.stratinit.server.remote.request.write.DisbandUnitRequest;
import com.kenstevens.stratinit.server.remote.request.write.GetBattleLogRequest;
import com.kenstevens.stratinit.server.remote.request.write.GetMailRequest;
import com.kenstevens.stratinit.server.remote.request.write.JoinGameRequest;
import com.kenstevens.stratinit.server.remote.request.write.MoveUnitsRequest;
import com.kenstevens.stratinit.server.remote.request.write.PostAnnouncementRequest;
import com.kenstevens.stratinit.server.remote.request.write.SendMessageRequest;
import com.kenstevens.stratinit.server.remote.request.write.SetGameRequest;
import com.kenstevens.stratinit.server.remote.request.write.SetRelationRequest;
import com.kenstevens.stratinit.server.remote.request.write.SwitchTerrainRequest;
import com.kenstevens.stratinit.server.remote.request.write.UpdateCityRequest;
import com.kenstevens.stratinit.server.remote.state.ServerStatus;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.RelationType;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

@Component("stratInit")
public class StratInitImpl implements StratInit {
	@Autowired
	private Spring spring;
	@Autowired
	private ServerManager serverManager;
	@Autowired
	private ServerStatus serverStatus;
	@Autowired
	private SMTPService smtpService;

	/*
	 * Non database requests
	 */

	@Override
	public Result<String> getVersion() {
		return Result.make(Constants.SERVER_VERSION);
	}

	@Override
	public Result<List<SIUnitBase>> getUnitBases() {
		List<SIUnitBase> result = Lists.newArrayList(Collections2.transform(
				Lists.newArrayList(UnitType.values()),
				new Function<UnitType, SIUnitBase>() {
					@Override
					public SIUnitBase apply(UnitType unitType) {
						return new SIUnitBase(UnitBase.getUnitBase(unitType));
					}
				}));
		return Result.make(result);
	}

	@Override
	public Result<Properties> getServerConfig() {
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
		return Result.make(properties);
	}

	public Result<None> setGame(int gameId) {
		return spring.autowire(new SetGameRequest(gameId)).process(gameId);
	}

	/*
	 * Read-only database requests
	 */

	public Result<List<SIGame>> getJoinedGames() {
		return spring.getBean(GetJoinedGamesRequest.class).processNoGame();
	}

	public Result<List<SIGame>> getUnjoinedGames() {
		return spring.getBean(GetUnjoinedGamesRequest.class).processNoGame();
	}

	public Result<List<SINation>> getNations() {
		return spring.getBean(GetNationsRequest.class).process();
	}

	public Result<List<SISector>> getSectors() {
		return spring.getBean(GetSectorsRequest.class).process();
	}

	@Override
	public Result<List<SIUnit>> getUnits() {
		return spring.getBean(GetUnitsRequest.class).process();
	}

	@Override
	public Result<List<SIUnit>> getSeenUnits() {
		return spring.getBean(GetSeenUnitsRequest.class).process();
	}

	@Override
	public Result<SINation> getMyNation() {
		return spring.getBean(GetMyNationRequest.class).process();
	}

	@Override
	public Result<List<SICity>> getCities() {
		return spring.getBean(GetCitiesRequest.class).process();
	}

	@Override
	public Result<List<SICity>> getSeenCities() {
		return spring.getBean(GetSeenCitiesRequest.class).process();
	}

	@Override
	public Result<SIUpdate> getUpdate() {
		return spring.getBean(GetUpdateRequest.class).process();
	}

	@Override
	public Result<List<SIBattleLog>> getBattleLog() {
		return spring.getBean(GetBattleLogRequest.class).process();
	}

	@Override
	public Result<List<SIMessage>> getMail() {
		return spring.getBean(GetMailRequest.class).process();
	}

	@Override
	public Result<List<SIRelation>> getRelations() {
		return spring.getBean(GetRelationsRequest.class).process();
	}

	@Override
	public Result<List<SIMessage>> getMessages() {
		return spring.getBean(GetMessagesRequest.class).process();
	}

	@Override
	public Result<List<SIMessage>> getSentMail() {
		return spring.getBean(GetSentMailRequest.class).process();
	}

	@Override
	public Result<List<SIMessage>> getAnnouncements() {
		return spring.getBean(GetAnnouncementsRequest.class).process();
	}

	@Override
	public Result<List<SISatellite>> getSattelites() {
		return spring.getBean(GetSattelitesRequest.class).process();
	}

	@Override
	public Result<List<SIUnitBuilt>> getUnitsBuilt() {
		return spring.getBean(GetUnitsBuiltRequest.class).process();
	}

	/*
	 * Database update requests
	 */

	@Override
	public Result<SICity> updateCity(SICity sicity, UpdateCityField field) {
		return spring.autowire(new UpdateCityRequest(sicity, field)).process();
	}

	@Override
	public Result<SIUpdate> moveUnits(List<SIUnit> units, SectorCoords target) {
		return spring.autowire(new MoveUnitsRequest(units, target)).process();
	}

	@Override
	public Result<SIUpdate> cedeUnits(List<SIUnit> siunits, int nationId) {
		return spring.autowire(new CedeUnitsRequest(siunits, nationId)).process();
	}

	@Override
	public Result<SIUpdate> cedeCity(SICity sicity, int nationId) {
		return spring.autowire(new CedeCityRequest(sicity, nationId)).process();
	}

	@Override
	public Result<SIRelation> setRelation(int nationId,
			RelationType relationType) {
		return spring.autowire(new SetRelationRequest(nationId, relationType))
				.process();
	}

	@Override
	public Result<Integer> sendMessage(SIMessage simessage) {
		return spring.autowire(new SendMessageRequest(simessage)).process();
	}

	@Override
	public Result<Nation> joinGame(Player player, int gameId) {
		return spring.autowire(new JoinGameRequest(player, gameId)).process(
				gameId);
	}

	@Override
	public synchronized Result<None> shutdown() {
		// TODO SEC block access to this method to admin only
		if (!serverStatus.isRunning()) {
			return new Result<None>("The server is not running.", false);
		}
		serverManager.shutdown();
		return new Result<None>("SERVER SHUTDOWN COMPLETE", true);
	}

	@Override
	public Result<Nation> joinGame(int gameId) {
		return spring.autowire(new JoinGameRequest(null, gameId)).process(
				gameId);
	}

	@Override
	public Result<SIUpdate> disbandUnits(List<SIUnit> siunits) {
		return spring.autowire(new DisbandUnitRequest(siunits)).process();
	}

	@Override
	public Result<SIUpdate> cancelMoveOrder(List<SIUnit> siunits) {
		return spring.autowire(new CancelMoveOrderRequest(siunits)).process();
	}

	@Override
	public Result<SIUpdate> buildCity(List<SIUnit> siunits) {
		return spring.autowire(new BuildCityRequest(siunits)).process();
	}

	@Override
	public Result<SIUpdate> switchTerrain(List<SIUnit> siunits) {		
		return spring.autowire(new SwitchTerrainRequest(siunits)).process();
	}

	@Override
	public Result<List<SINewsLogsDay>> getNewsLogs() {
		return spring.getBean(GetLogsRequest.class).process();
	}

	@Override
	public Result<Integer> postAnnouncement(String subject, String body) {
		return spring.autowire(new PostAnnouncementRequest(subject, body)).processNoGame();
	}

	@Override
	public Result<List<SITeam>> getTeams() {
		return spring.getBean(GetTeamsRequest.class).process();
	}

	@Override
	public Result<SIUpdate> concede() {
		return spring.autowire(new ConcedeRequest()).process();
	}

	@Override
	public Result<None> submitError(String subject, Exception exception) {
		smtpService.sendException(subject, exception);
		return Result.trueInstance();
	}
}
