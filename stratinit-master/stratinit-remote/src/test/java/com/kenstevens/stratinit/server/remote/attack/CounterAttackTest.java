package com.kenstevens.stratinit.server.remote.attack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.kenstevens.stratinit.model.MoveCost;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.remote.ThreePlayerBase;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

public class CounterAttackTest extends ThreePlayerBase {
	private static final SectorCoords SEA32 = new SectorCoords(3, 2);
	private static final SectorCoords SEA34 = new SectorCoords(3, 4);
	private static final SectorCoords SEA42 = new SectorCoords(4, 2);
	private static final SectorCoords SEA52 = new SectorCoords(5, 2);
	private static final SectorCoords SEA51 = new SectorCoords(5, 1);
	private static final SectorCoords SEA62 = new SectorCoords(6, 2);
	private static final SectorCoords SEA61 = new SectorCoords(6, 1);
	private static final SectorCoords SEA31 = new SectorCoords(3, 1);
	private static final SectorCoords SUPPLY = new SectorCoords(4, 1);
	private static final SectorCoords ATT = new SectorCoords (0, 0);
	private static final SectorCoords DEF = new SectorCoords (1, 1);
	@Test
	public void destCounterAttacks() {
		declareWar();
		Unit dest3 = unitDaoService.buildUnit(nationMe, SEA32,
				UnitType.DESTROYER);
		Unit dest4 = unitDaoService.buildUnit(nationThem, SEA42,
				UnitType.DESTROYER);
		Result<MoveCost> result = moveUnits(
				makeUnitList(dest3), SEA42);
		assertResult(result);
		assertDamaged(result, dest3);
		assertFiredOnce(result, dest3);
		assertDamaged(result, dest4);
		assertFiredOnce(result, dest4);
	}
	@Test
	public void dest3OutCounterAttacks() {
		declareWar();
		Unit dest3 = unitDaoService.buildUnit(nationMe, SEA32,
				UnitType.DESTROYER);
		Unit dest4 = unitDaoService.buildUnit(nationThem, SEA42,
				UnitType.DESTROYER);
		Unit dest5 = unitDaoService.buildUnit(nationThem, SEA52,
				UnitType.DESTROYER);
		Result<MoveCost> result = moveUnits(
				makeUnitList(dest3), SEA42);
		assertResult(result);
		assertDamaged(result, dest3);
		assertFiredOnce(result, dest3);
		assertDamaged(result, dest4);
		assertFiredOnce(result, dest4);
		assertNotDamaged(result, dest5);
		assertNotFired(result, dest5);
	}

	@Test
	public void allAdjCounterAttacks() {
		declareWar();
		Unit dest3 = unitDaoService.buildUnit(nationMe, SEA32,
				UnitType.DESTROYER);
		Unit dest4 = unitDaoService.buildUnit(nationThem, SEA42,
				UnitType.DESTROYER);
		Unit dest31 = unitDaoService.buildUnit(nationThem, SEA31,
				UnitType.DESTROYER);
		Result<MoveCost> result = moveUnits(
				makeUnitList(dest3), SEA42);
		assertResult(result);
		assertDamaged(result, dest3);
		assertFiredOnce(result, dest3);
		assertDamaged(result, dest4);
		assertFiredOnce(result, dest4);
		assertNotDamaged(result, dest31);
		assertFiredOnce(result, dest31);
		assertEquals(result.toString(), 3, result.getMessages().size());
	}
	
	@Test
	public void allEnemyAdjCounterAttacks() {
		declareWar();
		warDeclaredByThird();
		Unit dest3 = unitDaoService.buildUnit(nationMe, SEA32,
				UnitType.DESTROYER);
		Unit dest4 = unitDaoService.buildUnit(nationThem, SEA42,
				UnitType.DESTROYER);
		Unit dest31 = unitDaoService.buildUnit(nationThird, SEA31,
				UnitType.DESTROYER);
		Result<MoveCost> result = moveUnits(
				makeUnitList(dest3), SEA42);
		assertResult(result);
		assertDamaged(result, dest3);
		assertFiredOnce(result, dest3);
		assertDamaged(result, dest4);
		assertFiredOnce(result, dest4);
		assertNotDamaged(result, dest31);
		assertFiredOnce(result, dest31);
	}
	
	@Test
	public void moveThenAttOnlyCounterFireOnceNoSupply() {
		declareWar();
		Unit dest3 = unitDaoService.buildUnit(nationMe, SEA32,
				UnitType.DESTROYER);
		Unit dest5 = unitDaoService.buildUnit(nationThem, SEA52,
				UnitType.DESTROYER);
		Unit dest31 = unitDaoService.buildUnit(nationThem, SEA31,
				UnitType.DESTROYER);
		Result<MoveCost> result = moveUnits(
				makeUnitList(dest3), SEA52);
		assertResult(result);
		assertDamaged(result, dest3);
		assertFiredOnce(result, dest3);
		assertNotDamaged(result, dest31);
		assertNotFired(result, dest31); // since out of supply
		assertNotMoved(result, dest31);
		assertNotMoved(result, dest5);
		assertFiredOnce(result, dest5);
		assertDamaged(result, dest5);
	}
	
