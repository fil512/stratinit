package com.kenstevens.stratinit.server.rest.commands;

import com.kenstevens.stratinit.dto.SISector;
import com.kenstevens.stratinit.helper.GameHelper;
import com.kenstevens.stratinit.server.rest.WithUnitsBase;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GetSectorsTest extends WithUnitsBase {

    @Test
    public void getSectors() {
        List<SISector> sectors = nationController.getSectors();
        assertEquals(82, sectors.size());
        for (SISector sector : sectors) {
            assertNotNull(sector.type);
        }
        buildWorld(sectors);
    }

    private void buildWorld(List<SISector> sectors) {
        SISector[][] world = new SISector[GameHelper.GAME_SIZE][GameHelper.GAME_SIZE];

        for (SISector sector : sectors) {
            world[sector.coords.x][sector.coords.y] = sector;
        }
    }
}
