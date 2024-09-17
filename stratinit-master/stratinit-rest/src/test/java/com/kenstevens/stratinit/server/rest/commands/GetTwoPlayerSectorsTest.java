package com.kenstevens.stratinit.server.rest.commands;

import com.kenstevens.stratinit.client.model.WorldSector;
import com.kenstevens.stratinit.move.WorldView;
import com.kenstevens.stratinit.server.rest.TwoPlayerBase;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetTwoPlayerSectorsTest extends TwoPlayerBase {
    private static final SectorCoords CITY = new SectorCoords(1, 4);

    @Test
    public void cityOwner() {
        WorldView WORLD = sectorDaoService.getAllWorldView(nationMe);
        WorldSector city = WORLD.getWorldSectorOrNull(CITY);
        assertEquals(nationMe, city.getNation());
        declareAlliance();
        allianceDeclared();
        unitDaoService.buildUnit(nationThem, CITY, UnitType.INFANTRY);
        city = sectorDaoService.refreshWorldSector(nationMe, WORLD, city);
        assertEquals(nationMe, city.getNation());
    }
}
