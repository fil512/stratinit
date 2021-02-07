package com.kenstevens.stratinit.server.daoservice;

import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Player;
import com.kenstevens.stratinit.model.World;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.event.EventQueue;
import com.kenstevens.stratinit.server.remote.StratInitDaoBase;
import com.kenstevens.stratinit.server.remote.mail.MailService;
import com.kenstevens.stratinit.server.remote.mail.MailTemplate;
import com.kenstevens.stratinit.type.Constants;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.*;

public class GameDaoStateChangeTest extends StratInitDaoBase {
	private final Mockery context = new Mockery();
	private EventQueue eventQueue;
	private MailService mailService;
	private WorldManager worldManager;
	private SectorDao sectorDao;

	@Autowired
	private EventQueue origEventQueue;
	@Autowired
	private MailService origMailService;
	@Autowired
	private WorldManager origWorldManager;
	@Autowired
	private SectorDao origSectorDao;

	@Autowired
	private GameDaoService gameDaoService;

	@Before
	public void setupMocks() {

		eventQueue = context.mock(EventQueue.class);
		mailService = context.mock(MailService.class);
		worldManager = context.mock(WorldManager.class);
		sectorDao = context.mock(SectorDao.class);

		ReflectionTestUtils.setField(gameDaoService, "eventQueue", eventQueue);
		ReflectionTestUtils
				.setField(gameDaoService, "mailService", mailService);
		ReflectionTestUtils.setField(gameDaoService, "worldManager",
				worldManager);
		ReflectionTestUtils.setField(gameDaoService, "sectorDao", sectorDao);
	}

	@After
	public void undoMocks() {
		ReflectionTestUtils.setField(gameDaoService, "eventQueue",
				origEventQueue);
		ReflectionTestUtils.setField(gameDaoService, "mailService",
				origMailService);
		ReflectionTestUtils.setField(gameDaoService, "worldManager",
				origWorldManager);
		ReflectionTestUtils.setField(gameDaoService, "sectorDao",
				origSectorDao);
	}

	@Test
	public void createGame() {
		Game game = makeGame();
		assertNotNull(game);
		assertTrue(game.isEnabled());
		assertNotNull(game.getCreated());
		assertNull(game.getStartTime());
		assertNotMapped(game);
		assertEquals(0, game.getPlayers());
		assertFalse(game.isBlitz());
	}

	private Game makeGame() {
		return gameDaoService.createGame("test");
	}

	private Game makeBlitzGame() {
		return gameDaoService.createBlitzGame("test", 2);
	}

	@Test
	public void createBlitzGame() {
		context.checking(new Expectations() {
			{
				oneOf(worldManager).build(with(any(Game.class)));
				oneOf(sectorDao).persist(with(aNull(World.class)));
			}
		});

		Game game = makeBlitzGame();
		assertNotNull(game.getCreated());
		assertNotNull(game.getStartTime());
		assertIsMapped(game);
		assertEquals(0, game.getPlayers());
		assertTrue(game.isBlitz());
		context.assertIsSatisfied();
	}

	private void assertNotMapped(Game game) {
		assertNull(game.getMapped());
		assertFalse(game.isMapped());
		assertEquals(0, game.getIslands());
		assertEquals(0, game.getSize());
	}

	private void assertIsMapped(Game game) {
		assertNotNull(game.getMapped());
		assertTrue(game.isMapped());
		assertTrue(game.getSize() > 0);
		assertTrue(game.getIslands() > 0);
	}

	@Test
	public void scheduleGameNoPlayers() {
		final Game game = makeGame();

		context.checking(new Expectations() {
			{
				oneOf(eventQueue).schedule(game, false);
			}
		});

		gameDaoService.scheduleGame(game);
		assertNotNull(game.getCreated());
		assertNotNull(game.getStartTime());
		assertNotMapped(game);
		assertEquals(0, game.getPlayers());
		context.assertIsSatisfied();
	}

	@Test
	public void scheduleGameTwice() {
		final Game game = makeGame();
		context.checking(new Expectations() {
			{
				oneOf(eventQueue).schedule(game, false);
			}
		});

		gameDaoService.scheduleGame(game);
		gameDaoService.scheduleGame(game);
		context.assertIsSatisfied();
	}

	@Test
	public void scheduleGameOnePlayer() {
		final Game game = makeGame();
		final Player player = createPlayer();
		gameDaoService.joinGame(player, game.getId(), false);

		context.checking(new Expectations() {
			{
				oneOf(eventQueue).schedule(game, false);
				oneOf(mailService).sendEmail(with(same(player)),
						with(any(MailTemplate.class)));
			}
		});

		gameDaoService.scheduleGame(game);
		assertNotNull(game.getCreated());
		assertNotNull(game.getStartTime());
		assertNotMapped(game);
		assertEquals(1, game.getPlayers());
		context.assertIsSatisfied();
	}

	@Test(expected = IllegalStateException.class)
	public void mapGameNotStarted() {
		Game game = makeGame();
		gameDaoService.mapGame(game);
	}

	@Test(expected = IllegalStateException.class)
	public void mapGameTwice() {
		final Game game = makeGame();

		context.checking(new Expectations() {
			{
				oneOf(eventQueue).schedule(game, false);
				oneOf(worldManager).build(game);
				oneOf(sectorDao).persist(with(any(World.class)));
			}
		});
		gameDaoService.scheduleGame(game);
		gameDaoService.mapGame(game);
		gameDaoService.mapGame(game);
		context.assertIsSatisfied();
	}

