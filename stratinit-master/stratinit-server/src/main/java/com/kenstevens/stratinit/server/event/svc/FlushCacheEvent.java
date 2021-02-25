package com.kenstevens.stratinit.server.event.svc;

import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.server.event.Event;

public class FlushCacheEvent extends Event {
    private final DataCache dataCache;

    FlushCacheEvent(DataCache dataCache) {
        super(dataCache);
        this.dataCache = dataCache;
    }

    @Override
    protected void execute() {
        dataCache.flush();
    }
}
