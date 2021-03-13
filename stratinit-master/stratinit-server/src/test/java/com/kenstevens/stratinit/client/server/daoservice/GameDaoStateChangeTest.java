package com.kenstevens.stratinit.client.server.daoservice;

import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Player;
import com.kenstevens.stratinit.client.model.World;
import com.kenstevens.stratinit.config.IServerConfig;
import com.kenstevens.stratinit.config.RunModeEnum;
import com.kenstevens.stratinit.config.ServerConfig;
import com.kenstevens.stratinit.dao.GameDao;
import com.kenstevens.stratinit.dao.NationDao;
import com.kenstevens.stratinit.dao.SectorDao;
import com.kenstevens.stratinit.helper.GameHelper;
import com.kenstevens.stratinit.helper.NationHelper;
import com.kenstevens.stratinit.helper.PlayerHelper;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.GameDaoService;
import com.kenstevens.stratinit.server.daoservice.RelationDaoService;
import com.kenstevens.stratinit.server.event.svc.EventQueue;
import com.kenstevens.stratinit.server.rest.mail.MailService;
import com.kenstevens.stratinit.server.svc.GameCreator;
import com.kenstevens.stratinit.server.svc.WorldManager;
import com.kenstevens.stratinit.type.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameDaoStateChangeTest {
    @Mock
    private EventQueue eventQueue;
    @Mock
    private MailService mailService;
    @Mock
    private WorldManager worldManager;
    @Mock
    private SectorDao sectorDao;
    @Mock
    private GameDao gameDao;
    @Mock
    private NationDao nationDao;
    @Mock
    private GameCreator gameCreator;
    @Mock
    private RelationDaoService relationDaoService;

    private Game game;

    private final IServerConfig serverConfig = new ServerConfig(RunModeEnum.TEST);

    @InjectMocks
    private final GameDaoService gameDaoService = new GameDaoService(serverConfig);

    @BeforeEach
    public void before() {
        game = GameHelper.newGame();
    }

    @Test
    public void createGame() {
        Game game = gameDaoService.createGame("test");
        assertNotNull(game);
        assertTrue(game.isEnabled());
        assertNull(game.getStartTime());
        assertNotMapped(game);
        assertEquals(0, game.getPlayers());
        assertFalse(game.isBlitz());
    }

    @Test
    public void createBlitzGame() {
        World world = prepWorld();

        Game game = gameDaoService.createBlitzGame("test", 2);
        assertNotNull(game.getStartTime());
        assertIsMapped(game);
        assertEquals(0, game.getPlayers());
        assertTrue(game.isBlitz());
        verify(worldManager).build(any(Game.class));
        verify(sectorDao).save(world);
    }

    private void assertNotMapped(Game game) {
        assertNull(game.getMapped());
        assertFalse(game.isMapped());
        assertEquals(0, game.getIslands());
        assertEquals(0, game.getGamesize());
    }

    private void assertIsMapped(Game game) {
        assertNotNull(game.getMapped());
        assertTrue(game.isMapped());
        assertTrue(game.getGamesize() > 0);
        assertTrue(game.getIslands() > 0);
    }

    @Test
    public void scheduleGameNoPlayers() {

        gameDaoService.scheduleGame(game);
        assertNotNull(game.getStartTime());
        assertNotMapped(game);
        assertEquals(0, game.getPlayers());
        verify(eventQueue).schedule(game, false);
    }

    @Test
    public void scheduleGameTwice() {

        gameDaoService.scheduleGame(game);
        gameDaoService.scheduleGame(game);
        verify(eventQueue).schedule(game, false);
    }

    @Test
    public void scheduleGameOnePlayer() {

        final Player player = PlayerHelper.me;

        prepJoinGame(1);

        gameDaoService.joinGame(player, game.getId(), false);

        gameDaoService.scheduleGame(game);
        assertNotNull(game.getStartTime());
        assertNotMapped(game);
        assertEquals(1, game.getPlayers());

        verify(eventQueue).schedule(game, false);
        verify(mailService).sendEmail(any(), any());
    }

    private void prepJoinGame(int numPlayers) {
        when(gameDao.findGame(GameHelper.gameId)).thenReturn(GameHelper.game);
        List<Nation> nations = new ArrayList<>();
        for (int i = 0; i < numPlayers; ++i) {
            nations.add(NationHelper.newNation(PlayerHelper.newPlayer(i)));
        }
        when(nationDao.getNations(GameHelper.game)).thenReturn(nations);
    }

    @Test
    public void mapGameNotStarted() {

        try {
            gameDaoService.mapGame(game);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Game " + GameHelper.gameName + " can not be mapped.  It has not been started yet.", e.getMessage());
        }
    }

    @Test
    public void mapGameTwice() {

        final Player player = PlayerHelper.me;

        World world = prepWorld();

        prepJoinGame(1);

        final Result<Nation> result = gameDaoService.joinGame(player, game.getId(), false);
        gameDaoService.scheduleGame(game);
        gameDaoService.mapGame(game);
        try {
            gameDaoService.mapGame(game);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Game " + GameHelper.gameName + " is already mapped.  It cannot be mapped again.", e.getMessage());
        }
        verify(eventQueue).schedule(game, false);
        verify(worldManager).build(game);
        verify(sectorDao).save(world);
        verify(worldManager).addPlayerToMap(0, result.getValue());
        verify(mailService).sendEmail(any(), any());
    }

    @Test
    public void mapGameNoPlayers() {

        gameDaoService.scheduleGame(game);
        try {
            gameDaoService.mapGame(game);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Game " + GameHelper.gameName + " may not be mapped.  It has no players.", e.getMessage());
        }
        verify(eventQueue).schedule(game, false);
    }

    @Test
    public void joinBeforeMapped() {

        final Player player = PlayerHelper.me;

        World world = prepWorld();

        prepJoinGame(1);

        final Result<Nation> result = gameDaoService.joinGame(player, game
                .getId(), false);

        gameDaoService.scheduleGame(game);
        gameDaoService.mapGame(game);
        assertNotNull(game.getStartTime());
        assertIsMapped(game);
        assertEquals(1, game.getPlayers());

        // TODO sleep extract similar sets of asserts
        verify(eventQueue).schedule(game, false);
        verify(worldManager).build(game);
        verify(worldManager).addPlayerToMap(0, result.getValue());
        verify(sectorDao).save(world);
        verify(mailService).sendEmail(any(), any());
    }

    private World prepWorld() {
        World world = new World(GameHelper.game, true);
        when(worldManager.build(any())).thenReturn(world);
        return world;
    }

    @Test
    public void joinAfterMapped() {

        World world = setupTwoPlayers(game);
        Player player = PlayerHelper.me;

        final Result<Nation> result = gameDaoService.joinGame(player, game.getId(), false);

        assertResult(result);
        assertNotNull(game.getStartTime());
        assertIsMapped(game);
        assertEquals(3, game.getPlayers());

        verify(eventQueue).schedule(game, false);
        verify(worldManager).build(game);
        verify(worldManager, times(3)).addPlayerToMap(any(Integer.class), any(Nation.class));
        verify(sectorDao).save(world);
    }

    private World setupTwoPlayers(Game game) {
        World world = prepWorld();

        gameDaoService.scheduleGame(game);
        prepJoinGame(2);
        playersJoinGame(game, 2);
        gameDaoService.mapGame(game);
        return world;
    }

    @Test
    public void joinTooManyAfterMapped() {
        World world = setupTwoPlayers(game);

        assertEquals(Constants.MAP_EXTRA_SLOTS + 2, game.getIslands());

        playersJoinGame(game, Constants.MAP_EXTRA_SLOTS);

        final Player player = PlayerHelper.me;
        final Result<Nation> result = gameDaoService.joinGame(player, game.getId(), false);
        assertFalseResult(result);

        assertNotNull(game.getStartTime());
        assertIsMapped(game);
        assertEquals(Constants.MAP_EXTRA_SLOTS + 2, game.getPlayers());
        verify(eventQueue).schedule(game, false);
        verify(worldManager).build(game);

        verify(worldManager, times(Constants.MAP_EXTRA_SLOTS + 2)).addPlayerToMap(any(Integer.class), any(Nation.class));
        verify(sectorDao).save(world);

    }

    private void playersJoinGame(final Game game, int numPlayers) {
        for (int i = 0; i < numPlayers; ++i) {
            final Player player = PlayerHelper.newPlayer(i);
            final Result<Nation> result = gameDaoService.joinGame(player, game.getId(), false);
            assertResult(result);
        }
    }

    @Test
    public void scheduleGameMinPlayersToMap() {
        assertNull(game.getStartTime());
        prepJoinGame(serverConfig.getMinPlayersToSchedule());
        playersJoinGame(game, serverConfig.getMinPlayersToSchedule());

        assertNotNull(game.getStartTime());
        assertNotMapped(game);
        assertEquals(serverConfig.getMinPlayersToSchedule(), game.getPlayers());
        verify(eventQueue).schedule(game, false);

        verify(mailService, times(serverConfig.getMinPlayersToSchedule())).sendEmail(any(), any());
    }

    @Test
    // FIXME not confident this is testing what we want to test here
    public void scheduleGameMinPlayersPlusOneToMap() {
        final int numPlayers = serverConfig.getMinPlayersToSchedule() + 1;

        assertNull(game.getStartTime());

        prepJoinGame(numPlayers - 1);
        playersJoinGame(game, numPlayers);

        assertNotNull(game.getStartTime());
        assertNotMapped(game);
        assertEquals(numPlayers, game.getPlayers());
        verify(eventQueue).schedule(game, false);
        verify(mailService, times(numPlayers - 1)).sendEmail(any(), any());
    }

    protected void assertResult(Result<?> result) {
        assertTrue(result.isSuccess(), result.toString());
    }

    protected void assertFalseResult(Result<? extends Object> result) {
        assertFalse(result.isSuccess(), result.toString());
    }
}
