package com.kenstevens.stratinit.server.rest.request.read;

import com.kenstevens.stratinit.dto.SINation;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.server.rest.TwoPlayerBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GetUpdateTest extends TwoPlayerBase {

    @Test
    public void updateNationsShowCityCountAndPower_issue106() {
        SIUpdate update = gameController.getUpdate();
        boolean foundWithCities = false;
        for (SINation nation : update.nations) {
            if (nation.nationId == nationMe.getNationId()) {
                assertTrue(nation.cities > 0,
                        "My nation should have cities but got " + nation.cities);
                foundWithCities = true;
            }
        }
        assertTrue(foundWithCities, "Should have found my nation in the update");
    }
}
