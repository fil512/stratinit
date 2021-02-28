package com.kenstevens.stratinit.server.rest.event;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Sector;
import com.kenstevens.stratinit.server.event.CityBuildEvent;
import com.kenstevens.stratinit.server.event.Event;
import com.kenstevens.stratinit.server.event.svc.EventTimer;
import com.kenstevens.stratinit.server.event.svc.JavaTimer;
import com.kenstevens.stratinit.server.rest.StratInitDaoBase;
import com.kenstevens.stratinit.server.rest.helper.WorldManagerHelper;
import com.kenstevens.stratinit.type.SectorCoords;
import com.kenstevens.stratinit.type.SectorType;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@DirtiesContext
public class EventTimerTest extends StratInitDaoBase {
    protected City city;
    @MockBean
    JavaTimer javaTimer;
    @Autowired
    EventTimer eventTimer;
    @Autowired
    WorldManagerHelper worldManagerHelper;

    @BeforeEach
    public void init() {
        super.init();
        eventTimer.clearForUnitTest();
        eventTimer.start();
        Nation nation = worldManagerHelper.createNation(testGameId);
        city = new City(new Sector(testGame, new SectorCoords(0, 0), SectorType.PLAYER_CITY), nation, UnitType.INFANTRY);
    }

    @AfterEach
    public void shutdown() {
        eventTimer.shutdown();
    }

    @Test
    public void testShutdown() {
        assertFalse(eventTimer.isShuttingDown());
        eventTimer.shutdown();
        verify(javaTimer).cancel();
        assertTrue(eventTimer.isShuttingDown());
    }

    @Test
    public void scheduleOne() {
        final Event event = new CityBuildEvent(city);
        assertEquals(null, eventTimer.getScheduledEvent(city));
        eventTimer.schedule(event);
        assertEquals(event, eventTimer.getScheduledEvent(city));
        verify(javaTimer).schedule(event.getTask(), event.getTime(), event.getPeriodMilliseconds());
    }

    @Test
    public void scheduleTwo() {
        final Event event1 = new CityBuildEvent(city);
        final Event event2 = new CityBuildEvent(city);
        assertEquals(null, eventTimer.getScheduledEvent(city));
        eventTimer.schedule(event1);
        assertEquals(event1, eventTimer.getScheduledEvent(city));
        eventTimer.schedule(event2);
        assertEquals(event2, eventTimer.getScheduledEvent(city));
        verify(javaTimer).schedule(event1.getTask(), event1.getTime(), event1.getPeriodMilliseconds());
        verify(javaTimer).cancel(event1);
        verify(javaTimer).schedule(event2.getTask(), event2.getTime(), event2.getPeriodMilliseconds());
    }
}
