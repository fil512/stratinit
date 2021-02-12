package com.kenstevens.stratinit.repo.impl;

import com.kenstevens.stratinit.StratInitTest;
import com.kenstevens.stratinit.model.UnitSeen;
import com.kenstevens.stratinit.repo.UnitDal;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.FlushModeType;

@Transactional
public class GameRepoTest extends StratInitTest {
	@Autowired
	private UnitDal unitDal;

	@Test
	public void testSaveOrUpdateUnitSeen() {
		createUnit1();
		UnitSeen unitSeen = new UnitSeen(testNation1, testUnit1);
		unitDal.saveOrUpdate(unitSeen);
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
