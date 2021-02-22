package com.kenstevens.stratinit.server.daoservice;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.WorldSector;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.server.remote.StratInitWebBase;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SectorDaoServiceTest extends StratInitWebBase {
    private static final SectorCoords PORT = new SectorCoords(2, 2);

    @Test
    public void shipFlak() {
        joinGamePlayerMe();
        sectorDaoService.captureCity(nationMe, PORT);
        setBuild(PORT, UnitType.TRANSPORT);
        Unit cruiser = unitDaoService.buildUnit(nationMe, PORT,
                UnitType.CRUISER);
        unitDaoService.buildUnit(nationMe, PORT,
                UnitType.CRUISER);
        WorldView WORLD = sectorDaoService.getAllWorldView(nationMe);
        WorldSector worldSector = WORLD.getWorldSector(PORT);
        int flak = cruiser.getFlak();
        assertEquals(flak * 2, worldSector.getFlak());
    }

    @Test
    public void worldFlak() {
        joinGamePlayerMe();
		sectorDaoService.captureCity(nationMe, PORT);
		City city = sectorDao.getCity(testWorld.getSector(PORT));
		city.setBuild(UnitType.INFANTRY, new Date());
		sectorDaoService.merge(city);
		WorldView WORLD = sectorDaoService.getAllWorldView(nationMe);
		WorldSector worldSector = WORLD.getWorldSector(PORT);
		assertEquals(Constants.FORT_FLAK, worldSector.getFlak());
	}
}
