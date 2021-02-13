package com.kenstevens.stratinit.dao.impl;

import com.google.common.collect.Lists;
import com.kenstevens.stratinit.StratInitTest;
import com.kenstevens.stratinit.cache.DataCache;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.model.UnitSeen;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UnitDaoTest extends StratInitTest {
	@Autowired
	private UnitDao unitDao;
	@Autowired
	protected DataCache dataCache;

	@Test
	public void testUnitPersistance() {
		createUnit1();
		List<Unit> result = Lists.newArrayList(unitDao.getUnits(testGame,
				testCoords));
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(testCoords, result.get(0).getCoords());
		result = unitDao.getUnits(testNation1);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(testCoords, result.get(0).getCoords());
	}

	@Test
	public void testUnitSeenDoublePersist() {
		createUnit1();
		UnitSeen unitSeen = new UnitSeen(testNation1, testUnit1);
		unitDao.persist(unitSeen);
		unitDao.persist(unitSeen);
	}

	@Test
	public void testUnitSeenRemoveUnit() {
		createUnit1();
		UnitSeen unitSeen = new UnitSeen(testNation1, testUnit1);
		unitDao.persist(unitSeen);
		entityManager.flush();
		unitDao.remove(testUnit1);
		unitDao.persist(unitSeen);
	}
}
