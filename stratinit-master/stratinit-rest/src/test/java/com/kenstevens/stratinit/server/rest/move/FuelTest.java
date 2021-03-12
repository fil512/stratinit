package com.kenstevens.stratinit.server.rest.move;

import com.kenstevens.stratinit.client.model.MoveCost;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.rest.TwoPlayerBase;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FuelTest extends TwoPlayerBase {
    private static final SectorCoords FIGHTER_LAND = new SectorCoords(0, 0);
    private static final SectorCoords FIGHTER_SEA = new SectorCoords(3, 0);
    private static final SectorCoords CARRIER = new SectorCoords(4, 0);
    private static final SectorCoords CITY = new SectorCoords(1, 4);
    private static final SectorCoords NEXT_TO_CITY = new SectorCoords(1, 5);
    private static final SectorCoords ALLY_CITY = new SectorCoords(8, 4);
    @Autowired
    protected SectorDaoService sectorDaoServiceImpl;

    @Test
    public void moveCostsFuel() {
        Unit fighter = unitDaoService.buildUnit(nationMe, FIGHTER_LAND,
                UnitType.FIGHTER);
        Result<MoveCost> result = moveUnits(
                makeUnitList(fighter), FIGHTER_SEA);
        assertResult(result);
        assertTrue(fighter.getUnitBase().getMobility() > fighter.getFuel(), result.toString());
    }

    @Test
    public void myCarrierRefuels() {
        Unit fighter = unitDaoService.buildUnit(nationMe, FIGHTER_LAND,
                UnitType.FIGHTER);
        unitDaoService.buildUnit(nationMe, CARRIER,
                UnitType.CARRIER);
        Result<MoveCost> result = moveUnits(
                makeUnitList(fighter), CARRIER);
        assertResult(result);
        assertRefueled(fighter, result);
    }

    @Test
    public void mySupplyNoRefuels() {
        Unit fighter = unitDaoService.buildUnit(nationMe, FIGHTER_LAND,
                UnitType.FIGHTER);
        unitDaoService.buildUnit(nationMe, CARRIER,
                UnitType.SUPPLY);
        Result<MoveCost> result = moveUnits(
                makeUnitList(fighter), CARRIER);
        assertResult(result);
        assertNotRefueled(fighter, result);
    }

    @Test
    public void myCarrierRefuelsHeli() {
        Unit heli = unitDaoService.buildUnit(nationMe, FIGHTER_LAND,
                UnitType.HELICOPTER);
        unitDaoService.buildUnit(nationMe, CARRIER,
                UnitType.CARRIER);
        Result<MoveCost> result = moveUnits(
                makeUnitList(heli), CARRIER);
        assertResult(result);
        assertRefueled(heli, result);
    }

    @Test
    public void myBBRefuelsHeli() {
        Unit heli = unitDaoService.buildUnit(nationMe, FIGHTER_LAND,
                UnitType.HELICOPTER);
        unitDaoService.buildUnit(nationMe, CARRIER,
                UnitType.BATTLESHIP);
        Result<MoveCost> result = moveUnits(
                makeUnitList(heli), CARRIER);
        assertResult(result);
        assertRefueled(heli, result);
    }

    @Test
    public void myAirportRefuels() {
        Unit fighter = unitDaoService.buildUnit(nationMe, FIGHTER_LAND,
                UnitType.FIGHTER);
        setBuild(CITY, UnitType.FIGHTER);
        Result<MoveCost> result = moveUnits(
                makeUnitList(fighter), CITY);
        assertResult(result);
        assertRefueled(fighter, result);
    }

    private void assertRefueled(Unit fighter, Result<MoveCost> result) {
        assertTrue(fighter.getUnitBase().getMobility() == fighter.getFuel(), result.toString());
    }

    @Test
    public void myPortNoRefuels() {
        Unit fighter = unitDaoService.buildUnit(nationMe, FIGHTER_LAND,
                UnitType.FIGHTER);
        setBuild(CITY, UnitType.INFANTRY);
        Result<MoveCost> result = moveUnits(
                makeUnitList(fighter), CITY);
        assertResult(result);
        assertNotRefueled(fighter, result);
    }

    @Test
    public void myPortRefuelsHeli() {
        Unit heli = unitDaoService.buildUnit(nationMe, FIGHTER_LAND,
                UnitType.HELICOPTER);
        setBuild(CITY, UnitType.INFANTRY);
        Result<MoveCost> result = moveUnits(
                makeUnitList(heli), CITY);
        assertResult(result);
        assertRefueled(heli, result);
    }

    private void assertNotRefueled(Unit fighter, Result<MoveCost> result) {
        assertFalse(fighter.getUnitBase().getMobility() == fighter.getFuel(), result.toString());
    }

    @Test
    public void attackFromCityRefuelsAndReAmmo() {
        declareWar();
        Unit fighter = unitDaoService.buildUnit(nationMe, CITY,
                UnitType.FIGHTER);
        setBuild(CITY, UnitType.FIGHTER);
        unitDaoService.buildUnit(nationThem, NEXT_TO_CITY,
                UnitType.INFANTRY);
        Result<MoveCost> result = moveUnits(
                makeUnitList(fighter), NEXT_TO_CITY);
        assertResult(result);
        assertRefueled(fighter, result);
        assertTrue(fighter.getUnitBase().getAmmo() == fighter.getAmmo(), result.toString());
    }

    @Test
    public void allyCarrierRefuels() {
        declareAlliance();
        allianceDeclared();
        Unit fighter = unitDaoService.buildUnit(nationMe, FIGHTER_LAND,
                UnitType.FIGHTER);
        unitDaoService.buildUnit(nationThem, CARRIER,
                UnitType.CARRIER);
        Result<MoveCost> result = moveUnits(
                makeUnitList(fighter), CARRIER);
        assertResult(result);
        assertRefueled(fighter, result);
    }

    @Test
    public void allyCityRefuels() {
        declareAlliance();
        allianceDeclared();
        Unit fighter = unitDaoService.buildUnit(nationMe, FIGHTER_LAND,
                UnitType.FIGHTER);
        setBuild(ALLY_CITY, UnitType.FIGHTER);
        Result<MoveCost> result = moveUnits(
                makeUnitList(fighter), ALLY_CITY);
        assertResult(result);
        assertRefueled(fighter, result);
    }

    @Test
    public void carrierMoveRefuelsMe() {
        Unit fighter = unitDaoService.buildUnit(nationMe, FIGHTER_LAND,
                UnitType.FIGHTER);
        Unit carrier = unitDaoService.buildUnit(nationMe, CARRIER,
                UnitType.CARRIER);

        Result<MoveCost> result = moveUnits(
                makeUnitList(fighter), FIGHTER_SEA);
        assertResult(result);
        assertTrue(fighter.getUnitBase().getMobility() > fighter.getFuel(), result.toString());
        result = moveUnits(
                makeUnitList(carrier), FIGHTER_SEA);
        assertRefueled(fighter, result);
    }

    @Test
    public void allyCarrierMoveRefuelsMe() {
        declareAlliance();
        allianceDeclared();

        Unit fighter = unitDaoService.buildUnit(nationThem, FIGHTER_LAND,
                UnitType.FIGHTER);
        Unit carrier = unitDaoService.buildUnit(nationMe, CARRIER,
                UnitType.CARRIER);
        Result<MoveCost> result = moveUnits(nationThem,
                makeUnitList(fighter), FIGHTER_SEA);
        assertResult(result);
        assertTrue(fighter.getUnitBase().getMobility() > fighter.getFuel(), result.toString());
        result = moveUnits(
                makeUnitList(carrier), FIGHTER_SEA);
        assertRefueled(fighter, result);
    }

    @Test
    public void neutralCarrierCannotEnter() {
        Unit fighter = unitDaoService.buildUnit(nationThem, FIGHTER_LAND,
                UnitType.FIGHTER);
        Unit carrier = unitDaoService.buildUnit(nationMe, CARRIER,
                UnitType.CARRIER);

        Result<MoveCost> result = moveUnits(nationThem,
                makeUnitList(fighter), FIGHTER_SEA);
        assertResult(result);
        assertTrue(fighter.getUnitBase().getMobility() > fighter.getFuel(), result.toString());

        result = moveUnits(
                makeUnitList(carrier), FIGHTER_SEA);
        assertTrue(fighter.getUnitBase().getMobility() > fighter.getFuel(), result.toString());
    }

    @Test
    public void warCarrierCannotEnter() {
        declareWar();
        Unit fighter = unitDaoService.buildUnit(nationThem, FIGHTER_LAND,
                UnitType.FIGHTER);
        Unit carrier = unitDaoService.buildUnit(nationMe, CARRIER,
                UnitType.CARRIER);

        Result<MoveCost> result = moveUnits(nationThem,
                makeUnitList(fighter), FIGHTER_SEA);
        assertResult(result);
        assertTrue(fighter.getUnitBase().getMobility() > fighter.getFuel(), result.toString());

        result = moveUnits(
                makeUnitList(carrier), FIGHTER_SEA);
        assertTrue(fighter.getUnitBase().getMobility() > fighter.getFuel(), result.toString());
    }
}
