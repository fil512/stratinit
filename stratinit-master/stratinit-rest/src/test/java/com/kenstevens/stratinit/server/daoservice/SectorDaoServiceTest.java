package com.kenstevens.stratinit.server.daoservice;

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

public class SectorDaoServiceTest extends BaseStratInitControllerTest {
    private static final SectorCoords PORT = new SectorCoords(2, 2);

    @Test
    public void shipFlak() {
        joinGamePlayerMe();
        cityDaoService.captureCity(nationMe, PORT);
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
        cityDaoService.captureCity(nationMe, PORT);
        City city = cityDao.getCity(testWorld.getSectorOrNull(PORT));
        city.setBuild(UnitType.INFANTRY, new Date());
        cityDaoService.merge(city);
        WorldView WORLD = sectorDaoService.getAllWorldView(nationMe);
        WorldSector worldSector = WORLD.getWorldSector(PORT);
        assertEquals(Constants.FORT_FLAK, worldSector.getFlak());
    }
}
