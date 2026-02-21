package com.kenstevens.stratinit.server.rest.attack;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.model.MoveCost;
import com.kenstevens.stratinit.client.model.Sector;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.event.svc.EventQueue;
import com.kenstevens.stratinit.server.rest.TwoPlayerBase;
import com.kenstevens.stratinit.type.CityType;
import com.kenstevens.stratinit.type.MoveType;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unused")
public class AttackTakeCityTest extends TwoPlayerBase {
    private static final SectorCoords ATTACK_FROM = new SectorCoords(1, 1);
    private static final SectorCoords LAND = new SectorCoords(2, 1);
    private static final SectorCoords CITY = new SectorCoords(2, 2);
    private static final SectorCoords BESIDE = new SectorCoords(3, 2);
    @Autowired
    private EventQueue eventQueue;

    @Test
    public void takeCityFromTransport() {
        Unit inf = unitService.buildUnit(nationMe, BESIDE,
                UnitType.INFANTRY);
        Unit trans = unitService.buildUnit(nationMe, BESIDE,
                UnitType.TRANSPORT);
        cityService.captureCity(nationThem, CITY);
        declareWar();
        Result<MoveCost> result = moveUnits(makeUnitList(inf), CITY);
        assertResult(result);
        assertDamaged(result, inf);
        assertTookCity(result);
    }

    @Test
    public void takeNeutralCityFromTransportDamagedInf() {
        Unit inf = unitService.buildUnit(nationMe, BESIDE,
                UnitType.INFANTRY);
        inf.damage(1);
        Unit trans = unitService.buildUnit(nationMe, BESIDE,
                UnitType.TRANSPORT);
        Result<MoveCost> result = moveUnits(
                makeUnitList(inf), CITY);
        assertResult(result);
        inf = unitDao.findUnit(inf.getId());
        assertNull(inf);
        List<Unit> units = unitDao.getUnits(nationMe);
        assertFalse(units.contains(inf));
        assertTookCity(result);
    }

    @Test
    public void takeNeutralCityFromTransport() {
        Unit inf = unitService.buildUnit(nationMe, BESIDE,
                UnitType.INFANTRY);
        Unit trans = unitService.buildUnit(nationMe, BESIDE,
                UnitType.TRANSPORT);
        Result<MoveCost> result = moveUnits(
                makeUnitList(inf), CITY);
        assertResult(result);
        assertDamaged(result, inf);
        assertTookCity(result);
    }

    @Test
    public void takeNeutralCity2FromTransport() {
        Unit inf = unitService.buildUnit(nationMe, BESIDE,
                UnitType.INFANTRY);
        Unit inf2 = unitService.buildUnit(nationMe, BESIDE,
                UnitType.INFANTRY);
        Unit trans = unitService.buildUnit(nationMe, BESIDE,
                UnitType.TRANSPORT);
        Result<MoveCost> result = moveUnits(
                makeUnitList(inf, inf2), CITY);
        assertResult(result);
        assertDamaged(result, inf);
        assertTookCity(result);
    }

    @Test
    public void cannotTakeOccuppiedCityFromTransport() {
        Unit inf = unitService.buildUnit(nationMe, BESIDE,
                UnitType.INFANTRY);
        Unit trans = unitService.buildUnit(nationMe, BESIDE,
                UnitType.TRANSPORT);
        Unit einf = unitService.buildUnit(nationThem, CITY,
                UnitType.TRANSPORT);
        cityService.captureCity(nationThem, CITY);
        declareWar();
        Result<MoveCost> result = moveUnits(makeUnitList(inf), CITY);
        assertFalseResult(result);
        inf = unitDao.findUnit(inf.getId());
        assertEquals(BESIDE, inf.getCoords());
        assertNoTookCity(result);
    }

    @Test
    public void cannotTakeCityWithPlane() {
        Unit plane1 = unitService.buildUnit(nationMe, BESIDE,
                UnitType.FIGHTER);
        Unit plane2 = unitService.buildUnit(nationMe, BESIDE,
                UnitType.FIGHTER);
        Unit plane3 = unitService.buildUnit(nationMe, BESIDE,
                UnitType.FIGHTER);
        Unit zeppelin = unitService.buildUnit(nationThem, CITY,
                UnitType.ZEPPELIN);
        cityService.captureCity(nationThem, CITY);
        declareWar();
        Result<MoveCost> result = moveUnits(
                makeUnitList(plane1, plane2, plane3), CITY);
        assertResult(result);
        assertNoTookCity(result);
    }


