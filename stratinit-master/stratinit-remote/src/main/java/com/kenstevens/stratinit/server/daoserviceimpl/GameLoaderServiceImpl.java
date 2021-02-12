package com.kenstevens.stratinit.server.daoserviceimpl;

import com.kenstevens.stratinit.cache.GameCache;
import com.kenstevens.stratinit.cache.GameLoader;
import com.kenstevens.stratinit.server.daoservice.GameLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class GameLoaderServiceImpl implements GameLoader, GameLoaderService {
	@Autowired
	private GameLoader gameLoader;

	@SuppressWarnings("unused")
	@Override
	public void flush(GameCache gameCache) {
		gameLoader.flush(gameCache);
	}

	@Override
	public GameCache loadGame(int gameId) {
		return gameLoader.loadGame(gameId);
	}
}
