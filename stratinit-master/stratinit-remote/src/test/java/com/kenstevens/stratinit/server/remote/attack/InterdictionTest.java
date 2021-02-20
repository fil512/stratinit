package com.kenstevens.stratinit.server.remote.attack;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.model.*;
import com.kenstevens.stratinit.remote.Result;
import com.kenstevens.stratinit.server.daoservice.SectorDaoService;
import com.kenstevens.stratinit.server.remote.TwoPlayerBase;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InterdictionTest extends TwoPlayerBase {
	@Autowired
	protected SectorDaoService sectorDaoServiceImpl;
	private static final SectorCoords TANK = new SectorCoords(2, 0);
	private static final SectorCoords MOV = new SectorCoords(3, 0);
	private static final SectorCoords MOV2 = new SectorCoords(3, 6);
	private static final SectorCoords BETWEEN = new SectorCoords(4, 0);
	private static final SectorCoords CLOSER = new SectorCoords(5, 0);
	private static final SectorCoords INT = new SectorCoords(5, 1);
	private static final SectorCoords INT2 = new SectorCoords(6, 1);
	private static final SectorCoords PORT = new SectorCoords(2, 2);
	private static final SectorCoords BYPORT = new SectorCoords(3, 2);

	@Test
	public void destNoSupplyNoInterdictsDest() {
		Unit mdest = unitDaoService
				.buildUnit(nationMe, MOV, UnitType.DESTROYER);
		Unit idest = unitDaoService.buildUnit(nationThem, INT,
				UnitType.DESTROYER);
		Result<MoveCost> result = moveUnits(makeUnitList(mdest),
				BETWEEN);
		assertResult(result);
		assertNoInterdiction(mdest, idest, result);
	}

	@Test
	public void destInSupplyInterdictsDest() {
		Unit mdest = unitDaoService
				.buildUnit(nationMe, MOV, UnitType.DESTROYER);
		Unit idest = unitDaoService.buildUnit(nationThem, INT,
				UnitType.DESTROYER);
		sectorDaoServiceImpl.captureCity(nationThem, PORT);
		setBuild(PORT, UnitType.TRANSPORT);
		Result<MoveCost> result = moveUnits(makeUnitList(mdest),
				BETWEEN);
		assertFalseResult(result);
		assertInterdiction(mdest, idest, result);
		List<UnitAttackedBattleLog> logs = getUnitAttackedLogs();
		assertEquals(1, logs.size());
		UnitAttackedBattleLog log = logs.get(0);
		assertEquals(nationThem, log.getAttacker());
		assertEquals(nationMe, log.getDefender());
		assertEquals(idest, log.getAttackerUnit());
		assertEquals(mdest, log.getDefenderUnit());
		assertEquals(AttackType.INTERDICTION, log.getAttackType());
		assertTrue(idest.getCoords().equals(INT));

	}

	@Test
	public void patrolInSupplyInterdictsPatrol() {
		Unit mpatrol = unitDaoService.buildUnit(nationMe, MOV, UnitType.PATROL);
		Unit ipatrol = unitDaoService.buildUnit(nationThem, INT,
				UnitType.PATROL);
		sectorDaoServiceImpl.captureCity(nationThem, PORT);
		setBuild(PORT, UnitType.TRANSPORT);
		Result<MoveCost> result = moveUnits(makeUnitList(mpatrol),
				BETWEEN);
		assertFalseResult(result);
		assertInterdiction(mpatrol, ipatrol, result);
		List<UnitAttackedBattleLog> logs = getUnitAttackedLogs();
		assertEquals(1, logs.size());
		UnitAttackedBattleLog log = logs.get(0);
		assertEquals(nationThem, log.getAttacker());
		assertEquals(nationMe, log.getDefender());
		assertEquals(ipatrol, log.getAttackerUnit());
		assertEquals(mpatrol, log.getDefenderUnit());
		assertEquals(AttackType.INTERDICTION, log.getAttackType());
	}

	@Test
	public void patrolInSupplyNoInterdictsDest() {
		Unit mdest = unitDaoService
				.buildUnit(nationMe, MOV, UnitType.DESTROYER);
		Unit ipatrol = unitDaoService.buildUnit(nationThem, INT,
				UnitType.PATROL);
		sectorDaoServiceImpl.captureCity(nationThem, PORT);
		setBuild(PORT, UnitType.TRANSPORT);
		Result<MoveCost> result = moveUnits(makeUnitList(mdest),
				BETWEEN);
		assertResult(result);
		assertNoInterdiction(mdest, ipatrol, result);
	}

	@Test
	public void bbInSupplyInterdictsDest2Away() {
		Unit mdest = unitDaoService
				.buildUnit(nationMe, MOV, UnitType.DESTROYER);
		Unit ibb = unitDaoService.buildUnit(nationThem, INT2,
				UnitType.BATTLESHIP);
		sectorDaoServiceImpl.captureCity(nationThem, PORT);
		setBuild(PORT, UnitType.TRANSPORT);
		Result<MoveCost> result = moveUnits(makeUnitList(mdest),
				BETWEEN);
		assertFalseResult(result);
		assertInterdiction(mdest, ibb, result);
		List<UnitAttackedBattleLog> logs = getUnitAttackedLogs();
		assertEquals(1, logs.size());
		UnitAttackedBattleLog log = logs.get(0);
		assertEquals(nationThem, log.getAttacker());
		assertEquals(nationMe, log.getDefender());
		assertEquals(ibb, log.getAttackerUnit());
		assertEquals(mdest, log.getDefenderUnit());
		assertEquals(AttackType.INTERDICTION, log.getAttackType());
	}

	@Test
	public void patrolInSupplyInterdictsTransport2Away() {
		Unit mtransport = unitDaoService.buildUnit(nationMe, MOV,
				UnitType.TRANSPORT);
		Unit ipatrol = unitDaoService.buildUnit(nationThem, INT2,
				UnitType.PATROL);
		sectorDaoServiceImpl.captureCity(nationThem, PORT);
		setBuild(PORT, UnitType.TRANSPORT);
		Result<MoveCost> result = moveUnits(makeUnitList(mtransport),
				BETWEEN);
		assertFalseResult(result);
		assertInterdiction(mtransport, ipatrol, result);
		List<UnitAttackedBattleLog> logs = getUnitAttackedLogs();
		assertEquals(1, logs.size());
		UnitAttackedBattleLog log = logs.get(0);
		assertEquals(nationThem, log.getAttacker());
		assertEquals(nationMe, log.getDefender());
		assertEquals(ipatrol, log.getAttackerUnit());
		assertEquals(mtransport, log.getDefenderUnit());
		assertEquals(AttackType.INTERDICTION, log.getAttackType());
	}

	@Test
	public void destInSupplyInterdictsDest2Away() {
		Unit mdest = unitDaoService
				.buildUnit(nationMe, MOV, UnitType.DESTROYER);
		Unit idest = unitDaoService.buildUnit(nationThem, INT2,
				UnitType.DESTROYER);
		sectorDaoServiceImpl.captureCity(nationThem, PORT);
		setBuild(PORT, UnitType.TRANSPORT);
		Result<MoveCost> result = moveUnits(makeUnitList(mdest),
				BETWEEN);
		assertFalseResult(result);
		assertInterdiction(mdest, idest, result);
		List<UnitAttackedBattleLog> logs = getUnitAttackedLogs();
		assertEquals(1, logs.size());
		UnitAttackedBattleLog log = logs.get(0);
		assertEquals(nationThem, log.getAttacker());
		assertEquals(nationMe, log.getDefender());
		assertEquals(idest, log.getAttackerUnit());
		assertEquals(mdest, log.getDefenderUnit());
		assertEquals(AttackType.INTERDICTION, log.getAttackType());
		assertFalse(idest.getCoords().equals(INT2));
	}

	@Test
	public void destInSupplyNoInterdictsSub2Away() {
		Unit msub = unitDaoService.buildUnit(nationMe, MOV, UnitType.SUBMARINE);
		Unit idest = unitDaoService.buildUnit(nationThem, INT2,
				UnitType.DESTROYER);
		sectorDaoServiceImpl.captureCity(nationThem, PORT);
		setBuild(PORT, UnitType.TRANSPORT);
		Result<MoveCost> result = moveUnits(makeUnitList(msub),
				BETWEEN);
		assertResult(result);
		assertNoInterdiction(msub, idest, result);
		List<UnitAttackedBattleLog> logs = getUnitAttackedLogs();
		assertEquals(0, logs.size());
	}

	private ArrayList<UnitAttackedBattleLog> getUnitAttackedLogs() {
		return Lists.newArrayList(logDao
				.getUnitAttackedBattleLogs(nationMe));
	}

	@Test
	public void twoDestInSupplyInterdictsDestNoStack() {
		Unit mdest = unitDaoService
				.buildUnit(nationMe, MOV, UnitType.DESTROYER);
		Unit idest = unitDaoService.buildUnit(nationThem, INT,
				UnitType.DESTROYER);
		Unit idest2 = unitDaoService.buildUnit(nationThem, INT2,
				UnitType.DESTROYER);
		sectorDaoServiceImpl.captureCity(nationThem, PORT);
		setBuild(PORT, UnitType.TRANSPORT);
		Result<MoveCost> result = moveUnits(makeUnitList(mdest),
				BETWEEN);
		assertFalseResult(result);
		assertFalse(idest.getCoords().equals(idest2.getCoords()));
		assertInterdiction(mdest, idest, result);
	}

	@Test
	public void interdictionStopsMovement() {
		Unit mdest = unitDaoService
				.buildUnit(nationMe, MOV, UnitType.DESTROYER);
		Unit idest = unitDaoService.buildUnit(nationThem, INT,
				UnitType.DESTROYER);
		sectorDaoServiceImpl.captureCity(nationThem, PORT);
		setBuild(PORT, UnitType.TRANSPORT);
		Result<MoveCost> result = moveUnits(makeUnitList(mdest),
				CLOSER);
		assertFalseResult(result);
		assertFalse(mdest.getCoords().equals(CLOSER));
		assertInterdiction(mdest, idest, result);
	}

	@Test
	public void interdictionMovesXportInf() {
		Unit mxport = unitDaoService
				.buildUnit(nationMe, MOV2, UnitType.TRANSPORT);
		mxport.setMobility(UnitBase.getUnitBase(UnitType.TRANSPORT).getMaxMobility());

		Unit minf = unitDaoService
				.buildUnit(nationMe, MOV2, UnitType.INFANTRY);
		Unit idest = unitDaoService.buildUnit(nationThem, INT,
				UnitType.DESTROYER);
		sectorDaoServiceImpl.captureCity(nationThem, PORT);
		setBuild(PORT, UnitType.TRANSPORT);
		Result<MoveCost> result = moveUnits(makeUnitList(mxport),
				CLOSER);
		assertFalseResult(result);
		assertFalse(mxport.getCoords().equals(CLOSER));
		assertFalse(mxport.getCoords().equals(MOV2));
		assertFalse(minf.getCoords().equals(MOV2));
		assertTrue(minf.getCoords().equals(mxport.getCoords()));
		assertInterdiction(mxport, idest, result);
	}

	@Test
	public void interdictionMovesDeadXportKillsInf() {
		Unit mxport = unitDaoService
				.buildUnit(nationMe, MOV2, UnitType.TRANSPORT);
		mxport.setMobility(UnitBase.getUnitBase(UnitType.TRANSPORT).getMaxMobility());
		mxport.setHp(1);
		Unit minf = unitDaoService
				.buildUnit(nationMe, MOV2, UnitType.INFANTRY);
		unitDaoService.buildUnit(nationThem, INT,
				UnitType.DESTROYER);
		sectorDaoServiceImpl.captureCity(nationThem, PORT);
		setBuild(PORT, UnitType.TRANSPORT);
		Result<MoveCost> result = moveUnits(makeUnitList(mxport),
				CLOSER);
		assertFalseResult(result);
		assertFalse(mxport.isAlive());
		assertFalse(minf.isAlive());
	}
	
	@Test
	public void friendlyDestInSupplyNoInterdictsDest() {
		friendlyDeclared();
		Unit mdest = unitDaoService
				.buildUnit(nationMe, MOV, UnitType.DESTROYER);
		Unit idest = unitDaoService.buildUnit(nationThem, INT,
				UnitType.DESTROYER);
		sectorDaoServiceImpl.captureCity(nationThem, PORT);
		Result<MoveCost> result = moveUnits(makeUnitList(mdest),
				BETWEEN);
		assertResult(result);
		assertNoInterdiction(mdest, idest, result);
	}

	@Test
	public void tankOnLandInterdictsDestroyerNoCounterAttack() {
		Unit mdest = unitDaoService.buildUnit(nationMe, BETWEEN,
				UnitType.DESTROYER);
		Unit itank = unitDaoService.buildUnit(nationThem, TANK, UnitType.TANK);
		Result<MoveCost> result = moveUnits(makeUnitList(mdest), MOV);
		assertFalseResult(result);
		assertInterdiction(mdest, itank, result);
		assertNotDamaged(result, itank);
		assertFiredOnce(result, itank);
		assertNotFired(result, mdest);
	}

	@Test
	public void tankOnLandInterdictsBBNoCounterAttack() {
		Unit mbb = unitDaoService.buildUnit(nationMe, BETWEEN,
				UnitType.BATTLESHIP);
		Unit itank = unitDaoService.buildUnit(nationThem, TANK, UnitType.TANK);
		Result<MoveCost> result = moveUnits(makeUnitList(mbb), MOV);
		assertFalseResult(result);
		assertInterdiction(mbb, itank, result);
		assertNotDamaged(result, itank);
		assertFiredOnce(result, itank);
		assertNotFired(result, mbb);
	}

	@Test
	public void infOnLandNoInterdictsDestroyer() {
		Unit mdest = unitDaoService.buildUnit(nationMe, BETWEEN,
				UnitType.DESTROYER);
		Unit iinf = unitDaoService.buildUnit(nationThem, TANK,
				UnitType.INFANTRY);
		Result<MoveCost> result = moveUnits(makeUnitList(mdest), MOV);
		assertResult(result);
		assertNoInterdiction(mdest, iinf, result);
	}

	@Test
	public void tankInCityInterdictsDestroyerNoCounterAttack() {
		Unit mdest = unitDaoService.buildUnit(nationMe, BETWEEN,
				UnitType.DESTROYER);
		Unit itank = unitDaoService.buildUnit(nationThem, PORT, UnitType.TANK);
		sectorDaoServiceImpl.captureCity(nationThem, PORT);
		Result<MoveCost> result = moveUnits(makeUnitList(mdest),
				BYPORT);
		assertFalseResult(result);
		assertInterdiction(mdest, itank, result);
		assertNotDamaged(result, itank);
		assertNotFired(result, mdest);
	}

	@Test
	public void nbInterdictsAttack() {
		Unit mdest = unitDaoService
				.buildUnit(nationMe, MOV, UnitType.DESTROYER);
		Unit inb = unitDaoService.buildUnit(nationThem, INT,
				UnitType.NAVAL_BOMBER);
		Result<MoveCost> result = moveUnits(makeUnitList(mdest),
				BETWEEN);
		assertFalseResult(result);
		assertInterdiction(mdest, inb, result);
	}

	@Test
	public void fighterInterceptsInterdictingNb() {
		Unit mdest = unitDaoService
				.buildUnit(nationMe, MOV, UnitType.DESTROYER);
		Unit fighter = unitDaoService
		.buildUnit(nationMe, TANK, UnitType.FIGHTER);
		unitDaoService.buildUnit(nationThem, INT,
				UnitType.SUPPLY);
		Unit inb = unitDaoService.buildUnit(nationThem, INT2,
				UnitType.NAVAL_BOMBER);
		Result<MoveCost> result = moveUnits(makeUnitList(mdest),
				BETWEEN);
		assertResult(result);
		assertNotDamaged(result, mdest);
		assertMoved(result, fighter);
		assertEquals(inb.getCoords(), INT2);
	}

	@Test
	public void nb2InterdictsAttack() {
		Unit mdest = unitDaoService
				.buildUnit(nationMe, MOV, UnitType.DESTROYER);
		unitDaoService.buildUnit(nationThem, INT,
				UnitType.SUPPLY);
		Unit inb = unitDaoService.buildUnit(nationThem, INT2,
				UnitType.NAVAL_BOMBER);
		Result<MoveCost> result = moveUnits(makeUnitList(mdest),
				BETWEEN);
		assertFalseResult(result);
		assertInterdiction(mdest, inb, result);
		assertEquals(inb.getCoords(), INT2);
		assertFired(result, inb);
	}

	@Test
	public void nb2OnCarrierResuppliesInterdictsAttack() {
		Unit mdest = unitDaoService
				.buildUnit(nationMe, MOV, UnitType.DESTROYER);
		
		unitDaoService
		.buildUnit(nationThem, INT2, UnitType.CARRIER);
		Unit inb = unitDaoService.buildUnit(nationThem, INT2,
				UnitType.NAVAL_BOMBER);
		Result<MoveCost> result = moveUnits(makeUnitList(mdest),
				BETWEEN);
		assertFalseResult(result);
		assertInterdiction(mdest, inb, result);
		assertEquals(inb.getCoords(), INT2);
		assertNotFired(result, inb);
	}

	@Test
	public void nbNoSeeNoInterdictsAttack() {
		Unit mdest = unitDaoService
				.buildUnit(nationMe, MOV, UnitType.DESTROYER);
		Unit inb = unitDaoService.buildUnit(nationThem, INT2,
				UnitType.NAVAL_BOMBER);
		Result<MoveCost> result = moveUnits(makeUnitList(mdest),
				BETWEEN);
		assertResult(result);
		assertNoInterdiction(mdest, inb, result);
	}

	@Test
	public void fighterNoInterdictsAttack() {
		Unit mdest = unitDaoService
				.buildUnit(nationMe, MOV, UnitType.DESTROYER);
		Unit inb = unitDaoService.buildUnit(nationThem, INT,
				UnitType.FIGHTER);
		Result<MoveCost> result = moveUnits(makeUnitList(mdest),
				BETWEEN);
		assertResult(result);
		assertNoInterdiction(mdest, inb, result);
	}

	private void assertInterdiction(Unit mdest, Unit idest,
			Result<MoveCost> result) {
		assertDamaged(result, mdest);
		assertMoved(result, idest);
	}

	private void assertNoInterdiction(Unit mdest, Unit iunit,
			Result<MoveCost> result) {
		assertNotDamaged(result, mdest);
		assertNotMoved(result, iunit);
	}

}
