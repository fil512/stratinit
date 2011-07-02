package com.kenstevens.stratinit.server.remote.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import com.kenstevens.stratinit.client.gwt.callback.ServiceSecurityException;
import com.kenstevens.stratinit.client.gwt.service.GWTGameService;
import com.kenstevens.stratinit.client.gwt.service.GWTNone;
import com.kenstevens.stratinit.client.gwt.service.GWTResult;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.server.daoservice.WorldManager;
import com.kenstevens.stratinit.server.remote.mail.MailService;
import com.kenstevens.stratinit.server.remote.mail.MailTemplate;
import com.kenstevens.stratinit.world.WorldHelper;

public class CreateGameTest extends GWTTestBase {
	@Autowired
	private GWTGameService gameService;
	@Autowired
	private WorldManager worldManager;

	private Mockery context = new Mockery();
	private MailService mailService;
	private MailService savedMailService;

	@Before
	public void setupMocks() {
		mailService = context.mock(MailService.class);

		savedMailService = (MailService) ReflectionTestUtils.getField(
				worldManager, "mailService");
		ReflectionTestUtils
				.setField(worldManager, "mailService", mailService);
	}

	@After
	public void undoMocks() {
		ReflectionTestUtils.setField(worldManager, "mailService",
				savedMailService);
	}

	@Test
	public void gameCreation() throws ServiceSecurityException {
		context.checking(new Expectations() {
			{
				oneOf(mailService).sendEmail(with(any(Player.class)),
						with(any(MailTemplate.class)));
			}
		});

		String name = "gameservicetest";
		List<Game> games = gameDao.getAllGames();
		int origSize = games.size();
		GWTResult<GWTNone> result = gameService.createBlitzGame(name, 2);
		assertResult(result);
		entityManager.flush();
		games = gameDao.getAllGames();
		assertEquals(origSize + 2, games.size());
		Game game = games.get(games.size() - 2);
		assertEquals(name, game.getName());
		List<Sector> sectors = dataCache.getSectors(game.getId());
		new WorldHelper().validateWorld(sectors);
		setAuthentication(PLAYER_ME_NAME);
		GWTResult<Integer> jresult = gameService.joinGame(game.getId());
		assertResult(jresult);
		entityManager.flush();
		context.assertIsSatisfied();
	}

	protected void assertResult(GWTResult<? extends Object> result) {
		assertTrue(result.toString(), result.isSuccess());
	}

}
