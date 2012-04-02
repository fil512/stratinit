package com.kenstevens.stratinit.server.remote.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.cache.GameCache;
import com.kenstevens.stratinit.model.Game;

@Scope("prototype")
@Component
public class SynchronizedDataAccess {
	@Autowired
	private DataCache dataCache;
	private final Game game;
	private final DataWriter writer;

	public SynchronizedDataAccess(Game game, DataWriter writer) {
		this.game = game;
		this.writer = writer;
	}

	public void write() {
		GameCache gameCache = dataCache.getGameCache(game);
		synchronized (gameCache) {
			writer.writeData();
		}
	}
}
