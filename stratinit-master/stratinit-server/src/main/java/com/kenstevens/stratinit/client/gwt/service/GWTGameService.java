package com.kenstevens.stratinit.client.gwt.service;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.kenstevens.stratinit.client.gwt.callback.ServiceSecurityException;
import com.kenstevens.stratinit.client.gwt.model.GWTBuildAudit;
import com.kenstevens.stratinit.client.gwt.model.GWTCity;
import com.kenstevens.stratinit.client.gwt.model.GWTGame;
import com.kenstevens.stratinit.client.gwt.model.GWTPlayer;
import com.kenstevens.stratinit.client.gwt.model.GWTSectorCoords;
import com.kenstevens.stratinit.client.gwt.model.GWTUnit;
import com.kenstevens.stratinit.client.gwt.model.GWTUpdate;

@RemoteServiceRelativePath("game.rpc")
public interface GWTGameService extends RemoteService {

	GWTResult<GWTNone> createGame(String name);
	GWTResult<GWTNone> createBlitzGame(String name, int islands);
	GWTResult<GWTNone> removeGame(int id);
	GWTResult<GWTNone> endGame(int id);
	GWTResult<GWTNone> setGame(int gameId);
	List<GWTGame> getUnjoinedGames() throws ServiceSecurityException;
	List<GWTGame> getJoinedGames() throws ServiceSecurityException;
	GWTResult<String> getGameMap(int id);
	GWTUpdate getUpdate();
	List<GWTUnit> getUnits();
	List<GWTCity> getCities();
	GWTResult<GWTUpdate> updateCity(GWTCity city, boolean isBuild);
	GWTResult<GWTUpdate> moveUnits(List<GWTUnit> units, GWTSectorCoords coords);
	GWTResult<Integer> joinGame(int id) throws ServiceSecurityException;
	List<GWTBuildAudit> fetchBuildAudit();
	GWTResult<GWTNone> shutdown() throws ServiceSecurityException;
	GWTResult<GWTNone> updatePlayer(String newPassword, String email, boolean emailGameMail) throws ServiceSecurityException;
	GWTResult<GWTPlayer> fetchPlayer() throws ServiceSecurityException;
	GWTResult<Integer> postAnnouncement(String subject, String body) throws ServiceSecurityException;

	public static final class Util {
		private static GWTGameServiceAsync instance;
		private Util() {}
		public static GWTGameServiceAsync getInstance() {
			if (instance == null) {
				instance = GWT.create(GWTGameService.class);
			}
			return instance;
		}
	}
}