package com.kenstevens.stratinit.dao;

import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.cache.GameCache;
import com.kenstevens.stratinit.cache.NationCache;
import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.Nation;
import org.springframework.beans.factory.annotation.Autowired;

public class CacheDao {
	@Autowired
	protected DataCache dataCache;

	protected GameCache getGameCache(int gameId) {
		return dataCache.getGameCache(gameId);
	}

	protected GameCache getGameCache(Nation nation) {
		return getGameCache(nation.getGame());
	}

	protected GameCache getGameCache(Game game) {
		return getGameCache(game.getId());
	}

	protected NationCache getNationCache(Nation nation) {
		return getGameCache(nation).getNationCache(nation);
	}
}
