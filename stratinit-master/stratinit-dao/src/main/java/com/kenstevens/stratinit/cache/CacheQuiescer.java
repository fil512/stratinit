package com.kenstevens.stratinit.cache;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kenstevens.stratinit.QuiesceService;

@Service
public class CacheQuiescer implements QuiesceService {
	private final Log log = LogFactory.getLog(getClass());

	@Autowired
	private DataCache dataCache;

	@Override
	public void quiesce() {
		List<GameCache> gameCaches = dataCache.getGameCaches();
		recursivelyLockAndFlush(gameCaches);
	}

	private void recursivelyLockAndFlush(List<GameCache> gameCaches) {
		if (gameCaches.isEmpty()) {
			log.info("Flushing Data Cache.");
			dataCache.flush();
		} else {
			GameCache headGameCache = gameCaches.remove(0);
			log.info("Locking game #" + headGameCache.getGameId());
			synchronized (headGameCache) {
				recursivelyLockAndFlush(gameCaches);
			}
		}
	}
}
