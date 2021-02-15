package com.kenstevens.stratinit.cache;

import com.kenstevens.stratinit.QuiesceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CacheQuiescer implements QuiesceService {
	private final Logger logger = LoggerFactory.getLogger(getClass());

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
