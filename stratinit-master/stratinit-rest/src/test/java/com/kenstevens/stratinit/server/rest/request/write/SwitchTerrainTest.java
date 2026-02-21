package com.kenstevens.stratinit.server.rest.request.write;

import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.rest.TwoPlayerBase;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.SectorType;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SwitchTerrainTest extends TwoPlayerBase {
    private static final SectorCoords INLAND = new SectorCoords(1, 1);
    private static final SectorCoords COASTLAND = new SectorCoords(2, 1);
    private static final SectorCoords BEACHWATER = new SectorCoords(3, 1);
    private static final SectorCoords DEEPSEA = new SectorCoords(4, 1);
    private static final SectorCoords CITY = new SectorCoords(2, 2);

    @Test
    public void switchTerrainNotEnoughMob() {
        Unit eng = unitService.buildUnit(nationMe, COASTLAND,
                UnitType.ENGINEER);

        assertEquals(SectorType.LAND, testWorld.getSectorOrNull(COASTLAND).getType());
        Result<SIUpdate> result = switchTerrain(eng);
        assertFalseResult(result);
        assertEquals(SectorType.LAND, testWorld.getSectorOrNull(COASTLAND).getType());
    }

    @Test
    public void switchTerrainNotEng() {
        Unit tank = unitService.buildUnit(nationMe, COASTLAND,
                UnitType.TANK);
        tank.setMobility(tank.getMaxMobility());
        assertEquals(SectorType.LAND, testWorld.getSectorOrNull(COASTLAND).getType());
        Result<SIUpdate> result = switchTerrain(tank);
        assertFalseResult(result);
        assertEquals(SectorType.LAND, testWorld.getSectorOrNull(COASTLAND).getType());
    }

    @Test
    public void switchTerrainGotCompany() {
        Unit eng = unitService.buildUnit(nationMe, COASTLAND,
                UnitType.ENGINEER);
        eng.setMobility(eng.getMaxMobility());
        unitService.buildUnit(nationMe, COASTLAND,
                UnitType.INFANTRY);

        assertEquals(SectorType.LAND, testWorld.getSectorOrNull(COASTLAND).getType());
        Result<SIUpdate> result = switchTerrain(eng);
        assertFalseResult(result);
        assertEquals(SectorType.LAND, testWorld.getSectorOrNull(COASTLAND).getType());
    }

    @Test
    public void switchTerrainInland() {
        Unit eng = unitService.buildUnit(nationMe, INLAND,
                UnitType.ENGINEER);
        eng.setMobility(eng.getMaxMobility());

        assertEquals(SectorType.LAND, testWorld.getSectorOrNull(INLAND).getType());
        Result<SIUpdate> result = switchTerrain(eng);
        assertFalseResult(result);
        assertEquals(SectorType.LAND, testWorld.getSectorOrNull(INLAND).getType());
    }

    @Test
    public void switchTerrainCoastland() {
        Unit eng = unitService.buildUnit(nationMe, COASTLAND,
                UnitType.ENGINEER);
        eng.setMobility(eng.getMaxMobility());

        assertEquals(SectorType.LAND, testWorld.getSectorOrNull(COASTLAND).getType());
        Result<SIUpdate> result = switchTerrain(eng);
        assertResult(result);
        assertEquals(SectorType.WATER, testWorld.getSectorOrNull(COASTLAND).getType());
    }

    @Test
    public void switchTerrainBeachwater() {
        Unit eng = unitService.buildUnit(nationMe, BEACHWATER,
                UnitType.ENGINEER);
        eng.setMobility(eng.getMaxMobility());

        assertEquals(SectorType.WATER, testWorld.getSectorOrNull(BEACHWATER).getType());
        Result<SIUpdate> result = switchTerrain(eng);
        assertResult(result);
        assertEquals(SectorType.LAND, testWorld.getSectorOrNull(BEACHWATER).getType());
    }

    @Test
    public void switchTerrainDeepSea() {
        Unit eng = unitService.buildUnit(nationMe, DEEPSEA,
                UnitType.ENGINEER);
        eng.setMobility(eng.getMaxMobility());

        assertEquals(SectorType.WATER, testWorld.getSectorOrNull(DEEPSEA).getType());
        Result<SIUpdate> result = switchTerrain(eng);
        assertFalseResult(result);
        assertEquals(SectorType.WATER, testWorld.getSectorOrNull(DEEPSEA).getType());
    }

    @Test
    public void switchTerrainCity() {
        Unit eng = unitService.buildUnit(nationMe, CITY,
                UnitType.ENGINEER);
        eng.setMobility(eng.getMaxMobility());

        assertEquals(SectorType.NEUTRAL_CITY, testWorld.getSectorOrNull(CITY).getType());
        Result<SIUpdate> result = switchTerrain(eng);
        assertFalseResult(result);
        assertEquals(SectorType.NEUTRAL_CITY, testWorld.getSectorOrNull(CITY).getType());
    }

}
