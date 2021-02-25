package com.kenstevens.stratinit.server.remote.event;

import com.kenstevens.stratinit.server.event.CityBuildEvent;
import com.kenstevens.stratinit.server.event.Event;
import org.jmock.Expectations;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EventTimerTest extends JavaTimerMockedBase {
    @Test
    public void shutdown() {
        assertFalse(eventTimer.isShuttingDown());
        context.checking(new Expectations() {
            {
                oneOf(javaTimer).cancel();
            }
        });
        eventTimer.shutdown();
        assertTrue(eventTimer.isShuttingDown());
        context.assertIsSatisfied();
    }

    @Test
    public void scheduleOne() {
        final Event event = new CityBuildEvent(city);
        context.checking(new Expectations() {
            {
                oneOf(javaTimer).schedule(event.getTask(), event.getTime(), event.getPeriodMilliseconds());
            }
        });
        assertEquals(null, eventTimer.getScheduledEvent(city));
        eventTimer.schedule(event);
        assertEquals(event, eventTimer.getScheduledEvent(city));
        context.assertIsSatisfied();
    }

    @Test
    public void scheduleTwo() {
        final Event event1 = new CityBuildEvent(city);
        final Event event2 = new CityBuildEvent(city);
        context.checking(new Expectations() {
            {
                oneOf(javaTimer).schedule(event1.getTask(), event1.getTime(), event1.getPeriodMilliseconds());
                oneOf(javaTimer).cancel(event1);
                oneOf(javaTimer).schedule(event2.getTask(), event2.getTime(), event2.getPeriodMilliseconds());
            }
        });
        assertEquals(null, eventTimer.getScheduledEvent(city));
        eventTimer.schedule(event1);
        assertEquals(event1, eventTimer.getScheduledEvent(city));
        eventTimer.schedule(event2);
        assertEquals(event2, eventTimer.getScheduledEvent(city));
        context.assertIsSatisfied();
    }
}