	@Test
	public void moveThenAttOnlyCounterFireOnceSupply() {
		declareWar();
		Unit dest3 = unitDaoService.buildUnit(nationMe, SEA32,
				UnitType.DESTROYER);
		Unit dest5 = unitDaoService.buildUnit(nationThem, SEA52,
				UnitType.DESTROYER);
		Unit dest31 = unitDaoService.buildUnit(nationThem, SEA31,
				UnitType.DESTROYER);
		Unit supply = unitDaoService.buildUnit(nationThem, SUPPLY,
				UnitType.SUPPLY);
		Result<MoveCost> result = moveUnits(
				makeUnitList(dest3), SEA52);
		assertFalseResult(result);
		assertDamaged(result, dest3);
		assertNotFired(result, dest3);
		assertNotDamaged(result, dest31);
		assertMoved(result, dest31);
		assertMoved(result, dest5);
		assertNotDamaged(result, dest5);
		assertEquals(supply.getUnitBase().getAmmo() - 2, supply.getAmmo());
	}
	
	@Test
	public void nonEscortMovesToInterdict() {
		declareWar();
		Unit dest4 = unitDaoService.buildUnit(nationThem, SEA42,
				UnitType.DESTROYER);
		Unit mdest = unitDaoService.buildUnit(nationMe, SEA62, UnitType.DESTROYER);
		unitDaoService.buildUnit(nationThem, SUPPLY,
				UnitType.SUPPLY);
		Result<MoveCost> result = moveUnits(
				makeUnitList(mdest), SEA61);
		assertFalseResult(result);
		assertMoved(result, dest4);
		assertFalse(dest4.getCoords().equals(SEA42));
		assertNotFired(result, mdest);
		assertDamaged(result, mdest);
	}
	@Test
	public void escortDoesntMoveToInterdict() {
		declareWar();
		Unit bb3 = unitDaoService.buildUnit(nationThem, SEA34,
				UnitType.BATTLESHIP);
		Unit dest4 = unitDaoService.buildUnit(nationThem, SEA42,
				UnitType.DESTROYER);
		Unit mdest = unitDaoService.buildUnit(nationMe, SEA62, UnitType.DESTROYER);
		unitDaoService.buildUnit(nationThem, SUPPLY,
				UnitType.SUPPLY);
		Result<MoveCost> result = moveUnits(
				makeUnitList(mdest), SEA61);
		assertResult(result);
		assertNotMoved(result, dest4);
		assertNotDamaged(result, mdest);
		assertNotFired(result, mdest);
		assertNotFired(result, bb3);
		assertNotFired(result, bb3);
	}
	@Test
	public void escortCruiserDoesMoveToInterdictSub() {
		declareWar();
		Unit bb3 = unitDaoService.buildUnit(nationThem, SEA34,
				UnitType.BATTLESHIP);
		Unit cru4 = unitDaoService.buildUnit(nationThem, SEA42,
				UnitType.CRUISER);
		Unit msub = unitDaoService.buildUnit(nationMe, SEA62, UnitType.SUBMARINE);
		unitDaoService.buildUnit(nationThem, SUPPLY,
				UnitType.SUPPLY);
		Result<MoveCost> result = moveUnits(
				makeUnitList(msub), SEA61);
		assertFalseResult(result);
		assertMoved(result, cru4);
		assertFalse(cru4.getCoords().equals(SEA42));
		assertNotFired(result, msub);
		assertDamaged(result, msub);
		assertNotMoved(result, bb3);
	}
	@Test
	public void nonEscortBBDoesMoveToInterdictDest() {
		declareWar();
		Unit bb3 = unitDaoService.buildUnit(nationThem, SEA34,
				UnitType.BATTLESHIP);
		Unit bb4 = unitDaoService.buildUnit(nationThem, SEA42,
				UnitType.BATTLESHIP);
		Unit mdest = unitDaoService.buildUnit(nationMe, SEA62, UnitType.DESTROYER);
		unitDaoService.buildUnit(nationThem, SUPPLY,
				UnitType.SUPPLY);
		Result<MoveCost> result = moveUnits(
				makeUnitList(mdest), SEA61);
		assertFalseResult(result);
		assertMoved(result, bb4);
		assertFalse(bb4.getCoords().equals(SEA42));
		assertNotFired(result, mdest);
		assertDamaged(result, mdest);
		assertNotMoved(result, bb3);
	}
	@Test
	public void escortDestDoesntMoveToInterdictSub() {
		declareWar();
		Unit bb3 = unitDaoService.buildUnit(nationThem, SEA34,
				UnitType.BATTLESHIP);
		Unit dest4 = unitDaoService.buildUnit(nationThem, SEA42,
				UnitType.DESTROYER);
		Unit msub = unitDaoService.buildUnit(nationMe, SEA62, UnitType.SUBMARINE);
		unitDaoService.buildUnit(nationThem, SUPPLY,
				UnitType.SUPPLY);
		Result<MoveCost> result = moveUnits(
				makeUnitList(msub), SEA61);
		assertResult(result);
		assertNotMoved(result, dest4);
		assertNotDamaged(result, msub);
		assertNotFired(result, msub);
		assertNotMoved(result, bb3);
	}
	@Test
	public void escortInterdictsIfAdjacent() {
		declareWar();
		Unit bb3 = unitDaoService.buildUnit(nationThem, SEA34,
				UnitType.BATTLESHIP);
		Unit dest4 = unitDaoService.buildUnit(nationThem, SEA42,
				UnitType.DESTROYER);
		Unit mdest = unitDaoService.buildUnit(nationMe, SEA52, UnitType.DESTROYER);
		unitDaoService.buildUnit(nationThem, SUPPLY,
				UnitType.SUPPLY);
		Result<MoveCost> result = moveUnits(
				makeUnitList(mdest), SEA51);
		assertFalseResult(result);
		assertMoved(result, dest4);
		assertEquals(dest4.getCoords(), SEA42);
		assertNotFired(result, mdest);
		assertDamaged(result, mdest);
		assertNotFired(result, bb3);
	}
	@Test
	public void noEscortAllies() {
		declareWar();
		themThirdAlly();
		Unit bb3 = unitDaoService.buildUnit(nationThird, SEA34,
				UnitType.BATTLESHIP);
		Unit dest4 = unitDaoService.buildUnit(nationThem, SEA42,
				UnitType.DESTROYER);
		Unit mdest = unitDaoService.buildUnit(nationMe, SEA62, UnitType.DESTROYER);
		unitDaoService.buildUnit(nationThem, SUPPLY,
				UnitType.SUPPLY);
		Result<MoveCost> result = moveUnits(
				makeUnitList(mdest), SEA61);
		assertFalseResult(result);
		assertMoved(result, dest4);
		assertNotFired(result, mdest);
		assertNotFired(result, bb3);
		assertDamaged(result, mdest);
	}
	
