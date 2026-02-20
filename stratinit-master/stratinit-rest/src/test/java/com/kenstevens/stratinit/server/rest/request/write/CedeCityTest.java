package com.kenstevens.stratinit.server.rest.request.write;

import com.kenstevens.stratinit.client.model.Sector;
import com.kenstevens.stratinit.client.model.SectorSeen;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.dto.SICityUpdate;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.CedeCityJson;
import com.kenstevens.stratinit.server.rest.TwoPlayerBase;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CedeCityTest extends TwoPlayerBase {
    @Test
    public void cedeCity() {
        allianceDeclared();
        declareAlliance();
        List<SICityUpdate> cities = stratInitController.getCities().getValue();
        assertEquals(2, cities.size());
        SICityUpdate sicity = cities.get(0);
        assertEquals(sicity.nationId, nationMeId);
        Sector sector = testWorld.getSectorOrNull(sicity.coords);
        SectorSeen sectorSeen = sectorDao.findSectorSeen(nationThem, sector);
        assertNull(sectorSeen);
        List<Unit> units = new ArrayList<>(unitDao.getUnits(sector));
        assertEquals(4, units.size());
        assertEquals(nationMe, units.get(0).getNation());

        stratInitController.cedeCity(new CedeCityJson(sicity, nationThemId));

        cities = stratInitController.getCities().getValue();
        assertEquals(1, cities.size());
        SICityUpdate seenCity = findSeenCity(sicity);
        assertNotNull(seenCity);
        assertEquals(nationThemId, seenCity.nationId);

        units = new ArrayList<>(unitDao.getUnits(sector));
        assertEquals(4, units.size());
        assertEquals(nationThem, units.get(0).getNation());

        sectorSeen = sectorDao.findSectorSeen(nationThem, sector);
        assertNotNull(sectorSeen);

    }

    private SICityUpdate findSeenCity(SICityUpdate sicity) {
        Result<List<SICityUpdate>> seencities = stratInitController.getSeenCities();
        for (SICityUpdate seenCity : seencities.getValue()) {
            if (seenCity.coords.equals(sicity.coords)) {
                return seenCity;
            }
        }
        return null;
    }
}
