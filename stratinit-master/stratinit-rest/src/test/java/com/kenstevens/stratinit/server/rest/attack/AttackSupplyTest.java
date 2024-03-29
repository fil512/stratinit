package com.kenstevens.stratinit.server.rest.attack;

import com.kenstevens.stratinit.client.model.MoveCost;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.rest.TwoPlayerBase;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AttackSupplyTest extends TwoPlayerBase {
    private static final SectorCoords DEF_PORT = new SectorCoords(5, 8);
    private static final SectorCoords NEAR_PORT = new SectorCoords(6, 8);
    private static final SectorCoords PORT = new SectorCoords(7, 8);
    @Autowired
    protected SectorDaoService sectorDaoService;

    @Test
    public void attackInSupply() {
        declareWar();
        Unit dest = unitDaoService.buildUnit(nationMe, NEAR_PORT,
                UnitType.DESTROYER);
        cityDaoService.captureCity(nationMe, PORT);
        setBuild(PORT, UnitType.TRANSPORT);
        unitDaoService.buildUnit(nationThem, DEF_PORT,
                UnitType.DESTROYER);
        Result<MoveCost> result = moveUnits(
                makeUnitList(dest), DEF_PORT);
        assertResult(result);
        assertTrue(dest.getMobility() == dest.getUnitBase().getMobility() - 1);
        assertTrue(dest.getAmmo() == dest.getUnitBase().getAmmo(), result.toString());
    }

    @Test
    public void bomberNoGainsAmmoInPort() {
        declareWar();
        Unit bomber = unitDaoService.buildUnit(nationMe, PORT,
                UnitType.NAVAL_BOMBER);
        cityDaoService.captureCity(nationMe, PORT);
        setBuild(PORT, UnitType.TRANSPORT);
        Unit supply = unitDaoService.buildUnit(nationThem, NEAR_PORT,
                UnitType.SUPPLY);
        Result<MoveCost> result = moveUnits(
                makeUnitList(bomber), NEAR_PORT);
        assertResult(result);
        assertFired(result, bomber);
        assertDamaged(result, supply);
        assertFullFuel(result, bomber);
    }

    @Test
    public void bomberGainsAmmoInAirport() {
        declareWar();
        Unit bomber = unitDaoService.buildUnit(nationMe, PORT,
                UnitType.NAVAL_BOMBER);
        cityDaoService.captureCity(nationMe, PORT);
        setBuild(PORT, UnitType.BASE);
        Unit supply = unitDaoService.buildUnit(nationThem, NEAR_PORT,
                UnitType.SUPPLY);
        Result<MoveCost> result = moveUnits(
                makeUnitList(bomber), NEAR_PORT);
        assertResult(result);
        assertNotFired(result, bomber);
        assertDamaged(result, supply);
    }

    @Test
    public void attackOutOfSupply() {
        declareWar();
        Unit dest = unitDaoService.buildUnit(nationMe, NEAR_PORT,
                UnitType.DESTROYER);
        unitDaoService.buildUnit(nationThem, DEF_PORT,
                UnitType.DESTROYER);
        Result<MoveCost> result = moveUnits(
                makeUnitList(dest), DEF_PORT);
        assertResult(result);
        assertTrue(dest.getMobility() == dest.getUnitBase().getMobility() - 2);
        assertTrue(dest.getAmmo() == dest.getUnitBase().getAmmo() - 1);
    }

    @Test
    public void attackPatrolOutOfSupply() {
        declareWar();
        Unit patrol = unitDaoService.buildUnit(nationMe, NEAR_PORT,
                UnitType.PATROL);
        unitDaoService.buildUnit(nationThem, DEF_PORT,
                UnitType.SUPPLY);
        Result<MoveCost> result = moveUnits(
                makeUnitList(patrol), DEF_PORT);
        assertResult(result);
        assertTrue(patrol.getMobility() == patrol.getUnitBase().getMobility() - 1);
        assertTrue(patrol.getAmmo() == patrol.getUnitBase().getAmmo() - 1);
    }

    @Test
    public void defenderInSupply() {
        declareWar();
        Unit dest = unitDaoService.buildUnit(nationMe, NEAR_PORT,
                UnitType.DESTROYER);
        cityDaoService.captureCity(nationThem, PORT);
        setBuild(PORT, UnitType.TRANSPORT);
        Unit def = unitDaoService.buildUnit(nationThem, DEF_PORT,
                UnitType.DESTROYER);
        Result<MoveCost> result = moveUnits(
                makeUnitList(dest), DEF_PORT);
        assertResult(result);
        assertTrue(def.getMobility() == def.getUnitBase().getMobility());
        assertTrue(def.getAmmo() == def.getUnitBase().getAmmo());
    }

    @Test
    public void defenderOutOfSupply() {
        declareWar();
        Unit dest = unitDaoService.buildUnit(nationMe, NEAR_PORT,
                UnitType.DESTROYER);
        Unit def = unitDaoService.buildUnit(nationThem, DEF_PORT,
                UnitType.DESTROYER);
        Result<MoveCost> result = moveUnits(
                makeUnitList(dest), DEF_PORT);
        assertResult(result);
        assertTrue(def.getMobility() == def.getUnitBase().getMobility());
        assertTrue(def.getAmmo() == def.getUnitBase().getAmmo() - 1);
    }


    @Test
    public void zepMoveIntoAllyCityRefillsAmmo() {
        declareAlliance();
        allianceDeclared();
        Unit zep = unitDaoService.buildUnit(nationMe, NEAR_PORT,
                UnitType.ZEPPELIN);
        zep.decreaseAmmo();
        cityDaoService.captureCity(nationThem, PORT);
        setBuild(PORT, UnitType.TRANSPORT);
        Result<MoveCost> result = moveUnits(
                makeUnitList(zep), PORT);
        assertResult(result);
        assertNotFired(result, zep);
        assertFullFuel(result, zep);
    }


}
