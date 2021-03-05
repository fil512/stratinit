package com.kenstevens.stratinit.client.server.rest.commands;

import com.kenstevens.stratinit.client.server.rest.WithUnitsBase;
import com.kenstevens.stratinit.dto.SISector;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GetSectorsTest extends WithUnitsBase {

    @Test
    public void getSectors() {
        List<SISector> sectors = stratInitController.getSectors().getValue();
        assertEquals(82, sectors.size());
        for (SISector sector : sectors) {
            assertNotNull(sector.type);
        }
        buildWorld(sectors);
    }

    private void buildWorld(List<SISector> sectors) {
        SISector[][] world = new SISector[GAME_SIZE][GAME_SIZE];

        for (SISector sector : sectors) {
            world[sector.coords.x][sector.coords.y] = sector;
        }
    }
}
