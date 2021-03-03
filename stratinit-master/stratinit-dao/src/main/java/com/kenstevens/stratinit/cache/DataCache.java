package com.kenstevens.stratinit.cache;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.kenstevens.stratinit.config.IServerConfig;
import com.kenstevens.stratinit.model.*;
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

	private final Map<Integer, GameCache> gameMap = new TreeMap<Integer, GameCache>();
	private final Map<Integer, Player> playerMap = new TreeMap<Integer, Player>();

	@SuppressWarnings("unused")
	@PostConstruct
	private void load() {
		loadPlayers();
		loadGames();
	}

	private void loadGames() {
		logger.info("Loading games into cache...");

		for (Game game : gameRepo.findAll()) {
			Integer gameId = game.getId();
			getGameCache(gameId);
		}
		logger.info("...done.");
	}

	private void loadPlayers() {
		logger.info("Loading players into cache...");

		for (Player player : playerRepo.findAll()) {
			add(player);
		}
		logger.info("...done.");
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
		return Lists.newArrayList(Iterables.filter(gameMap.values(),
				new Predicate<GameCache>() {
					@Override
					public boolean apply(GameCache gameCache) {
						return gameCache.getGame().isEnabled();
					}

				}));
	}

	public List<Game> getAllGames() {
		return Lists.transform(getGameCaches(),
				new Function<GameCache, Game>() {
					@Override
					public Game apply(GameCache gameCache) {
						return gameCache.getGame();
					}
				});
	}
	
	public List<Player> getAllPlayers() {
		return Lists.newArrayList(playerMap.values());
	}

	public List<Nation> getAllNations() {
		List<Nation> retval = new ArrayList<Nation>();
		for (GameCache gameCache : getGameCaches()) {
			retval.addAll(gameCache.getNations());
		}
		return retval;
	}

	public void remove(Game game) {
		gameMap.remove(game.getId());
	}

	public synchronized void flush() {
		logger.debug("Flushing games to database...");
		for (GameCache gameCache : getGameCaches()) {
			gameLoader.flush(gameCache);
		}
		logger.debug("...done");
	}

	public GameCache getGameCache(Game game) {
		return getGameCache(game.getId());
	}

	public World getWorld(int gameId) {
		return getGameCache(gameId).getWorld();
	}

	public List<City> getAllCities() {
		List<City> retval = new ArrayList<City>();
		for (GameCache gameCache : getGameCaches()) {
			retval.addAll(gameCache.getCities());
		}
		return retval;
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
		List<Unit> retval = new ArrayList<Unit>();
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
