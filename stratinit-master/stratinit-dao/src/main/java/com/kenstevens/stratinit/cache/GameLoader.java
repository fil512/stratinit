package com.kenstevens.stratinit.cache;


public interface GameLoader {

	GameCache loadGame(int gameId);

	void flush(GameCache gameCache);

}