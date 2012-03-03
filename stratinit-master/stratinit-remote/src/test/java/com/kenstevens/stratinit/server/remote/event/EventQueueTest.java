package com.kenstevens.stratinit.server.remote.event;

import java.util.Date;

import org.jmock.Expectations;
import org.junit.Test;

import com.kenstevens.stratinit.model.City;
import com.kenstevens.stratinit.model.Nation;
import com.kenstevens.stratinit.model.Unit;
import com.kenstevens.stratinit.type.UnitType;
import com.kenstevens.stratinit.util.GameScheduleHelper;

@SuppressWarnings("deprecation")
public class EventQueueTest extends EventTimerMockedBase {

	private Date now = new Date();
	private Date started = new Date(now.getTime() - 1);

	@Test
	public void shutdown() {
		context.checking(new Expectations() {
			{
				oneOf(eventTimer).shutdown();
			}
		});

		eventQueue.shutdown();
		context.assertIsSatisfied();
	}

	@Test
	public void scheduleCity() {
		GameScheduleHelper.setStartTime(testGame, started);
		City city = new City();
		Nation nation = new Nation(testGame, playerMe);
		city.setNation(nation);
		city.setBuild(UnitType.INFANTRY, new Date());
		context.checking(new Expectations() {
			{
				oneOf(eventTimer).schedule((Event)with(a(CityBuildEvent.class)));
			}
		});

		eventQueue.schedule(city);
		context.assertIsSatisfied();
	}

	@Test
	public void scheduleUnit() {
		GameScheduleHelper.setStartTime(testGame, started);
		Unit unit = new Unit();
		Nation nation = new Nation(testGame, playerMe);
		unit.setNation(nation);
		unit.setType(UnitType.INFANTRY);
		context.checking(new Expectations() {
			{
				oneOf(eventTimer).schedule((Event)with(a(UnitUpdateEvent.class)));
			}
		});

		eventQueue.schedule(unit);
		context.assertIsSatisfied();
	}

	@Test
	public void scheduleCreatedGame() {
		GameScheduleHelper.setStartTime(testGame, null);
		testGame.setMapped(null);
		eventQueue.schedule(testGame, false);
		context.assertIsSatisfied();
	}

	@Test
	public void scheduleStartedGame() {
		GameScheduleHelper.setStartTime(testGame, started);
		testGame.setMapped(null);
		context.checking(new Expectations() {
			{
				oneOf(eventTimer).schedule((Event)with(a(GameMapEvent.class)));
				oneOf(eventTimer).schedule((Event)with(a(GameStartEvent.class)));
			}
		});
		eventQueue.schedule(testGame, false);
		context.assertIsSatisfied();
	}

	@Test
	public void scheduleMappedGame() {
		GameScheduleHelper.setStartTime(testGame, started);
		testGame.setMapped(started);
		context.checking(new Expectations() {
			{
				oneOf(eventTimer).schedule((Event)with(a(GameStartEvent.class)));
			}
		});
		eventQueue.schedule(testGame, false);
		context.assertIsSatisfied();
	}
}
