package com.kenstevens.stratinit.world;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorType;

public class WorldHelper {

	public void validateWorld(List<Sector> sectors) {
		int[]sectorCount = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		int[]cityCount = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		int[]startCityCount = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		for (Sector sector : sectors) {
			int island = sector.getIsland();
			if (island == Constants.UNASSIGNED) {
				continue;
			}
			++sectorCount[island];
			assertNotNull(sector.getType());
			if (sector.getType() == SectorType.NEUTRAL_CITY) {
				++cityCount[island];
			} else if (sector.getType() == SectorType.START_CITY) {
				++ startCityCount[island];
			}
		}
		for (int i = 0; i < 2; ++ i) {
			assertTrue("Sectors on island "+i+" > 10.  Was: "+sectorCount[i], sectorCount[i] > 10);
// TODO test adding players
//			assertEquals("Cities on island "+i, 2, cityCount[i]);
//			assertEquals("Start cities on island "+i, 2, startCityCount[i]);
		}
	}
}
