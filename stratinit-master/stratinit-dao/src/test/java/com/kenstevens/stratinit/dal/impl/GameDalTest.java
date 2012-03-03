package com.kenstevens.stratinit.dal.impl;

import javax.persistence.FlushModeType;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.kenstevens.stratinit.StratInitTest;
import com.kenstevens.stratinit.dal.UnitDal;
import com.kenstevens.stratinit.model.UnitSeen;

@Transactional
public class GameDalTest extends StratInitTest {
	@Autowired
	private UnitDal unitDal;

	@Test
	public void testSaveOrUpdateUnitSeen() {
		createUnit1();
		UnitSeen unitSeen = new UnitSeen(testNation1, testUnit1);
		unitDal.saveOrUpdate(unitSeen );
	}

	@Test
	public void testSaveOrUpdateUnitSeenTwice() {
		entityManager.setFlushMode(FlushModeType.COMMIT);
		createUnit1();
		UnitSeen unitSeen = new UnitSeen(testNation1, testUnit1);
		unitDal.saveOrUpdate(unitSeen);
		unitDal.saveOrUpdate(unitSeen);
	}
}
