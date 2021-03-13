package com.kenstevens.stratinit.server.svc;

import com.kenstevens.stratinit.cache.GameCache;

public interface GameLoaderService {

    void flush(GameCache gameCache);

    GameCache loadGame(int gameId);

}