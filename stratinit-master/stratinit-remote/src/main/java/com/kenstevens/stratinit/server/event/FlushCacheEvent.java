package com.kenstevens.stratinit.server.event;

import com.kenstevens.stratinit.cache.DataCache;

public class FlushCacheEvent extends Event {
	private DataCache dataCache;

	FlushCacheEvent(DataCache dataCache) {
		super(dataCache);
		this.dataCache = dataCache;
	}

	@Override
	protected void execute() {
		dataCache.flush();
	}
}
