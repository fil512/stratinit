package com.kenstevens.stratinit.world;

import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.type.Constants;

import java.util.HashMap;
import java.util.Map;

public final class GameSizer {

	private static final Map<Integer, Integer> ISLANDS_TO_SIZE = new HashMap<Integer, Integer>();

	static {
		// Formula used to calculate this is sqrt(80^80 * numPlayers / 8)
		// because
		// we know that 80x80 is a good map size for 8 players.
		// rounded to the nearest 5
		ISLANDS_TO_SIZE.put(2, 60);
		ISLANDS_TO_SIZE.put(3, 70);
		ISLANDS_TO_SIZE.put(4, 80);
		ISLANDS_TO_SIZE.put(5, 90);
		ISLANDS_TO_SIZE.put(6, 100);
		ISLANDS_TO_SIZE.put(7, 110);
		ISLANDS_TO_SIZE.put(8, 115);
		ISLANDS_TO_SIZE.put(9, 120);
		ISLANDS_TO_SIZE.put(10, 125);
		ISLANDS_TO_SIZE.put(11, 135);
		ISLANDS_TO_SIZE.put(12, 140);
		ISLANDS_TO_SIZE.put(13, 145);
		ISLANDS_TO_SIZE.put(14, 150);
		ISLANDS_TO_SIZE.put(15, 155);
		ISLANDS_TO_SIZE.put(16, 160);
		ISLANDS_TO_SIZE.put(17, 165);
		ISLANDS_TO_SIZE.put(18, 170);
		ISLANDS_TO_SIZE.put(19, 175);
		ISLANDS_TO_SIZE.put(20, 180);
	}

	private GameSizer() {}

	public static int islandsToSize(int islands) {
		return ISLANDS_TO_SIZE.get(islands);
	}

	public static void setSize(Game game) {
		if (game.getIslands() <= 0) {
			throw new IllegalStateException("Game " + game
					+ " cannot be sized because it has no islands");
		}
		int size = ISLANDS_TO_SIZE.get(game.getIslands());
		game.setGamesize(size);
	}

	public static void setIslands(Game game) {
		int players = game.getPlayers();
		int islands = players + Constants.MAP_EXTRA_SLOTS;
		if (islands % 2 != 0) {
			++islands;
		}
		if (islands > Constants.MAX_PLAYERS_PER_GAME) {
			islands = Constants.MAX_PLAYERS_PER_GAME;
		}
		game.setIslands(islands);
	}

}
