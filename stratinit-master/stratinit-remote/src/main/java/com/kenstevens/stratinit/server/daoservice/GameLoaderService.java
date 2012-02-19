package com.kenstevens.stratinit.server.daoservice;

import com.kenstevens.stratinit.cache.GameCache;

public interface GameLoaderService {

	public abstract void flush(GameCache gameCache);

	public abstract GameCache loadGame(int gameId);

}