	@Test
	public void infNoCounterAttackZep() {
		declareWar();
		Unit zep = unitDaoService.buildUnit(nationMe, ATT,
				UnitType.ZEPPELIN);
		Unit inf = unitDaoService.buildUnit(nationThem, DEF,
				UnitType.INFANTRY);
		Result<MoveCost> result = moveUnits(
				makeUnitList(zep), DEF);
		assertResult(result);
		assertNotDamaged(result, zep);
		assertFiredOnce(result, zep);
		assertDamaged(result, inf);
		assertNotFired(result, inf);
	}

	
	@Test
	public void tankNoCounterAttackZep() {
		declareWar();
		Unit zep = unitDaoService.buildUnit(nationMe, ATT,
				UnitType.ZEPPELIN);
		Unit tank = unitDaoService.buildUnit(nationThem, DEF,
				UnitType.TANK);
		Result<MoveCost> result = moveUnits(
				makeUnitList(zep), DEF);
		assertResult(result);
		assertNotDamaged(result, zep);
		assertFiredOnce(result, zep);
		assertDamaged(result, tank);
		assertNotFired(result, tank);
	}
	
	@Test
	public void patrolNoCounterAttackZep() {
		declareWar();
		Unit zep = unitDaoService.buildUnit(nationMe, ATT,
				UnitType.ZEPPELIN);
		Unit patrol = unitDaoService.buildUnit(nationThem, DEF,
				UnitType.PATROL);
		Result<MoveCost> result = moveUnits(
				makeUnitList(zep), DEF);
		assertResult(result);
		assertNotDamaged(result, zep);
		assertFiredOnce(result, zep);
		assertDamaged(result, patrol);
		assertNotFired(result, patrol);
	}
	
	@Test
	public void destNoCounterAttackZep() {
		declareWar();
		Unit zep = unitDaoService.buildUnit(nationMe, ATT,
				UnitType.ZEPPELIN);
		Unit dest = unitDaoService.buildUnit(nationThem, DEF,
				UnitType.DESTROYER);
		Result<MoveCost> result = moveUnits(
				makeUnitList(zep), DEF);
		assertResult(result);
		assertNotDamaged(result, zep);
		assertFiredOnce(result, zep);
		assertDamaged(result, dest);
		assertNotFired(result, dest);
	}
	
	@Test
	public void flakHitsZep() {
		declareWar();
		Unit zep = unitDaoService.buildUnit(nationMe, ATT,
				UnitType.ZEPPELIN);
		Unit bat = unitDaoService.buildUnit(nationThem, DEF,
				UnitType.BATTLESHIP);
		Result<MoveCost> result = moveUnits(
				makeUnitList(zep), DEF);
		assertDamaged(result, zep);
		if (zep.isAlive()) {
			assertFiredOnce(result, zep);
			assertDamaged(result, bat);
		}
		assertNotFired(result, bat);
	}

}
