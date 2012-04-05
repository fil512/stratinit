package com.kenstevens.stratinit.server.remote.attack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.kenstevens.stratinit.model.MoveCost;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.remote.TwoPlayerBase;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;

public class InterceptionTest extends TwoPlayerBase {
	@Autowired
	protected SectorDaoService sectorDaoServiceImpl;
	private static final SectorCoords DEF = new SectorCoords(7, 0);
	private static final SectorCoords BETWEEN = new SectorCoords(8, 0);
	private static final SectorCoords ATT = new SectorCoords(9, 0);
	private static final SectorCoords ATT2 = new SectorCoords(10, 0);
	private static final SectorCoords CITY = new SectorCoords(8, 4);
	private static final SectorCoords BYCITY = new SectorCoords(7, 4);

	@Test
	public void neutralIntercepts() {
		Unit[] fighters = new Unit[5];
		for (int i = 0; i < 5; ++i) {
			fighters[i] = unitDaoService.buildUnit(nationThem, CITY,
					UnitType.FIGHTER);
		}
		Unit bomber = unitDaoService.buildUnit(nationMe, ATT,
				UnitType.HEAVY_BOMBER);
		unitDaoService.buildUnit(nationThem, DEF, UnitType.INFANTRY);

		Result<MoveCost> result = moveUnits(makeUnitList(bomber),
				BETWEEN);
		assertFalseResult(result);
		Unit fighter1 = fighters[0];
		Unit fighter2 = fighters[1];
		assertMoved(result, fighter1);
		assertNotDamaged(result, fighter1);
		assertNotMoved(result, fighter2);
		assertNotDamaged(result, fighter2);
	}

	@Test
	public void fighterInterceptFighterDamages() {
		Unit fighter = unitDaoService.buildUnit(nationThem, CITY,
				UnitType.FIGHTER);
		Unit myFighter = unitDaoService.buildUnit(nationMe, ATT, UnitType.FIGHTER);
		unitDaoService.buildUnit(nationThem, DEF, UnitType.INFANTRY);

		Result<MoveCost> result = moveUnits(makeUnitList(myFighter),
				BETWEEN);
		assertFalseResult(result);
		assertMoved(result, fighter);
		assertDamaged(result, fighter);
		assertFiredOnce(result, myFighter);
		assertFiredOnce(result, fighter);
	}


	@Test
	public void fighterInterceptNavalBomberDamages() {
		Unit fighter = unitDaoService.buildUnit(nationThem, CITY,
				UnitType.FIGHTER);
		Unit bomber = unitDaoService.buildUnit(nationMe, ATT,
				UnitType.NAVAL_BOMBER);
		unitDaoService.buildUnit(nationThem, DEF, UnitType.INFANTRY);

		Result<MoveCost> result = moveUnits(makeUnitList(bomber),
				BETWEEN);
		assertFalseResult(result);
		assertMoved(result, fighter);
		assertDamaged(result, fighter);
		assertFiredOnce(result, fighter);
		assertFiredOnce(result, bomber);
	}

	@Test
	public void fighterInterceptsLandUndamaged() {
		Unit fighter = unitDaoService.buildUnit(nationThem, CITY,
				UnitType.FIGHTER);
		Unit inf = unitDaoService.buildUnit(nationMe, ATT, UnitType.INFANTRY);
		unitDaoService.buildUnit(nationThem, DEF, UnitType.INFANTRY);

		Result<MoveCost> result = moveUnits(makeUnitList(inf),
				BETWEEN);
		assertFalseResult(result);
		assertMoved(result, fighter);
		assertNotDamaged(result, fighter);
	}

