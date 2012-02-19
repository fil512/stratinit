package com.kenstevens.stratinit.world;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.model.World;


public class WorldTest {
	@Test
	public void testWorldCreator() {
		Game game = new Game("test", 40);
		World world = new World(game, true);
		Sector sector;
		sector = world.getSector(0,0);
		assertNotNull(sector);
		sector = world.getSector(39,39);
		assertNotNull(sector);
	}
	
	@Test
	public void testWorldWrap() {
		Game game = new Game("test", 40);
		World world = new World(game, true);
		assertEquals(1, world.getSector(0,0).getCoords().distanceTo(world, world.getSector(0,39).getCoords()));
		assertEquals(1, world.getSector(0,0).getCoords().distanceTo(world, world.getSector(39,0).getCoords()));
		assertEquals(1, world.getSector(0,0).getCoords().distanceTo(world, world.getSector(39,39).getCoords()));
	}
}
