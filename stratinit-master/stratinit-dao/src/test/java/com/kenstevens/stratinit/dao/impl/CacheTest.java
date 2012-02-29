package com.kenstevens.stratinit.dao.impl;

import org.hibernate.internal.SessionImpl;
import org.hibernate.stat.Statistics;
import org.junit.Assert;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;

import com.kenstevens.stratinit.StratInitTest;
import com.kenstevens.stratinit.dao.UnitDao;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.type.UnitType;

@Ignore
public class CacheTest extends StratInitTest {
	@Autowired
	private UnitDao unitDao;

	// TODO TEST why no work in maven?
	public void testCache() {

		createNation1();
		for (int i = 0; i < 10; ++i) {
			Unit unit = new Unit(testNation1, UnitType.INFANTRY, testCoords);
			unitDao.persist(unit);
		}
		SessionImpl session = (SessionImpl) entityManager.getDelegate();
		Statistics stats = session.getFactory().getStatistics();

		long initPuts = stats.getSecondLevelCachePutCount();
		long initHits = stats.getSecondLevelCacheHitCount();

		unitDao.getAllUnits();
		assertStats(initPuts + 13, initHits);
		unitDao.getAllUnits();
		assertStats(initPuts + 13, initHits + 13);
	}

	private void assertStats(long puts, long hits) {
		SessionImpl session = (SessionImpl) entityManager.getDelegate();
		Statistics stats = session.getFactory().getStatistics();
		Assert.assertEquals(puts, stats.getSecondLevelCachePutCount());
		Assert.assertEquals(hits, stats.getSecondLevelCacheHitCount());
	}


}
