package com.kenstevens.stratinit.client.model;

import java.util.*;

public class GameList implements Iterable<GameView> {
	private final Map<Integer, GameView> gameMap = new Hashtable<Integer, GameView>();

	private static final Comparator<GameView> BY_GAME_ID = new Comparator<GameView>() {
		public int compare(GameView p1, GameView p2) {
			return Integer.valueOf(p2.getId()).compareTo(p1.getId());
		}
	};

	private void add(GameView game) {
		gameMap.put(game.getId(), game);
	}

	public GameView get(int id) {
		return gameMap.get(id);
	}

	public int size() {
		return gameMap.size();
	}

	public boolean isEmpty() {
		return gameMap.isEmpty();
	}

	public void clear() {
		gameMap.clear();
	}

	public GameView get(String gameIdString) {
		return get(Integer.valueOf(gameIdString));
	}

	@Override
	public Iterator<GameView> iterator() {
		List<GameView> list = new ArrayList<GameView>(gameMap.values());

		Collections.sort(list, BY_GAME_ID);
		return list.iterator();
	}

	public void remove(String gameIdString) {
		int gameId = Integer.valueOf(gameIdString);
		gameMap.remove(gameId);
	}

	public void addAll(List<GameView> games) {
		for (GameView game : games) {
			add(game);
		}
	}
}
