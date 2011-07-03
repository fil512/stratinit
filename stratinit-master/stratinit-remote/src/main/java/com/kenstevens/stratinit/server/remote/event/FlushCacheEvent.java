package com.kenstevens.stratinit.server.remote.event;

import java.util.Date;

import com.kenstevens.stratinit.cache.DataCache;

public class FlushCacheEvent extends Event {
	private DataCache dataCache;

	FlushCacheEvent(DataCache dataCache, Date startTime) {
		super(dataCache, startTime);
		this.dataCache = dataCache;
	}

	@Override
	protected void execute() {
		dataCache.flush();
	}
}
