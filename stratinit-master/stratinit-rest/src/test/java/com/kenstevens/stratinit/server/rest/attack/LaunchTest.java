package com.kenstevens.stratinit.server.rest.attack;

import com.kenstevens.stratinit.client.model.*;
import com.kenstevens.stratinit.dto.SICityUpdate;
import com.kenstevens.stratinit.dto.SISector;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.helper.PlayerHelper;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.rest.TwoPlayerBase;
import com.kenstevens.stratinit.type.*;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LaunchTest extends TwoPlayerBase {
    private static final SectorCoords CITY = new SectorCoords(4, 1);
    private static final SectorCoords TWEENCITY = new SectorCoords(8, 5);
    private static final SectorCoords ECITY = new SectorCoords(8, 4);
    private static final SectorCoords ECITYW = new SectorCoords(7, 4);
    private static final SectorCoords BYCITY = new SectorCoords(8 - Constants.SATELLITE_SIGHT, 6);
    private static final SectorCoords OUTCITY = new SectorCoords(8 - Constants.SATELLITE_SIGHT - 1, 6);

    @Test
    public void icbm3KillsCitiesAndUnits() {
        Unit icbm = unitService.buildUnit(nationMe, CITY,
                UnitType.ICBM_3);

        assertNoDevastation();
        Result<MoveCost> result = moveUnits(makeUnitList(icbm), ECITY);
        assertResult(result);
        assertDevastation();
        assertEquals(MoveType.LAUNCH_ICBM, result.getValue().getMoveType());
    }

    @Test
    public void icbm2KillsCitiesAndUnits() {
        Unit icbm = unitService.buildUnit(nationMe, CITY,
                UnitType.ICBM_2);

        assertNoDevastation();
        Result<MoveCost> result = moveUnits(makeUnitList(icbm), TWEENCITY);
        assertResult(result);
        assertDevastation();
    }

    private void assertNoDevastation() {
        List<City> cities = cityDao.getCities(nationThem);
        assertEquals(2, cities.size());
        List<Unit> units = unitDao.getUnits(nationThem);
        assertEquals(5, units.size());
    }

    private void assertDevastation() {
        List<City> cities;
        List<Unit> units;
        cities = cityDao.getCities(nationThem);
        assertEquals(0, cities.size());
        units = unitDao.getUnits(nationThem);
        assertEquals(0, units.size());
        Sector sector = testWorld.getSectorOrNull(ECITY);
        assertEquals(SectorType.WASTELAND, sector.getType());
    }

    @Test
    public void satSeesSectors() {
        Unit sat = unitService.buildUnit(nationMe, CITY,
                UnitType.SATELLITE);
        List<SISector> sseen = nationController.getSectors();
        List<SICityUpdate> cseen = cityController.getSeenCities();
        List<SIUnit> useen = unitController.getSeenUnits();
        Result<MoveCost> result = moveUnits(makeUnitList(sat), ECITY);
        assertResult(result);
        List<SISector> sseen2 = nationController.getSectors();
        List<SICityUpdate> cseen2 = cityController.getSeenCities();
        List<SIUnit> useen2 = unitController.getSeenUnits();
        assertTrue(sseen2.size() > sseen.size());
        assertTrue(cseen2.size() > cseen.size());
        assertTrue(useen2.size() > useen.size());
        assertEquals(MoveType.LAUNCH_SATELLITE, result.getValue().getMoveType());
    }

    @Test
    public void satSeesUnits() {
        Unit sat = unitService.buildUnit(nationMe, CITY,
                UnitType.SATELLITE);
        List<SIUnit> unitsSeen = unitController.getSeenUnits();
        assertEquals(0, unitsSeen.size());
        Result<MoveCost> result = moveUnits(makeUnitList(sat), BYCITY);
        assertResult(result);
        unitsSeen = unitController.getSeenUnits();
        assertEquals(5, unitsSeen.size());
    }

    @Test
    public void satSeesMovedUnits() {
        Unit sat = unitService.buildUnit(nationMe, CITY,
                UnitType.SATELLITE);
        Unit inf = unitService.buildUnit(nationThem, ECITY, UnitType.INFANTRY);
        List<SIUnit> unitsSeen = unitController.getSeenUnits();
        assertEquals(0, unitsSeen.size());
        Result<MoveCost> result = moveUnits(makeUnitList(sat), OUTCITY);
        assertResult(result);
        unitsSeen = unitController.getSeenUnits();
        assertEquals(0, unitsSeen.size());
        result = moveUnits(nationThem, makeUnitList(inf), ECITYW);
        assertResult(result);
        inf = unitDao.findUnit(inf.getId());
        assertEquals(ECITYW, inf.getCoords());
        Collection<SectorSeen> sectorsSeen = sectorDao.getSectorsSeen(nationMe);
        assertTrue(contains(sectorsSeen, ECITYW));
        unitsSeen = unitController.getSeenUnits();
        assertEquals(1, unitsSeen.size());
    }

    private boolean contains(Collection<SectorSeen> sectorsSeen,
                             SectorCoords coords) {
        for (SectorSeen sectorSeen : sectorsSeen) {
            if (sectorSeen.getCoords().equals(coords)) {
                return true;
            }
        }
        return false;

    }

    @Test
    public void allyNoSeesSectors() {
        List<SISector> sseen = nationController.getSectors();
        List<SICityUpdate> cseen = cityController.getSeenCities();
        List<SIUnit> useen = unitController.getSeenUnits();
        setRelation(nationThemId, RelationType.ALLIED);
        List<SISector> sseen2 = nationController.getSectors();
        List<SICityUpdate> cseen2 = cityController.getSeenCities();
        List<SIUnit> useen2 = unitController.getSeenUnits();
        assertFalse(sseen2.size() > sseen.size());
        assertFalse(cseen2.size() > cseen.size());
        assertFalse(useen2.size() > useen.size());
    }

    @Test
    public void alliedSeesSectors() {
        List<SISector> sseen = nationController.getSectors();
        List<SICityUpdate> cseen = cityController.getSeenCities();
        List<SIUnit> useen = unitController.getSeenUnits();
        setAuthentication(PLAYER_THEM_NAME);
        setRelation(nationMeId, RelationType.ALLIED);
        setAuthentication(PlayerHelper.PLAYER_ME);
        setRelation(nationThemId, RelationType.ALLIED);
        List<SISector> sseen2 = nationController.getSectors();
        List<SICityUpdate> cseen2 = cityController.getSeenCities();
        List<SIUnit> useen2 = unitController.getSeenUnits();
        assertTrue(sseen2.size() > sseen.size());
        assertTrue(cseen2.size() > cseen.size());
        assertTrue(useen2.size() > useen.size());
    }

}
