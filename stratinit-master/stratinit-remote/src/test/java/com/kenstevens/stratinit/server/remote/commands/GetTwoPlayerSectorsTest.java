package com.kenstevens.stratinit.server.remote.commands;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.server.remote.TwoPlayerBase;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

public class GetTwoPlayerSectorsTest extends TwoPlayerBase {
	private static final SectorCoords CITY = new SectorCoords(1, 4);
	
	@Test
	public void cityOwner() {
		WorldView WORLD = sectorDaoService.getAllWorldView(nationMe);
		WorldSector city = WORLD.getWorldSector(CITY);
		assertEquals(nationMe, city.getNation());
		declareAlliance();
		allianceDeclared();
		unitDaoService.buildUnit(nationThem, CITY, UnitType.INFANTRY);
		city = sectorDaoService.refreshWorldSector(nationMe, WORLD, city);
		assertEquals(nationMe, city.getNation());
	}
}
