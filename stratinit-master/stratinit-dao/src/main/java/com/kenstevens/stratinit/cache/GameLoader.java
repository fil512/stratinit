package com.kenstevens.stratinit.cache;


public interface GameLoader {

	public abstract GameCache loadGame(int gameId);

	public abstract void flush(GameCache gameCache);

}