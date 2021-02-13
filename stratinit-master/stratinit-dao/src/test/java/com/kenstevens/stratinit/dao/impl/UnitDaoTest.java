package com.kenstevens.stratinit.dao.impl;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.StratInitTest;
import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitSeen;
import com.kenstevens.stratinit.repo.UnitRepo;
import com.kenstevens.stratinit.repo.UnitSeenRepo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class UnitDaoTest extends StratInitTest {
	@Autowired
	private UnitDao unitDao;
	@Autowired
	private UnitRepo unitRepo;
	@Autowired
	private UnitSeenRepo unitSeenRepo;
	@Autowired
	protected DataCache dataCache;

	@Test
	public void testUnitPersistance() {
		createUnit1();
		List<Unit> result = Lists.newArrayList(unitDao.getUnits(testGame,
				testCoords));
		assertEquals(1, result.size());
		assertEquals(testCoords, result.get(0).getCoords());
		result = unitDao.getUnits(testNation1);
		assertEquals(1, result.size());
		assertEquals(testCoords, result.get(0).getCoords());
	}

	@Test
	public void testUnitSeenDoublePersist() {
		createUnit1();
		UnitSeen unitSeen = new UnitSeen(testNation1, testUnit1);
		unitDao.save(unitSeen);
		unitDao.save(unitSeen);
	}

	@Test
	public void testUnitSeenRemoveUnit() {
		createUnit1();
		UnitSeen unitSeen = new UnitSeen(testNation1, testUnit1);
		unitDao.save(unitSeen);
		assertEquals(1, unitRepo.count());
		assertEquals(1, unitSeenRepo.count());
		unitDao.delete(testUnit1);
		assertEquals(0, unitRepo.count());
		assertEquals(0, unitSeenRepo.count());
	}
}
