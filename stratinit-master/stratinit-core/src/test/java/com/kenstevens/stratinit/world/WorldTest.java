package com.kenstevens.stratinit.world;

import com.kenstevens.stratinit.client.model.Game;
import com.kenstevens.stratinit.client.model.Sector;
import com.kenstevens.stratinit.client.model.World;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class WorldTest {
	@Test
	public void testWorldCreator() {
        Game game = new Game("test", 40);
        World world = new World(game, true);
        Sector sector;
        sector = world.getSectorOrNull(0, 0);
        assertNotNull(sector);
        sector = world.getSectorOrNull(39, 39);
        assertNotNull(sector);
    }
	
	@Test
	public void testWorldWrap() {
        Game game = new Game("test", 40);
        World world = new World(game, true);
        assertEquals(1, world.getSectorOrNull(0, 0).getCoords().distanceTo(world, world.getSectorOrNull(0, 39).getCoords()));
        assertEquals(1, world.getSectorOrNull(0, 0).getCoords().distanceTo(world, world.getSectorOrNull(39, 0).getCoords()));
        // In hex grid, diagonal wrap (0,0)->(39,39) costs 2 steps, not 1
        assertEquals(2, world.getSectorOrNull(0, 0).getCoords().distanceTo(world, world.getSectorOrNull(39, 39).getCoords()));
    }
}
