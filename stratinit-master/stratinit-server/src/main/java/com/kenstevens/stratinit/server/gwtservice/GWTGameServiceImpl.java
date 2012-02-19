package com.kenstevens.stratinit.server.gwtservice;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.providers.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.client.gwt.callback.ServiceSecurityException;
import com.kenstevens.stratinit.client.gwt.model.GWTBuildAudit;
import com.kenstevens.stratinit.client.gwt.model.GWTCity;
import com.kenstevens.stratinit.client.gwt.model.GWTGame;
import com.kenstevens.stratinit.client.gwt.model.GWTPlayer;
import com.kenstevens.stratinit.client.gwt.model.GWTSectorCoords;
import com.kenstevens.stratinit.client.gwt.model.GWTUnit;
import com.kenstevens.stratinit.client.gwt.model.GWTUpdate;
import com.kenstevens.stratinit.client.gwt.service.GWTGameService;
import com.kenstevens.stratinit.client.gwt.service.GWTNone;
import com.kenstevens.stratinit.client.gwt.service.GWTResult;
import com.kenstevens.stratinit.main.Spring;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.GameBuildAudit;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.model.World;
import com.kenstevens.stratinit.remote.None;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.StratInit;
import com.kenstevens.stratinit.server.daoservice.GameDaoService;
import com.kenstevens.stratinit.server.daoservice.PlayerDaoService;
import com.kenstevens.stratinit.server.gwtrequest.GwtGetCitiesRequest;
import com.kenstevens.stratinit.server.gwtrequest.GwtGetUnitsRequest;
import com.kenstevens.stratinit.server.gwtrequest.GwtGetUpdateRequest;
import com.kenstevens.stratinit.server.gwtrequest.GwtMoveUnitsRequest;
import com.kenstevens.stratinit.server.gwtrequest.GwtUpdateCityRequest;
import com.kenstevens.stratinit.server.gwtrequest.translate.GWTPlayerTranslate;
import com.kenstevens.stratinit.server.remote.event.StratInitUpdater;
import com.kenstevens.stratinit.world.WorldPrinter;


