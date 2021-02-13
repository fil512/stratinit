package com.kenstevens.stratinit.dao.impl;

import com.kenstevens.stratinit.StratInitTest;
import com.kenstevens.stratinit.dao.LogDao;
import com.kenstevens.stratinit.model.AttackType;
import com.kenstevens.stratinit.model.CityCapturedBattleLog;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitAttackedBattleLog;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class LogDaoTest extends StratInitTest {
	@Autowired
	private LogDao logDao;

	@Test
	public void testCityLogPersistence() {
		createUnit1();
		CityCapturedBattleLog log = new CityCapturedBattleLog(
				AttackType.INITIAL_ATTACK, testUnit1, testNation1, testCoords);
		logDao.save(log);
		List<CityCapturedBattleLog> logs = logDao
				.getCityCapturedBattleLogs(testGame);
		assertEquals(1, logs.size());
		assertEquals(log.getId(), logs.get(0).getId());
	}

	@Test
	public void removeCityLog() {
		createUnit1();
		CityCapturedBattleLog log = new CityCapturedBattleLog(
				AttackType.INITIAL_ATTACK, testUnit1, testNation1, testCoords);
		logDao.save(log);
		logDao.delete(log);
		List<CityCapturedBattleLog> logs = logDao
				.getCityCapturedBattleLogs(testGame);
		assertEquals(0, logs.size());
	}

	@Test
	public void testUnitLogPersistence() {
		createUnit1();
		Unit unit2 = new Unit(testNation1, UnitType.INFANTRY, testCoords);
		unitDao.save(unit2);
		UnitAttackedBattleLog log = new UnitAttackedBattleLog(
				AttackType.INITIAL_ATTACK, testUnit1, unit2, testCoords);
		logDao.save(log);
		List<UnitAttackedBattleLog> logs = logDao
				.getUnitAttackedBattleLogs(testGame);
		assertEquals(1, logs.size());
		assertEquals(log.getId(), logs.get(0).getId());
	}
	
	@Test(expected=InvalidDataAccessApiUsageException.class)
	public void testDetachedUnitLogPersistence() {
		createUnit1();
		Unit unit2 = new Unit(testNation1, UnitType.INFANTRY, testCoords);
		UnitAttackedBattleLog log = new UnitAttackedBattleLog(
				AttackType.INITIAL_ATTACK, testUnit1, unit2, testCoords);
		logDao.save(log);
		List<UnitAttackedBattleLog> logs = logDao
				.getUnitAttackedBattleLogs(testGame);
		assertEquals(1, logs.size());
		assertEquals(log.getId(), logs.get(0).getId());
	}
	
	@Test
	public void testUnitDiedLogPersistence() {
		createUnit1();
		Unit unit2 = new Unit(testNation1, UnitType.INFANTRY, testCoords);
		unitDao.save(unit2);
		unit2.kill();
		unitDao.merge(unit2);
		UnitAttackedBattleLog log = new UnitAttackedBattleLog(
				AttackType.INITIAL_ATTACK, testUnit1, unit2, testCoords);
		logDao.save(log);
		List<UnitAttackedBattleLog> logs = logDao
				.getUnitAttackedBattleLogs(testGame);
		assertEquals(1, logs.size());
		assertEquals(log.getId(), logs.get(0).getId());
	}
	
}
