package com.kenstevens.stratinit.cache;

import org.springframework.beans.factory.annotation.Autowired;

public class MockGameLoaderService implements GameLoader {
	@Autowired
	private GameLoader gameLoader;

	@Override
	public void flush(GameCache gameCache) {
		gameLoader.flush(gameCache);
	}

	@Override
	public GameCache loadGame(int gameId) {
		return gameLoader.loadGame(gameId);
	}
}
