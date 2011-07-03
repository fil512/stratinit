package com.kenstevens.stratinit.cache;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.QuiesceService;

@Service
public class CacheQuiescer implements QuiesceService {
	private final Logger logger = Logger.getLogger(getClass());

	@Autowired
	private DataCache dataCache;

	@Override
	public void quiesce() {
		List<GameCache> gameCaches = dataCache.getGameCaches();
		recursivelyLockAndFlush(gameCaches);
	}

	private void recursivelyLockAndFlush(List<GameCache> gameCaches) {
		if (gameCaches.isEmpty()) {
			logger.info("Flushing Data Cache.");
			dataCache.flush();
		} else {
			GameCache headGameCache = gameCaches.remove(0);
			logger.info("Locking game #" + headGameCache.getGameId());
			synchronized (headGameCache) {
				recursivelyLockAndFlush(gameCaches);
			}
		}
	}
}
