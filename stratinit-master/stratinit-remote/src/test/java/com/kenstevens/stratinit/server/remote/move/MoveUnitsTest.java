package com.kenstevens.stratinit.server.remote.move;

import com.kenstevens.stratinit.dto.SISector;
import com.kenstevens.stratinit.dto.SIUnit;
import com.kenstevens.stratinit.dto.SIUpdate;
import com.kenstevens.stratinit.model.MoveCost;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.remote.WithUnitsBase;
import com.kenstevens.stratinit.type.MoveType;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class MoveUnitsTest extends WithUnitsBase {
	public static final SectorCoords PORT = new SectorCoords(2,2);
	public static final SectorCoords BY_PORT = new SectorCoords(1,2);

	@Test
	public void unitInsufMob() {
		List<SIUnit> units = makeUnitList(testInfantry);
		Result<MoveCost> result = moveUnits(units, new SectorCoords(5,5));
		assertFalseResult(result);
	}

	@Test
	public void unitInsufFuel() {
		List<SIUnit> units = makeUnitList(testHelicopter);
		testHelicopter.setFuel(5);
		Result<MoveCost> result = moveUnits(units, new SectorCoords(5,5));
		assertFalseResult(result);
	}

	@Test
	public void unitOneFuel() {
		Unit fighter = unitDaoService.buildUnit(nationMe, BY_PORT, UnitType.FIGHTER);
		fighter.setFuel(1);
		fighter.setMobility(1);
		sectorDaoService.captureCity(nationMe, PORT);
		Result<MoveCost> result = moveUnits(makeUnitList(fighter), PORT);
		assertResult(result);
	}

	@Test
	public void empty() {
		List<SIUnit> units = new ArrayList<SIUnit>();
		Result<MoveCost> result = moveUnits(units, new SectorCoords(0,10));
		assertFalseResult(result);
	}

	@Test
	public void canMove() {
        List<SIUnit> units = makeUnitList(testInfantry);
        SectorCoords target = new SectorCoords(0, 2);
        Result<SIUpdate> result = stratInit.moveUnits(units, target);
        assertResult(result);
        assertCoords(result, testInfantryId, target);
        List<SISector> sisectors = result.getValue().sectors;
        for (SISector sisector : sisectors) {
            if (sisector.coords.equals(target)) {
                assertEquals(testInfantry.getType(), sisector.topUnitType);
                assertEquals(testInfantry.getId().intValue(), sisector.topUnitId);
            }
        }
    }

	// 20 Dec 2.4 s
	@Test
	public void profileTest() {
		List<Unit> units = new ArrayList<Unit>();
		for (int i = 0; i < 50; ++i) {
			Unit unit = unitDaoServiceImpl.buildUnit(nationMe, START_COORDS, UnitType.INFANTRY);
			units.add(unit);
		}

		List<SIUnit> siunits = makeUnitList(units);
		SectorCoords target = new SectorCoords(0,2);
//		ProfilerAspect.enabled = true;
		Result<MoveCost> result = moveUnits(siunits, target);
		assertResult(result);
	}

	@Test
	public void landWater() {
		List<SIUnit> units = makeUnitList(testInfantry);
		SectorCoords target = new SectorCoords(4,0);
		Result<MoveCost> result = moveUnits(units, target);
		assertFalseResult(result);
	}

	@Test
	public void landEmptyTransport() {
        List<SIUnit> units = makeUnitList(testInfantry);
        Result<SIUpdate> result = stratInit.moveUnits(units, SEA1);
        assertResult(result);
        assertCoords(result, testInfantryId, SEA1);
    }

	@Test
	public void landNeutralCity() {
		List<SIUnit> units = makeUnitList(testInfantry);
		Result<MoveCost> result = moveUnits(units, PORT);
		assertResult(result);
		assertDamaged(result, testInfantry);
		assertEquals(MoveType.TAKE_CITY, result.getValue().getMoveType());
	}

	@Test
	public void landFullTransport() {
		List<SIUnit> units = makeUnitList(testInfantry);
		Result<MoveCost> result = moveUnits(units, SEA2);
		assertResult(result);
		assertFalse(testInfantry.getCoords().equals(SEA2));
		assertEquals(MoveType.MOVE, result.getValue().getMoveType());
	}

	@Test
	public void landFullTransport2() {
		List<Unit> UNITS = new ArrayList<Unit>();
		for (int i = 0; i < 7; ++i) {
			Unit unit = unitDaoServiceImpl.buildUnit(nationMe, START_COORDS, UnitType.INFANTRY);
			unit.addMobility();
			unit.addMobility();
			UNITS.add(unit);
		}
		List<SIUnit> unitGroup = makeUnitList(UNITS);
		Result<MoveCost> result = moveUnits(unitGroup, SEA1);
		assertResult(result);
		Unit first = unitDao.findUnit(UNITS.get(0).getId());
		Unit last = unitDao.findUnit(UNITS.get(6).getId());
		assertEquals(SEA1, first.getCoords());
		assertFalse(SEA1.equals(last.getCoords()));
	}

	@Test
	public void landTransportSixInfAddTank() {
		List<Unit> UNITS = new ArrayList<Unit>();
		for (int i = 0; i < 6; ++i) {
			Unit unit = unitDaoServiceImpl.buildUnit(nationMe, START_COORDS, UnitType.INFANTRY);
			unit.addMobility();
			unit.addMobility();
			UNITS.add(unit);
		}
		Result<MoveCost> result = moveUnits(makeUnitList(UNITS), SEA1);
        assertResult(result);
        Unit first = unitDao.findUnit(UNITS.get(0).getId());
        Unit last = unitDao.findUnit(UNITS.get(4).getId());
        assertEquals(SEA1, first.getCoords());
        assertEquals(SEA1, last.getCoords());
        Unit tank = unitDaoServiceImpl.buildUnit(nationMe, START_COORDS, UnitType.TANK);
        tank.addMobility();
        tank.addMobility();
        result = moveUnits(makeUnitList(tank), SEA1);
        assertResult(result);
        assertFalse(SEA1.equals(tank.getCoords()));
    }

    private void assertCoords(Result<SIUpdate> result, int unitId, SectorCoords target) {
        List<SIUnit> units = result.getValue().units;
        for (SIUnit unit : units) {
            if (unit.id == unitId) {
                assertEquals(target, unit.coords);
            }
        }
    }

    @Test
    public void transportNoBlocksInf() {
        Unit[] inf = new Unit[TRANSPORT_CAPACITY + 1];
        for (int i = 0; i < TRANSPORT_CAPACITY + 1; ++i) {
            inf[i] = unitDaoService.buildUnit(nationMe, BY_PORT, UnitType.TANK);
        }
        sectorDaoService.captureCity(nationMe, PORT);
        unitDaoService.buildUnit(nationMe, PORT, UnitType.TRANSPORT);
        Result<MoveCost> result = moveUnits(makeUnitList(inf), PORT);
        assertResult(result);
        for (int i = 0; i < TRANSPORT_CAPACITY + 1; ++i) {
            assertEquals(inf[i].getCoords(), PORT, result.toString());
        }
        assertEquals(inf[TRANSPORT_CAPACITY].getCoords(), PORT, result.toString());
    }

    @Test
    public void navyMyCity() {
        Unit dest = unitDaoService.buildUnit(nationMe, BY_PORT, UnitType.DESTROYER);
		List<SIUnit> units = makeUnitList(dest);
		sectorDaoService.captureCity(nationMe, PORT);
		Result<MoveCost> result = moveUnits(units, PORT);
		assertResult(result);
		assertEquals(dest.getCoords(), PORT, result.toString());
		assertEquals(MoveType.MOVE, result.getValue().getMoveType());
	}

	@Test
	public void navyMyInlandCity() {
		sectorDaoService.captureCity(nationMe, PORT);
		Unit dest = unitDaoService.buildUnit(nationMe, PORT, UnitType.DESTROYER);
		sectorDaoService.captureCity(nationMe, BY_PORT);
		Result<MoveCost> result = moveUnits(makeUnitList(dest), BY_PORT);
		assertFalseResult(result);
		assertEquals(dest.getCoords(), PORT, result.toString());
	}


}