	@Test
	public void twoFighterInterceptsLandUndamaged() {
		Unit fighter = unitDaoService.buildUnit(nationThem, CITY,
				UnitType.FIGHTER);
		Unit fighter2 = unitDaoService.buildUnit(nationThem, BYCITY,
				UnitType.FIGHTER);
		Unit inf = unitDaoService.buildUnit(nationMe, ATT, UnitType.INFANTRY);
		Unit inf2 = unitDaoService.buildUnit(nationMe, ATT, UnitType.INFANTRY);
		unitDaoService.buildUnit(nationThem, DEF, UnitType.INFANTRY);

		Result<MoveCost> result = moveUnits(makeUnitList(inf, inf2),
				BETWEEN);
		assertFalseResult(result);
		assertMoved(result, fighter);
		assertMoved(result, fighter2);
		assertNotDamaged(result, fighter);
		assertDamaged(result, inf);
	}

	@Test
	public void landAttackFighterCounterAttack() {
		declareWar();
		Unit fighter = unitDaoService.buildUnit(nationThem, BETWEEN,
				UnitType.FIGHTER);
		Unit inf = unitDaoService.buildUnit(nationMe, ATT, UnitType.INFANTRY);

		Result<MoveCost> result = moveUnits(makeUnitList(inf),
				BETWEEN);
		assertResult(result);
		assertMoved(result, inf);
		assertNotMoved(result, fighter);
		assertDamaged(result, fighter);
		assertDamaged(result, inf);
	}

	@Test
	public void fighterAttackLandCounterAttack() {
		declareWar();
		Unit inf = unitDaoService.buildUnit(nationThem, BETWEEN,
				UnitType.INFANTRY);
		Unit fighter = unitDaoService.buildUnit(nationMe, ATT, UnitType.FIGHTER);

		Result<MoveCost> result = moveUnits(makeUnitList(fighter),
				BETWEEN);
		assertResult(result);
		assertNotMoved(result, inf);
		assertMoved(result, fighter);
		assertDamaged(result, inf);
		assertDamaged(result, fighter);
	}

	@Test
	public void fighterInCityLosesAmmoAndFuel() {
		Unit fighter = unitDaoService.buildUnit(nationThem, CITY,
				UnitType.FIGHTER);
		Unit bomber = unitDaoService.buildUnit(nationMe, ATT,
				UnitType.HEAVY_BOMBER);
		unitDaoService.buildUnit(nationThem, DEF, UnitType.INFANTRY);

		Result<MoveCost> result = moveUnits(makeUnitList(bomber),
				BETWEEN);
		assertFalseResult(result);
		assertFired(result, fighter);
		assertMoved(result, fighter);
	}

	@Test
	public void fighterInAirportResuppliesAmmoAndFuel() {
		setBuild(CITY, UnitType.FIGHTER);
		Unit fighter = unitDaoService.buildUnit(nationThem, CITY,
				UnitType.FIGHTER);
		Unit bomber = unitDaoService.buildUnit(nationMe, ATT,
				UnitType.HEAVY_BOMBER);
		unitDaoService.buildUnit(nationThem, DEF, UnitType.INFANTRY);

		Result<MoveCost> result = moveUnits(makeUnitList(bomber),
				BETWEEN);
		assertFalseResult(result);
		assertNotFired(result, fighter);
		assertFalse(result.toString(),
				fighter.getUnitBase().getMobility() > fighter.getFuel());
	}

	@Test
	public void fighterInterceptsHeliNotInf() {
		unitDaoService.buildUnit(nationThem, CITY, UnitType.FIGHTER);
		Unit heli = unitDaoService
				.buildUnit(nationMe, ATT, UnitType.HELICOPTER);
		Unit inf = unitDaoService.buildUnit(nationMe, ATT, UnitType.INFANTRY);
		unitDaoService.buildUnit(nationThem, DEF, UnitType.INFANTRY);

		Result<MoveCost> result = moveUnits(makeUnitList(heli),
				BETWEEN);
		assertFalseResult(result);
		assertNotDamaged(result, inf);
		assertDamaged(result, heli);
	}

