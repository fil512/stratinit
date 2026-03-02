package com.kenstevens.stratinit.dao;

import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.cache.GameCache;
import com.kenstevens.stratinit.cache.NationCache;
import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.Nation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.atomic.AtomicInteger;

public class CacheDao {
	private static volatile boolean trainingMode = false;
	private static final AtomicInteger syntheticIdCounter = new AtomicInteger(-1);

	@Autowired
	protected DataCache dataCache;

	public static void setTrainingMode(boolean enabled) {
		trainingMode = enabled;
	}

	public static boolean isTrainingMode() {
		return trainingMode;
	}

	protected static boolean skipDb() {
		return trainingMode;
	}

	protected static int nextSyntheticId() {
		return syntheticIdCounter.getAndDecrement();
	}

	public static void resetSyntheticIds() {
		syntheticIdCounter.set(-1);
	}

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
