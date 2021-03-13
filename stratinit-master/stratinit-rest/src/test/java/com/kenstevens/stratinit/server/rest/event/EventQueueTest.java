package com.kenstevens.stratinit.server.rest.event;

import com.kenstevens.stratinit.client.model.City;
import com.kenstevens.stratinit.client.model.Nation;
import com.kenstevens.stratinit.client.model.Unit;
import com.kenstevens.stratinit.client.util.GameScheduleHelper;
import com.kenstevens.stratinit.server.event.CityBuildEvent;
import com.kenstevens.stratinit.server.event.GameMapEvent;
import com.kenstevens.stratinit.server.event.GameStartEvent;
import com.kenstevens.stratinit.server.event.UnitUpdateEvent;
import com.kenstevens.stratinit.server.event.svc.EventQueue;
import com.kenstevens.stratinit.type.UnitType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EventQueueTest extends EventTimerMockedBase {
	@Autowired
	private EventQueue eventQueue;

	private final Date now = new Date();
	private final Date started = new Date(now.getTime() - 1);

	@Test
	public void shutdown() {
		eventQueue.shutdown();
		verify(eventTimer).shutdown();
	}

	@Test
	public void scheduleCity() {
		GameScheduleHelper.setStartTime(testGame, started);
		City city = new City();
		Nation nation = new Nation(testGame, playerMe);
		city.setNation(nation);
		city.setBuild(UnitType.INFANTRY, new Date());

		eventQueue.schedule(city);
		verify(eventTimer).schedule(any(CityBuildEvent.class));
	}

	@Test
	public void scheduleUnit() {
		GameScheduleHelper.setStartTime(testGame, started);
		Unit unit = new Unit();
		Nation nation = new Nation(testGame, playerMe);
		unit.setNation(nation);
		unit.setType(UnitType.INFANTRY);

		eventQueue.schedule(unit);
		verify(eventTimer).schedule(any(UnitUpdateEvent.class));
	}

	@Test
	public void scheduleCreatedGame() {
		GameScheduleHelper.setStartTime(testGame, null);
		testGame.setMapped(null);

		reset(eventTimer);
		eventQueue.schedule(testGame, false);
		verifyNoInteractions(eventTimer);
	}

	@Test
	public void scheduleStartedGame() {
		GameScheduleHelper.setStartTime(testGame, started);
		testGame.setMapped(null);
		eventQueue.schedule(testGame, false);
		verify(eventTimer).schedule(any(GameMapEvent.class));
		verify(eventTimer).schedule(any(GameStartEvent.class));
	}

	@Test
	public void scheduleMappedGame() {
		GameScheduleHelper.setStartTime(testGame, started);
		testGame.setMapped(started);
		eventQueue.schedule(testGame, false);
		verify(eventTimer).schedule(any(GameStartEvent.class));
	}
}
