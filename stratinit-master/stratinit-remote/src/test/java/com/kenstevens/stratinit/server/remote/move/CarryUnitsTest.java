package com.kenstevens.stratinit.server.remote.move;

import com.kenstevens.stratinit.model.MoveCost;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.remote.StratInitWebBase;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class CarryUnitsTest extends StratInitWebBase {
    @Autowired
    protected SectorDaoService sectorDaoServiceImpl;
    private static final SectorCoords PORT = new SectorCoords(2, 2);
    private static final SectorCoords BY_PORT = new SectorCoords(1, 2);
    protected static final SectorCoords SEA1 = new SectorCoords(4, 0);
    protected static final SectorCoords SEA2 = new SectorCoords(4, 1);
    protected static final SectorCoords BETWEEN = new SectorCoords(1, 1);
    protected static final SectorCoords TARGET = new SectorCoords(0, 0);

    @BeforeEach
    public void doJoinGame() {
        joinGamePlayerMe();
    }

    // TODO assertEquals arguments are backwards

    @Test
    public void carrierDragsMostFighters() {
        Unit[] fighters = new Unit[CARRIER_CAPACITY + 1];
        for (int i = 0; i < CARRIER_CAPACITY + 1; ++i) {
            fighters[i] = unitDaoService.buildUnit(nationMe, SEA1,
                    UnitType.FIGHTER);
        }
        Unit carrier = unitDaoService.buildUnit(nationMe, SEA1,
                UnitType.CARRIER);
        Result<MoveCost> result = moveUnits(makeUnitList(carrier), SEA2);
        assertResult(result);
        assertEquals(carrier.getCoords(), SEA2, result.toString());
        for (int i = 0; i < CARRIER_CAPACITY; ++i) {
            assertEquals(fighters[i].getCoords(), SEA2, result.toString());
        }
        assertEquals(fighters[CARRIER_CAPACITY].getCoords(),
                SEA1, result.toString());
    }

	@Test
	public void carrierNoDragsTank() {
        Unit[] tank = new Unit[TRANSPORT_CAPACITY + 1];
        for (int i = 0; i < TRANSPORT_CAPACITY + 1; ++i) {
            tank[i] = unitDaoService.buildUnit(nationMe, PORT, UnitType.TANK);
        }
        sectorDaoServiceImpl.captureCity(nationMe, PORT);
        Unit carrier = unitDaoService.buildUnit(nationMe, PORT,
                UnitType.CARRIER);
        Result<MoveCost> result = moveUnits(makeUnitList(carrier), SEA1);
        assertResult(result);
        assertEquals(carrier.getCoords(), SEA1, result.toString());
        for (int i = 0; i < TRANSPORT_CAPACITY; ++i) {
            assertEquals(tank[i].getCoords(), PORT, result.toString());
        }
        assertEquals(tank[TRANSPORT_CAPACITY].getCoords(),
                PORT, result.toString());
    }

	@Test
	public void transportDragsMostInf() {
        Unit[] inf = new Unit[TRANSPORT_CAPACITY + 1];
        for (int i = 0; i < TRANSPORT_CAPACITY + 1; ++i) {
            inf[i] = unitDaoService
                    .buildUnit(nationMe, PORT, UnitType.INFANTRY);
        }
        sectorDaoServiceImpl.captureCity(nationMe, PORT);
        Unit transport = unitDaoService.buildUnit(nationMe, PORT,
                UnitType.TRANSPORT);
        Result<MoveCost> result = moveUnits(makeUnitList(transport), SEA1);
        assertResult(result);
        assertEquals(transport.getCoords(), SEA1, result.toString());
        for (int i = 0; i < TRANSPORT_CAPACITY; ++i) {
            assertEquals(inf[i].getCoords(), SEA1, result.toString());
        }
        assertEquals(inf[TRANSPORT_CAPACITY].getCoords(),
                PORT, result.toString());
    }

	@Test
	public void helicopterDragsSomeInf() {
        Unit[] inf = new Unit[HELICOPTER_CAPACITY + 1];
        for (int i = 0; i < HELICOPTER_CAPACITY + 1; ++i) {
            inf[i] = unitDaoService
                    .buildUnit(nationMe, PORT, UnitType.INFANTRY);
        }
        sectorDaoServiceImpl.captureCity(nationMe, PORT);
        Unit heli = unitDaoService.buildUnit(nationMe, PORT,
                UnitType.HELICOPTER);
        Result<MoveCost> result = moveUnits(makeUnitList(heli), SEA1);
        assertResult(result);
        assertEquals(heli.getCoords(), SEA1, result.toString());
        for (int i = 0; i < HELICOPTER_CAPACITY; ++i) {
            assertEquals(inf[i].getCoords(), SEA1, result.toString());
        }
        assertEquals(inf[HELICOPTER_CAPACITY].getCoords(),
                PORT, result.toString());
    }

	@Test
	public void twoHeliDragsTwiceInf() {
		Unit[] inf = new Unit[2 * HELICOPTER_CAPACITY + 1];
		for (int i = 0; i < 2 * HELICOPTER_CAPACITY + 1; ++i) {
            inf[i] = unitDaoService
                    .buildUnit(nationMe, PORT, UnitType.INFANTRY);
        }
        sectorDaoServiceImpl.captureCity(nationMe, PORT);
        Unit heli1 = unitDaoService.buildUnit(nationMe, PORT,
                UnitType.HELICOPTER);
        Unit heli2 = unitDaoService.buildUnit(nationMe, PORT,
                UnitType.HELICOPTER);
        Result<MoveCost> result = moveUnits(makeUnitList(heli1, heli2), SEA1);
        assertResult(result);
        assertEquals(heli1.getCoords(), SEA1, result.toString());
        assertEquals(heli2.getCoords(), SEA1, result.toString());
        for (int i = 0; i < HELICOPTER_CAPACITY * 2; ++i) {
            assertEquals(inf[i].getCoords(), SEA1, result.toString());
        }
        assertEquals(
                inf[2 * HELICOPTER_CAPACITY].getCoords(), PORT, result.toString());
    }

	@Test
	public void twoHeliinsufMobDragsSomeInf() {
		Unit[] inf = new Unit[2 * HELICOPTER_CAPACITY + 1];
		for (int i = 0; i < 2 * HELICOPTER_CAPACITY + 1; ++i) {
			inf[i] = unitDaoService
                    .buildUnit(nationMe, PORT, UnitType.INFANTRY);
        }
        sectorDaoServiceImpl.captureCity(nationMe, PORT);
        Unit heli1 = unitDaoService.buildUnit(nationMe, PORT,
                UnitType.HELICOPTER);
        heli1.setMobility(1);
        Unit heli2 = unitDaoService.buildUnit(nationMe, PORT,
                UnitType.HELICOPTER);
        Result<MoveCost> result = moveUnits(makeUnitList(heli1, heli2), SEA1);
        assertResult(result);
        assertFalse(heli1.getCoords().equals(SEA1), result.toString());
        assertEquals(heli2.getCoords(), SEA1, result.toString());
        int atDest = 0;
        for (int i = 0; i < HELICOPTER_CAPACITY * 2; ++i) {
            if (inf[i].getCoords().equals(SEA1)) {
                ++atDest;
            } else {
                assertEquals(inf[i].getCoords(), heli1.getCoords());
            }
        }
        assertEquals(HELICOPTER_CAPACITY, atDest);
    }

	@Test
	public void helicopterDragsInfFromCity() {
        Unit inf = unitDaoService.buildUnit(nationMe, PORT, UnitType.INFANTRY);
        sectorDaoServiceImpl.captureCity(nationMe, PORT);
        Unit heli = unitDaoService.buildUnit(nationMe, PORT,
                UnitType.HELICOPTER);
        Result<MoveCost> result = moveUnits(makeUnitList(heli), SEA1);
        assertResult(result);
        assertEquals(heli.getCoords(), SEA1, result.toString());
        assertEquals(inf.getCoords(), SEA1, result.toString());
    }

	@Test
	public void helicopterNoDragsHurtInfFromCity() {
        Unit inf = unitDaoService.buildUnit(nationMe, PORT, UnitType.INFANTRY);
        inf.damage(4);
        unitDao.save(inf);
        sectorDaoServiceImpl.captureCity(nationMe, PORT);
        Unit heli = unitDaoService.buildUnit(nationMe, PORT,
                UnitType.HELICOPTER);
        Result<MoveCost> result = moveUnits(makeUnitList(heli), SEA1);
        assertResult(result);
        assertEquals(heli.getCoords(), SEA1, result.toString());
        assertFalse(inf.getCoords().equals(SEA1), result.toString());
        assertEquals(inf.getCoords(), PORT, result.toString());
    }

	@Test
	public void helicopterNoDragsNoMobInfFromCity() {
        Unit inf = unitDaoService.buildUnit(nationMe, PORT, UnitType.INFANTRY);
        inf.decreaseMobility(inf.getMobility());
        unitDao.save(inf);
        sectorDaoServiceImpl.captureCity(nationMe, PORT);
        Unit heli = unitDaoService.buildUnit(nationMe, PORT,
                UnitType.HELICOPTER);
        Result<MoveCost> result = moveUnits(makeUnitList(heli), SEA1);
        assertResult(result);
        assertEquals(heli.getCoords(), SEA1, result.toString());
        assertFalse(inf.getCoords().equals(SEA1), result.toString());
        assertEquals(inf.getCoords(), PORT, result.toString());
    }

	@Test
	public void helicopterDragsNoFighters() {
        Unit[] fighter = new Unit[HELICOPTER_CAPACITY + 1];
        for (int i = 0; i < HELICOPTER_CAPACITY + 1; ++i) {
            fighter[i] = unitDaoService.buildUnit(nationMe, PORT,
                    UnitType.FIGHTER);
        }
        sectorDaoServiceImpl.captureCity(nationMe, PORT);
        Unit heli = unitDaoService.buildUnit(nationMe, PORT,
                UnitType.HELICOPTER);
        Result<MoveCost> result = moveUnits(makeUnitList(heli), SEA1);
        assertResult(result);
        assertEquals(heli.getCoords(), SEA1, result.toString());
        for (int i = 0; i < HELICOPTER_CAPACITY; ++i) {
            assertEquals(fighter[i].getCoords(), PORT, result.toString());
        }
        assertEquals(fighter[HELICOPTER_CAPACITY].getCoords(),
                PORT, result.toString());
    }

	@Test
	public void engDragsSomeMissile() {
        Unit[] missile = new Unit[ENGINEER_CAPACITY + 1];
        for (int i = 0; i < ENGINEER_CAPACITY + 1; ++i) {
            missile[i] = unitDaoService.buildUnit(nationMe, PORT,
                    UnitType.ICBM_1);
        }
        sectorDaoServiceImpl.captureCity(nationMe, PORT);
        Unit eng = unitDaoService.buildUnit(nationMe, PORT, UnitType.ENGINEER);
        Result<MoveCost> result = moveUnits(makeUnitList(eng), BY_PORT);
        assertResult(result);
        assertEquals(eng.getCoords(), BY_PORT, result.toString());
        for (int i = 0; i < ENGINEER_CAPACITY; ++i) {
            assertEquals(missile[i].getCoords(), BY_PORT, result.toString());
        }
        assertEquals(missile[ENGINEER_CAPACITY].getCoords(), PORT,
                result.toString());
    }

	@Test
	public void subDragsSomeMissile() {
        Unit[] missile = new Unit[SUB_CAPACITY + 1];
        for (int i = 0; i < SUB_CAPACITY + 1; ++i) {
            missile[i] = unitDaoService.buildUnit(nationMe, PORT,
                    UnitType.ICBM_1);
        }
        sectorDaoServiceImpl.captureCity(nationMe, PORT);
        Unit sub = unitDaoService.buildUnit(nationMe, PORT, UnitType.SUBMARINE);
        Result<MoveCost> result = moveUnits(makeUnitList(sub), SEA1);
        assertResult(result);
        assertEquals(sub.getCoords(), SEA1, result.toString());
        for (int i = 0; i < SUB_CAPACITY; ++i) {
            assertEquals(missile[i].getCoords(), SEA1, result.toString());
        }
        assertEquals(missile[SUB_CAPACITY].getCoords(), PORT, result.toString());
    }

	@Test
	public void cargoDragsSomeInf() {
        Unit cargo = unitDaoService.buildUnit(nationMe, PORT,
                UnitType.CARGO_PLANE);
        Unit[] inf = new Unit[CARGO_CAPACITY + 1];
        for (int i = 0; i < CARGO_CAPACITY + 1; ++i) {
            inf[i] = unitDaoService
                    .buildUnit(nationMe, PORT, UnitType.INFANTRY);
        }
        sectorDaoServiceImpl.captureCity(nationMe, PORT);
        Result<MoveCost> result = moveUnits(makeUnitList(cargo), TARGET);
        assertResult(result);
        assertEquals(cargo.getCoords(), TARGET, result.toString());
        for (int i = 0; i < CARGO_CAPACITY; ++i) {
            assertEquals(TARGET, inf[i].getCoords(),
                    i + ": " + result.toString());
        }
        assertEquals(PORT, inf[CARGO_CAPACITY].getCoords(), result.toString());
    }

	@Test
	public void cargoNoCarriesTank() {
        Unit cargo = unitDaoService.buildUnit(nationMe, PORT,
                UnitType.CARGO_PLANE);
        Unit tank = unitDaoService.buildUnit(nationMe, PORT, UnitType.TANK);
        sectorDaoServiceImpl.captureCity(nationMe, PORT);
        Result<MoveCost> result = moveUnits(makeUnitList(cargo), TARGET);
        assertResult(result);
        assertEquals(cargo.getCoords(), TARGET, result.toString());
        assertEquals(PORT, tank.getCoords(), result.toString());
    }

	@Test
	public void cargoNoCarriesEng() {
        Unit cargo = unitDaoService.buildUnit(nationMe, PORT,
                UnitType.CARGO_PLANE);
        Unit eng = unitDaoService.buildUnit(nationMe, PORT, UnitType.ENGINEER);
        sectorDaoServiceImpl.captureCity(nationMe, PORT);
        Result<MoveCost> result = moveUnits(makeUnitList(cargo), TARGET);
        assertResult(result);
        assertEquals(cargo.getCoords(), TARGET, result.toString());
        assertEquals(PORT, eng.getCoords(), result.toString());
    }

	@Test
	public void cargoNoScoop() {
        Unit cargo = unitDaoService.buildUnit(nationMe, PORT,
                UnitType.CARGO_PLANE);
        Unit infPort = unitDaoService.buildUnit(nationMe, PORT,
                UnitType.INFANTRY);
        Unit infMiddle = unitDaoService.buildUnit(nationMe, BETWEEN,
                UnitType.INFANTRY);
        sectorDaoServiceImpl.captureCity(nationMe, PORT);
        Result<MoveCost> result = moveUnits(makeUnitList(cargo), TARGET);
        assertResult(result);
        assertEquals(TARGET, cargo.getCoords(), result.toString());
        assertEquals(TARGET, infPort.getCoords(), result.toString());
        assertEquals(BETWEEN, infMiddle.getCoords(), result.toString());
    }
}