    @Test
    public void toLandFromTransport() {
        Unit inf = unitService.buildUnit(nationMe, BESIDE,
                UnitType.INFANTRY);
        Unit trans = unitService.buildUnit(nationMe, BESIDE,
                UnitType.TRANSPORT);
        cityService.captureCity(nationThem, CITY);
        Result<MoveCost> result = moveUnits(
                makeUnitList(inf), LAND);
        assertResult(result);
        inf = unitDao.findUnit(inf.getId());
        assertEquals(LAND, inf.getCoords());
    }

    private void assertTookCity(Result<MoveCost> result) {
        List<City> cities = cityDao.getCities(nationMe);
        Sector citySector = testWorld.getSectorOrNull(CITY);
        City city = cityDao.getCity(citySector);
        assertTrue(cities.contains(city), result.toString());
        assertEquals(MoveType.TAKE_CITY, result.getValue().getMoveType());
    }

    private void assertNoTookCity(Result<MoveCost> result) {
        List<City> cities = cityDao.getCities(nationMe);
        Sector citySector = testWorld.getSectorOrNull(CITY);
        City city = cityDao.getCity(citySector);
        assertFalse(cities.contains(city), result.toString());
        assertFalse(MoveType.TAKE_CITY.equals(result.getValue().getMoveType()));
    }

    @Test
    public void takeCity() {
        Unit inf = unitService.buildUnit(nationMe, ATTACK_FROM,
                UnitType.INFANTRY);
        cityService.captureCity(nationThem, CITY);
        declareWar();
        Result<MoveCost> result = moveUnits(
                makeUnitList(inf), CITY);
        assertResult(result);
        assertDamaged(result, inf);
        assertTookCity(result);
        Sector citySector = testWorld.getSectorOrNull(CITY);
        City city = cityDao.getCity(citySector);
        assertEquals(CityType.BASE, city.getType());
        assertEquals(UnitType.BASE, city.getBuild());
        assertFalse(eventQueue.cancel(city));
    }

    @Test
    public void takeCityNeutral() {
        Unit inf = unitService.buildUnit(nationMe, ATTACK_FROM,
                UnitType.INFANTRY);
        Result<MoveCost> result = moveUnits(
                makeUnitList(inf), CITY);
        assertResult(result);
        assertDamaged(result, inf);
        assertTookCity(result);
        Sector citySector = testWorld.getSectorOrNull(CITY);
        City city = cityDao.getCity(citySector);
        assertEquals(CityType.BASE, city.getType());
        assertEquals(UnitType.BASE, city.getBuild());
        assertFalse(eventQueue.cancel(city));
    }

    @Test
    public void takeCityThenMoveIn() {
        Unit inf = unitService.buildUnit(nationMe, ATTACK_FROM,
                UnitType.INFANTRY);
        Unit inf2 = unitService.buildUnit(nationMe, ATTACK_FROM,
                UnitType.INFANTRY);
        cityService.captureCity(nationThem, CITY);
        declareWar();
        Result<MoveCost> result = moveUnits(
                makeUnitList(inf), CITY);
        assertResult(result);
        assertTookCity(result);
        result = moveUnits(
                makeUnitList(inf2), CITY);
        assertResult(result);
        inf2 = unitDao.findUnit(inf2.getId());
        assertEquals(CITY, inf2.getCoords());
    }

    @Test
    public void planeAndInfTakeCity() {
        Unit plane = unitService.buildUnit(nationMe, ATTACK_FROM,
                UnitType.FIGHTER);
        Unit inf = unitService.buildUnit(nationMe, ATTACK_FROM,
                UnitType.INFANTRY);
        cityService.captureCity(nationThem, CITY);
        declareWar();
        Result<MoveCost> result = moveUnits(
                makeUnitList(plane, inf), CITY);
        assertResult(result);
        assertDamaged(result, inf);
        assertTookCity(result);
        assertEquals(ATTACK_FROM, plane.getCoords());
    }

}