	@Test
	public void fighterInterceptsHeliKillsBoth() {
		unitDaoService.buildUnit(nationThem, CITY, UnitType.FIGHTER);
		Unit heli = unitDaoService
				.buildUnit(nationMe, ATT, UnitType.HELICOPTER);
		heli.setHp(1);
		Unit inf = unitDaoService.buildUnit(nationMe, ATT, UnitType.INFANTRY);
		unitDaoService.buildUnit(nationThem, DEF, UnitType.INFANTRY);

		Result<MoveCost> result = moveUnits(makeUnitList(heli),
				BETWEEN);
		assertFalseResult(result);
		assertFalse(heli.isAlive());
		assertFalse(inf.isAlive());
	}


	@Test
	public void fighterIntercepts2HeliKillsOne() {
		unitDaoService.buildUnit(nationThem, CITY, UnitType.FIGHTER);
		Unit heli1 = unitDaoService
				.buildUnit(nationMe, ATT2, UnitType.HELICOPTER);
		Unit heli2 = unitDaoService
				.buildUnit(nationMe, ATT2, UnitType.HELICOPTER);
		heli1.setHp(1);
		Unit[] inf = new Unit[2*HELICOPTER_CAPACITY + 1];
		for (int i = 0; i < 2*HELICOPTER_CAPACITY + 1; ++i) {
			inf[i] = unitDaoService
					.buildUnit(nationMe, ATT2, UnitType.INFANTRY);
		}
		unitDaoService.buildUnit(nationThem, DEF, UnitType.INFANTRY);

		Result<MoveCost> result = moveUnits(makeUnitList(heli1, heli2),
				BETWEEN);
		assertFalseResult(result);
		assertFalse(result.toString(), heli1.isAlive());
		assertTrue(heli2.isAlive());
		int deadCount = 0;
		for (int i = 0; i < 2*HELICOPTER_CAPACITY; ++i) {
			if (inf[i].isAlive()) {
				assertEquals(heli2.getCoords(), inf[i].getCoords());
			} else {
				++deadCount;
			}
		}
		assertEquals(ATT2, inf[2*HELICOPTER_CAPACITY].getCoords());
		assertEquals(HELICOPTER_CAPACITY, deadCount);
	}

	

	
	@Test
	public void oneFighterInterceptsOneBomber() {
		declareWar();
		Unit[] fighters = new Unit[5];
		for (int i = 0; i < 5; ++i) {
			fighters[i] = unitDaoService.buildUnit(nationThem, CITY,
					UnitType.FIGHTER);
		}
		Unit bomber = unitDaoService.buildUnit(nationMe, ATT,
				UnitType.HEAVY_BOMBER);
		unitDaoService.buildUnit(nationThem, DEF, UnitType.INFANTRY);
		Result<MoveCost> result = moveUnits(makeUnitList(bomber), DEF);
		assertFalseResult(result);
		Unit fighter1 = fighters[0];
		Unit fighter2 = fighters[1];
		assertMoved(result, fighter1);
		assertNotMoved(result, fighter2);
	}

	@Test
	public void twoFightersInterceptsTwoBombers() {
		declareWar();
		Unit[] fighters = new Unit[5];
		for (int i = 0; i < 5; ++i) {
			fighters[i] = unitDaoService.buildUnit(nationThem, CITY,
					UnitType.FIGHTER);
		}
		Unit bomber1 = unitDaoService.buildUnit(nationMe, ATT,
				UnitType.HEAVY_BOMBER);
		Unit bomber2 = unitDaoService.buildUnit(nationMe, ATT,
				UnitType.HEAVY_BOMBER);
		unitDaoService.buildUnit(nationThem, DEF, UnitType.INFANTRY);
		Result<MoveCost> result = moveUnits(makeUnitList(new Unit[] {
				bomber1, bomber2 }), DEF);
		assertFalseResult(result);
		Unit fighter1 = fighters[0];
		Unit fighter2 = fighters[1];
		Unit fighter3 = fighters[3];
		assertMoved(result, fighter1);
		assertMoved(result, fighter2);
		assertNotMoved(result, fighter3);
	}
}
