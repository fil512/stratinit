package com.kenstevens.stratinit.client.server.daoservice;

import com.kenstevens.stratinit.cache.GameCache;

public interface GameLoaderService {

    void flush(GameCache gameCache);

    GameCache loadGame(int gameId);

}