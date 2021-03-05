package com.kenstevens.stratinit.client.server.rest.request.write;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.client.model.Sector;
import com.kenstevens.stratinit.client.model.SectorSeen;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.server.rest.TwoPlayerBase;
import com.kenstevens.stratinit.dto.SICity;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.remote.request.CedeCityJson;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CedeCityTest extends TwoPlayerBase {
    @Test
    public void cedeCity() {
        allianceDeclared();
        declareAlliance();
        List<SICity> cities = stratInitController.getCities().getValue();
        assertEquals(2, cities.size());
        SICity sicity = cities.get(0);
        assertEquals(sicity.nationId, nationMeId);
        Sector sector = testWorld.getSector(sicity.coords);
        SectorSeen sectorSeen = sectorDao.findSectorSeen(nationThem, sector);
        assertNull(sectorSeen);
        List<Unit> units = Lists.newArrayList(unitDao.getUnits(sector));
        assertEquals(4, units.size());
        assertEquals(nationMe, units.get(0).getNation());

        stratInitController.cedeCity(new CedeCityJson(sicity, nationThemId));

        cities = stratInitController.getCities().getValue();
        assertEquals(1, cities.size());
        SICity seenCity = findSeenCity(sicity);
        assertNotNull(seenCity);
        assertEquals(nationThemId, seenCity.nationId);

        units = Lists.newArrayList(unitDao.getUnits(sector));
        assertEquals(4, units.size());
        assertEquals(nationThem, units.get(0).getNation());

        sectorSeen = sectorDao.findSectorSeen(nationThem, sector);
        assertNotNull(sectorSeen);

    }

    private SICity findSeenCity(SICity sicity) {
        Result<List<SICity>> seencities = stratInitController.getSeenCities();
        for (SICity seenCity : seencities.getValue()) {
            if (seenCity.coords.equals(sicity.coords)) {
                return seenCity;
            }
        }
        return null;
    }
}
