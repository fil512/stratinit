package com.kenstevens.stratinit.world;

import com.kenstevens.stratinit.client.model.Game;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameSizerTest {
	@Test
	public void minPlayers() {
		Game game = new Game();
		GameSizer.setIslands(game);
		assertEquals(2, game.getIslands());
	}

	@Test
	public void oddPlayers() {
		Game game = new Game();
		game.setPlayers(3);
		GameSizer.setIslands(game);
		assertEquals(6, game.getIslands());
	}

	@Test
	public void evenPlayers() {
		Game game = new Game();
		game.setPlayers(4);
		GameSizer.setIslands(game);
		assertEquals(6, game.getIslands());
	}

	@Test
	public void maxPlayers() {
		Game game = new Game();
		game.setPlayers(12);
		GameSizer.setIslands(game);
		assertEquals(10, game.getIslands());
	}
	@Test
	public void maxPlayers10() {
		Game game = new Game();
		game.setPlayers(10);
		GameSizer.setIslands(game);
		assertEquals(10, game.getIslands());
	}
	@Test
	public void maxPlayers9() {
		Game game = new Game();
		game.setPlayers(9);
		GameSizer.setIslands(game);
		assertEquals(10, game.getIslands());
	}
}