@Service("gameService")
public class GWTGameServiceImpl extends GWTBaseServiceImpl implements
		GWTGameService {

	private static final long serialVersionUID = -7866472150712172396L;
	@Autowired
	private StratInit stratInit;
	@Autowired
	private StratInitUpdater stratInitUpdater;
	@Autowired
	private GameDaoService gameDaoService;
	@Autowired
	private PlayerDaoService playerDaoService;
	@Autowired
	private Spring spring;
	@Autowired
	private DataCache dataCache;

	public GWTResult<GWTNone> createGame(String name) {
		if (!isAdmin()) {
			return new GWTResult<GWTNone>("Only administrators may create a game.", false);
		}
		gameDaoService.createGame(name);
		return new GWTResult<GWTNone>(true);
	}

	public GWTResult<GWTNone> createBlitzGame(String name, int islands) {
		if (!isAdmin()) {
			return new GWTResult<GWTNone>("Only administrators may create a game.", false);
		}
		gameDaoService.createBlitzGame(name, islands);
		return new GWTResult<GWTNone>(true);
	}

	// TODO SEC Secure service calls
	// @Secured("ROLE_ADMIN")

	public List<GWTGame> getUnjoinedGames() throws ServiceSecurityException {
		Result<Player> presult = getPlayerResult();
		if (!presult.isSuccess()) {
			return null;
		}
		Player player = presult.getValue();

		List<GWTGame> retval = new ArrayList<GWTGame>();
		for (Game game : gameDaoService.getUnjoinedGames(player)) {
			GWTGame gWTGame = gameToGameRow(game);
			retval.add(gWTGame);
		}
		return retval;
	}

	private GWTGame gameToGameRow(Game game) {
		GWTGame gWTGame = new GWTGame();
		gWTGame.setId(game.getId());
		gWTGame.setName(game.getName());
		gWTGame.setEnabled(game.isEnabled());
		gWTGame.setSize(game.getSize());
		gWTGame.setCreated(game.getCreated());
		gWTGame.setStarted(game.getStartTime());
		gWTGame.setEnds(game.getEnds());
		gWTGame.setPlayers(game.getPlayers());
		gWTGame.setIslands(game.getIslands());
		gWTGame.setMapped(game.isMapped());
		return gWTGame;
	}

	public GWTResult<String> getGameMap(int gameId) {
		World world = dataCache.getGameCache(gameId).getWorld();
		if (world == null) {
			return new GWTResult<String>("Game " + gameId + " not found", false);
		}
		WorldPrinter worldPrinter = new WorldPrinter(world);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(outputStream);
		worldPrinter.print(printStream);
		return new GWTResult<String>("", true, outputStream.toString());
	}

	public GWTResult<Integer> joinGame(int gameId) throws ServiceSecurityException {
		Result<Player> presult = getPlayerResult();
		if (!presult.isSuccess()) {
			return new GWTResult<Integer>(presult.getMessages(), presult.isSuccess());
		}
		Player player = presult.getValue();
		Result<Nation> nresult = stratInit.joinGame(player, gameId);
		GWTResult<Integer> retval;
		if (nresult.isSuccess()) {
			retval = new GWTResult<Integer>(nresult.getMessages(),
					nresult.isSuccess(), nresult.getValue().getNationId());
		} else {
			retval = new GWTResult<Integer>(nresult.getMessages(), nresult.isSuccess());
		}
		return retval;
	}

	public GWTResult<GWTNone> removeGame(int gameId) {
		gameDaoService.removeGame(gameId);
		return new GWTResult<GWTNone>();
	}

	public List<GWTGame> getJoinedGames() throws ServiceSecurityException {
		Result<Player> presult = getPlayerResult();
		if (!presult.isSuccess()) {
			return null;
		}
		Player player = presult.getValue();

		List<GWTGame> retval = new ArrayList<GWTGame>();
		for (Game game : gameDaoService.getJoinedGames(player)) {
			GWTGame gWTGame = gameToGameRow(game);
			retval.add(gWTGame);
		}
		return retval;
	}

	@Override
	public List<GWTBuildAudit> fetchBuildAudit() {
		List<GWTBuildAudit> retval = new ArrayList<GWTBuildAudit>();
		for (GameBuildAudit gameBuildAudit : gameDaoService.getGameBuildAudit()) {
			GWTBuildAudit gwtBuildAudit = gameBuildAuditToGWTGameBuildAudit(gameBuildAudit);
			retval.add(gwtBuildAudit);
		}
		return retval;
	}

	private GWTBuildAudit gameBuildAuditToGWTGameBuildAudit(
			GameBuildAudit gameBuildAudit) {
		GWTBuildAudit retval = new GWTBuildAudit();
		retval.setCount(gameBuildAudit.getCount());
		retval.setType(gameBuildAudit.getType().toString());
		retval.setId(gameBuildAudit.getGameId());
		return retval;
	}

	@Override
	public GWTResult<GWTNone> shutdown() throws ServiceSecurityException {
		if (!isAdmin()) {
			return new GWTResult<GWTNone>("Only administrators may shut the server down.", false);
		}
		stratInit.shutdown();
		return new GWTResult<GWTNone>("Server shut down", true);
	}

	@Override
	public GWTResult<GWTNone> updatePlayer(String newPassword, String email,
			boolean emailGameMail) throws ServiceSecurityException {
		Result<Player> presult = getPlayerResult();
		if (!presult.isSuccess()) {
			return new GWTResult<GWTNone>(presult.getMessages(), presult.isSuccess());
		}
		Player player = presult.getValue();

		String encodedPassword = null;
		if (newPassword != null && !newPassword.isEmpty()) {
			ShaPasswordEncoder encoder = new ShaPasswordEncoder();
			encodedPassword = encoder.encodePassword(newPassword, null);
		}
		Result<Player> result = playerDaoService.updatePlayer(player, encodedPassword, email, emailGameMail);
		return new GWTResult<GWTNone>(result.getMessages(),
					result.isSuccess());
	}

	@Override
	public GWTResult<GWTPlayer> fetchPlayer() throws ServiceSecurityException {
		Result<Player> presult = getPlayerResult();
		GWTResult<GWTPlayer> retval;
		if (presult.isSuccess()) {
			retval = new GWTResult<GWTPlayer>(GWTPlayerTranslate.translate(presult.getValue()));
		} else {
			retval = new GWTResult<GWTPlayer>(presult.getMessages(), presult.isSuccess());
		}
		return retval;
	}

	@Override
	public GWTResult<Integer> postAnnouncement(String subject, String body)
			throws ServiceSecurityException {
		Result<Integer> result = stratInit.postAnnouncement(subject, body);
		return new GWTResult<Integer>(result.getMessages(),
				result.isSuccess());
	}

	@Override
	public GWTResult<GWTNone> setGame(int gameId) {
		Result<None> result = stratInit.setGame(gameId);
		return new GWTResult<GWTNone>(result.getMessages(), result.isSuccess());
	}

	@Override
	public GWTUpdate getUpdate() {
		return spring.getBean(GwtGetUpdateRequest.class).process().getValue();
	}

	@Override
	public List<GWTUnit> getUnits() {
		return spring.getBean(GwtGetUnitsRequest.class).process().getValue();
	}

	@Override
	public GWTResult<GWTUpdate> moveUnits(List<GWTUnit> units,
			GWTSectorCoords coords) {
		Result<GWTUpdate> result = spring.autowire(new GwtMoveUnitsRequest(units, coords)).process();
		return new GWTResult<GWTUpdate>(result.getMessages(), result.isSuccess(), result.getValue());
	}

	@Override
	public List<GWTCity> getCities() {
		return spring.getBean(GwtGetCitiesRequest.class).process().getValue();
	}

	@Override
	public GWTResult<GWTUpdate> updateCity(GWTCity city, boolean isBuild) {
		Result<GWTUpdate> result = spring.autowire(new GwtUpdateCityRequest(city, isBuild)).process();
		return new GWTResult<GWTUpdate>(result.getMessages(), result.isSuccess(), result.getValue());
	}

	@Override
	public GWTResult<GWTNone> endGame(int gameId) {
		if (!isAdmin()) {
			return new GWTResult<GWTNone>("Only administrators may end a game.", false);
		}
		stratInitUpdater.endGame(gameId);
		return new GWTResult<GWTNone>(true);
	}
}
