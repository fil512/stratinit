package com.kenstevens.stratinit.server.remote.event;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.server.event.svc.EventTimer;
import com.kenstevens.stratinit.server.event.svc.EventTimerImpl;
import com.kenstevens.stratinit.server.event.svc.JavaTimer;
import com.kenstevens.stratinit.server.remote.StratInitDaoBase;
import com.kenstevens.stratinit.server.remote.helper.WorldManagerHelper;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.SectorType;
import com.kenstevens.stratinit.type.UnitType;
import org.jmock.Mockery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

@Disabled
public class JavaTimerMockedBase  extends StratInitDaoBase {
	@Autowired
	WorldManagerHelper worldManagerHelper;

	protected Mockery context = new Mockery();

	protected JavaTimer javaTimer;

	protected EventTimer eventTimer = new EventTimerImpl();

	protected City city;

	@BeforeEach
	public void setupMocks() {
        javaTimer = context.mock(JavaTimer.class);
        ReflectionTestUtils.setField(eventTimer, "javaTimer", javaTimer);
        eventTimer.start();
    }

	@BeforeEach
	public void makeCity() {
		Nation nation = worldManagerHelper.createNation(testGameId);
		city = new City(new Sector(testGame, new SectorCoords(0, 0), SectorType.PLAYER_CITY), nation, UnitType.INFANTRY);
	}
}
