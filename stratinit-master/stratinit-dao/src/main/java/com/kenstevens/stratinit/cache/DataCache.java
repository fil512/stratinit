package com.kenstevens.stratinit.cache;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.config.IServerConfig;
import com.kenstevens.stratinit.repo.GameRepo;
import com.kenstevens.stratinit.repo.PlayerRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Repository
public class DataCache extends Updatable {
	private static final long serialVersionUID = 1L;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private GameLoader gameLoader;
	@Autowired
	private GameRepo gameRepo;
	@Autowired
	private PlayerRepo playerRepo;
	@Autowired
	private IServerConfig serverConfig;

	private final Map<Integer, GameCache> gameMap = new TreeMap<>();
	private final Map<Integer, Player> playerMap = new TreeMap<>();

	@SuppressWarnings("unused")
	@PostConstruct
	private void load() {
		loadPlayers();
		loadGames();
	}

	private void loadGames() {
		logger.info("Loading games into cache...");
		int count = 0;
		for (Game game : gameRepo.findAll()) {
			Integer gameId = game.getId();
			++count;
			getGameCache(gameId);
		}
		logger.info("{} games loaded.", count);
	}

	private void loadPlayers() {
		logger.info("Loading players into cache...");

		int count = 0;
		for (Player player : playerRepo.findAll()) {
			add(player);
			++count;
		}
		logger.info("{} players loaded.", count);
	}

	public void add(Player player) {
		playerMap.put(player.getId(), player);
	}

	public synchronized GameCache getGameCache(int gameId) {
		GameCache gameCache = gameMap.get(gameId);
		if (gameCache == null) {
			gameCache = gameLoader.loadGame(gameId);
			if (gameCache == null) {
				return null;
			}
			gameMap.put(gameId, gameCache);
		}
		return gameCache;
	}

	public List<GameCache> getGameCaches() {
		return gameMap.values().stream()
				.filter(gameCache -> gameCache.getGame().isEnabled())
				.collect(Collectors.toList());
	}

	public List<Game> getAllGames() {
		return getGameCaches().stream()
				.map(GameCache::getGame)
				.collect(Collectors.toList());
	}
	
	public List<Player> getAllPlayers() {
		return Lists.newArrayList(playerMap.values());
	}

	public void remove(Game game) {
		gameMap.remove(game.getId());
	}

	public synchronized void flush() {
		logger.trace("Flushing games to database...");
		for (GameCache gameCache : getGameCaches()) {
			gameLoader.flush(gameCache);
		}
		logger.trace("...done");
	}

	public GameCache getGameCache(Game game) {
		return getGameCache(game.getId());
	}

	public World getWorld(int gameId) {
		return getGameCache(gameId).getWorld();
	}

	public List<Sector> getSectors(int gameId) {
		return getGameCache(gameId).getSectors();
	}

	public Unit getUnit(int unitId) {
		for (GameCache gameCache : getGameCaches()) {
			Unit unit = gameCache.getUnit(unitId);
			if (unit != null) {
				return unit;
			}
		}
		return null;
	}

	public List<Unit> getAllUnits() {
		List<Unit> retval = new ArrayList<>();
		for (GameCache gameCache : getGameCaches()) {
			retval.addAll(gameCache.getUnits());
		}
		return retval;
	}

	@Override
	public Object getKey() {
		return gameMap;
	}

	@Override
	public boolean isKeyUnique() {
		return false;
	}

	@Override
	public long getUpdatePeriodMilliseconds() {
		return serverConfig.getFlushCacheMillis();
	}

	@Override
	public boolean isBlitz() {
		return false;
	}

	public void remove(Player player) {
		playerMap.remove(player.getId());
	}

	public Player getPlayer(Integer id) {
		return playerMap.get(id);
	}

	public void clear() {
		gameMap.clear();
		playerMap.clear();
	}
}
