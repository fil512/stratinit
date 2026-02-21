package com.kenstevens.stratinit.server.service;

import com.kenstevens.stratinit.BaseStratInitControllerTest;
import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.model.WorldSector;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SectorServiceTest extends BaseStratInitControllerTest {
    private static final SectorCoords PORT = new SectorCoords(2, 2);

    @Test
    public void shipFlak() {
        joinGamePlayerMe();
        cityService.captureCity(nationMe, PORT);
        setBuild(PORT, UnitType.TRANSPORT);
        Unit cruiser = unitService.buildUnit(nationMe, PORT,
                UnitType.CRUISER);
        unitService.buildUnit(nationMe, PORT,
                UnitType.CRUISER);
        WorldView WORLD = sectorService.getAllWorldView(nationMe);
        WorldSector worldSector = WORLD.getWorldSector(PORT);
        int flak = cruiser.getFlak();
        assertEquals(flak * 2, worldSector.getFlak());
    }

    @Test
    public void worldFlak() {
        joinGamePlayerMe();
        cityService.captureCity(nationMe, PORT);
        City city = cityDao.getCity(testWorld.getSectorOrNull(PORT));
        city.setBuild(UnitType.INFANTRY, new Date());
        cityService.merge(city);
        WorldView WORLD = sectorService.getAllWorldView(nationMe);
        WorldSector worldSector = WORLD.getWorldSector(PORT);
        assertEquals(Constants.FORT_FLAK, worldSector.getFlak());
    }
}