	@Test(expected=IllegalStateException.class)
	public void mapGameNoPlayers() {
		final Game game = makeGame();
		context.checking(new Expectations() {
			{
				oneOf(eventQueue).schedule(game, false);
				oneOf(worldManager).build(game);
				oneOf(sectorDao).persist(with(any(World.class)));
			}
		});
		gameDaoService.scheduleGame(game);
		gameDaoService.mapGame(game);
	}

	// FIXME this test started failing when we upgraded mock
	@Test
	public void joinBeforeMapped() {
		final Game game = makeGame();
		final Player player = createPlayer();
		final Result<Nation> result = gameDaoService.joinGame(player, game
				.getId(), false);

		context.checking(new Expectations() {
			{
				oneOf(eventQueue).schedule(game, false);
				oneOf(worldManager).build(game);
				oneOf(worldManager).addPlayerToMap(0, result.getValue());
				oneOf(sectorDao).persist(with(aNull(World.class)));
				oneOf(mailService).sendEmail(with(same(player)),
						with(any(MailTemplate.class)));
			}
		});
		gameDaoService.scheduleGame(game);
		gameDaoService.mapGame(game);
		assertNotNull(game.getCreated());
		assertNotNull(game.getStartTime());
		assertIsMapped(game);
		assertEquals(1, game.getPlayers());
		context.assertIsSatisfied();
	}

	@Test
	public void joinAfterMapped() {
		final Game game = makeGame();
		final Player player = createPlayer();

		context.checking(new Expectations() {
			{
				oneOf(eventQueue).schedule(game, false);
				oneOf(worldManager).build(game);
				exactly(3).of(worldManager).addPlayerToMap(with(any(Integer.class)),
						with(any(Nation.class)));
				oneOf(sectorDao).persist(with(aNull(World.class)));
			}
		});
		gameDaoService.scheduleGame(game);
		playersJoinGame(game, 2);
		gameDaoService.mapGame(game);
		final Result<Nation> result = gameDaoService.joinGame(player, game
				.getId(), false);
		assertResult(result);
		assertNotNull(game.getCreated());
		assertNotNull(game.getStartTime());
		assertIsMapped(game);
		assertEquals(3, game.getPlayers());
		context.assertIsSatisfied();
	}

	@Test
	public void joinTooManyAfterMapped() {
		final Game game = makeGame();

		context.checking(new Expectations() {
			{
				oneOf(eventQueue).schedule(game, false);
				oneOf(worldManager).build(game);
				exactly(Constants.MAP_EXTRA_SLOTS + 2).of(worldManager).addPlayerToMap(with(any(Integer.class)),
						with(any(Nation.class)));
				oneOf(sectorDao).persist(with(aNull(World.class)));
			}
		});
		gameDaoService.scheduleGame(game);
		playersJoinGame(game, 2);
		gameDaoService.mapGame(game);
		assertEquals(Constants.MAP_EXTRA_SLOTS+2, game.getIslands());

		playersJoinGame(game, Constants.MAP_EXTRA_SLOTS);

		final Player player = createPlayer();
		final Result<Nation> result = gameDaoService.joinGame(player, game
				.getId(), false);
		assertFalseResult(result);

		assertNotNull(game.getCreated());
		assertNotNull(game.getStartTime());
		assertIsMapped(game);
		assertEquals(Constants.MAP_EXTRA_SLOTS+2, game.getPlayers());
		context.assertIsSatisfied();
	}

	private void playersJoinGame(final Game game, int numPlayers) {
		for (int i = 0; i < numPlayers; ++i) {
			final Player player = createPlayer();
			final Result<Nation> result = gameDaoService.joinGame(player, game
					.getId(), false);
			assertResult(result);
		}
	}

	@Test
	public void scheduleGameMinPlayersToMap() {
		final Game game = makeGame();
		context.checking(new Expectations() {
			{
				oneOf(eventQueue).schedule(game, false);
				exactly(Constants.MIN_PLAYERS_TO_SCHEDULE).of(mailService)
						.sendEmail(with(any(Player.class)),
								with(any(MailTemplate.class)));
				// TODO TEST assert specific template types in these sendEmail
				// expectations.
			}
		});

		assertNull(game.getStartTime());

		playersJoinGame(game, Constants.MIN_PLAYERS_TO_SCHEDULE);

		assertNotNull(game.getCreated());
		assertNotNull(game.getStartTime());
		assertNotMapped(game);
		assertEquals(Constants.MIN_PLAYERS_TO_SCHEDULE, game.getPlayers());
		context.assertIsSatisfied();
	}

	@Test
	public void scheduleGameMinPlayersPlusOneToMap() {
		final Game game = makeGame();
		final int numPlayers = Constants.MIN_PLAYERS_TO_SCHEDULE + 1;
		context.checking(new Expectations() {
			{
				oneOf(eventQueue).schedule(game, false);
				exactly(numPlayers - 1).of(mailService).sendEmail(
						with(any(Player.class)), with(any(MailTemplate.class)));
			}
		});

		assertNull(game.getStartTime());

		playersJoinGame(game, numPlayers);

		assertNotNull(game.getCreated());
		assertNotNull(game.getStartTime());
		assertNotMapped(game);
		assertEquals(numPlayers, game.getPlayers());
		context.assertIsSatisfied();
	}

}
