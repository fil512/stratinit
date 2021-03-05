package com.kenstevens.stratinit.world;

import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.World;
import org.junit.jupiter.api.Test;

public class WorldCreatorTest {
	@Test
	public void testWorldCities() {
		Game game = new Game("test");
		game.setPlayers(6);
		game.setIslands(6);
		GameSizer.setSize(game);
		game.setId(1);
		WorldConfig worldConfig = new WorldConfig();
		WorldCreator worldCreator = new WorldCreator(worldConfig);
		World world = worldCreator.build(game);
		WorldPrinter worldPrinter = new WorldPrinter(world);
		worldPrinter.print();
		WorldHelper worldHelper = new WorldHelper();
		worldHelper.validateWorld(world.getSectors());
	}
}
