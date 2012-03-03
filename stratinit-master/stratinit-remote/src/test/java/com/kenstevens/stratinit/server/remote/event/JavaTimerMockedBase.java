package com.kenstevens.stratinit.server.remote.event;

import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.server.remote.StratInitDaoBase;
import com.kenstevens.stratinit.server.remote.helper.WorldManagerHelper;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.SectorType;
import com.kenstevens.stratinit.type.UnitType;

@Ignore
public class JavaTimerMockedBase  extends StratInitDaoBase {
	@Autowired
	WorldManagerHelper worldManagerHelper;
	
	protected Mockery context = new Mockery();

	protected JavaTimer javaTimer;

	protected EventTimer eventTimer = new EventTimerImpl();

	protected City city;
	
	@Before
	public void setupMocks() {
		javaTimer = context.mock(JavaTimer.class);
		ReflectionTestUtils.setField(eventTimer, "javaTimer", javaTimer);
	}
	
	@Before
	public void makeCity() {
		Nation nation = worldManagerHelper.createNation(testGameId);
		city = new City(new Sector(testGame, new SectorCoords(0,0), SectorType.PLAYER_CITY), nation, UnitType.INFANTRY);
	}
}
