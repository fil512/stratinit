package com.kenstevens.stratinit.client.event;



public class GameListArrivedEvent implements DataArrivedEvent {
	private final boolean joinedGames;

	public GameListArrivedEvent(boolean joinedGames) {
		this.joinedGames = joinedGames;
	}

	public boolean isJoinedGames() {
		return joinedGames;
	}
}
