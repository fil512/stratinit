package com.kenstevens.stratinit.server.daoservice;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.model.audit.UnitBuildAudit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.remote.StratInitWebBase;
import com.kenstevens.stratinit.type.UnitType;

public class GameDaoServiceTest extends StratInitWebBase {
	@Autowired
	private GameDaoService gameDaoService;
	@Autowired
	private PlayerDaoService playerDaoService;

	// TODO test Constants.MAX_PLAYERS_PER_GAME works

	@Test
	public void testNoDup() {
		Player player = createPlayer();
		joinGame(player);
		Result<Nation> result = gameDaoService.joinGame(player, testGameId);
		assertFalseResult(result);
	}

	@Test
	public void audit() {
		Player player = createPlayer();
		joinGame(player);
		List<UnitBuildAudit>buildAuditList = unitDao.getBuildAudits(testGameId, PLAYER_NAME);
		assertEquals(5, buildAuditList.size());
		UnitBuildAudit audit = buildAuditList.get(0);
		assertEquals(UnitType.INFANTRY, audit.getType());
		assertEquals(PLAYER_NAME, audit.getUsername());
	}


	@Test
	public void testUnjoinedGames() {
		Player player = createPlayer();
		List<Game> games = gameDaoService.getUnjoinedGames(player);
		int unjoined = games.size();
		joinGame(player);
		games = gameDaoService.getUnjoinedGames(player);
		assertEquals(unjoined - 1, games.size());
	}

	// Don't rename this method!
	@Test
	public void testRemoveGame() {
		Player player = createPlayer();
		List<Game> games = gameDaoService.getUnjoinedGames(player);
		int unjoined = games.size();
		gameDaoService.removeGame(testGameId);
		games = gameDaoService.getUnjoinedGames(player);
		assertEquals(unjoined - 1, games.size());
	}

	@Ignore
	@Test
	public void sevenPlayersMapped() throws InterruptedException {
		Game game = gameDaoService.createGame("test");
		int gameId = game.getId();
		List<Player> players = Lists.newArrayList();
		List<Nation> nations = Lists.newArrayList();
		int numPlayers = 5;
		// TODO TEST why does this throw an exception with numPlayers >= 5?
		for (int i = 0; i < numPlayers ; ++i) {
			System.out.println("Player "+i);
			Result<Player> rresult = playerDaoService.register("testPlayer"+i, "testy", "tesyt@test.com", "Test User Agent");
			assertResult(rresult);
			Player player = rresult.getValue();
			players.add(player);
			Result<Nation> jresult = gameDaoService.joinGame(player, gameId);
			// TODO what's causing the fail is that the 4th player to join the game is triggering the game start
			assertResult(jresult);
			Nation nation = jresult.getValue();
			nations.add(nation);
		}
		gameDaoService.scheduleGame(game);
		// TODO reproduce mapGame error in logs
		gameDaoService.mapGame(game);
	}
}
