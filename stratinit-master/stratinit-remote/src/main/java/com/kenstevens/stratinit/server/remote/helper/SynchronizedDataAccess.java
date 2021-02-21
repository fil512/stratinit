package com.kenstevens.stratinit.server.remote.helper;

import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.cache.GameCache;
import com.kenstevens.stratinit.model.Game;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class SynchronizedDataAccess {
	private final DataCache dataCache;
	private final DataWriter writer;

	public SynchronizedDataAccess(DataCache dataCache, DataWriter writer) {
		this.dataCache = dataCache;
		this.writer = writer;
	}

	public void write(Game game) {
		GameCache gameCache = dataCache.getGameCache(game);
		synchronized (gameCache) {
			writer.writeData();
		}
	}
}
