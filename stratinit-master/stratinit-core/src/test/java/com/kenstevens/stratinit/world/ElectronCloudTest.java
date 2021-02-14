package com.kenstevens.stratinit.world;

import com.kenstevens.stratinit.model.Game;
import com.kenstevens.stratinit.type.SectorCoords;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class ElectronCloudTest {
	@Test
	public void testEnergy() {
		ElectronCloud cloud = new ElectronCloud(100, 10);
		Game testGame = new Game();
		testGame.setId(1);
		cloud.init(testGame);
		cloud = cloud.drift();
		int mindist = 100;
		for (int i = 0; i < 10; ++i) {
			SectorCoords coords = cloud.getPlayerCoord(i);
			for (int j = 0; j < 10; ++j) {
				if (i == j) {
					continue;
				}
				SectorCoords coords2 = cloud.getPlayerCoord(j);
				int dist = coords.distanceTo(cloud, coords2);
				if (dist < mindist) {
					mindist = dist;
				}
			}
		}
		assertTrue(mindist > 12);
	}
}
