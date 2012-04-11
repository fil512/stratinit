package com.kenstevens.stratinit.event;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.eventbus.Subscribe;
import com.kenstevens.stratinit.StratInitClientTest;

public class EventTest extends StratInitClientTest {
	public static final String TEST_STRING = "test";
	FooEvent fooEvent = new FooEvent(TEST_STRING);
	final int[] callCount = {0};
	final String[] gotString = {""};

	@Autowired
	StratinitEventBus eventBus;

	static class FooEvent implements StratInitEvent {

		private final String name;

		public FooEvent(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	@Subscribe
	public void handleFooEvent(FooEvent event) {
		++callCount[0];
		gotString[0] = event.getName();
	}
	
	@Test
	public void testDispatch() {

		assertEquals(0, callCount[0]);
		assertEquals("", gotString[0]);
		eventBus.register(this);
		eventBus.post(fooEvent);
		assertEquals(1, callCount[0]);
		assertEquals(TEST_STRING, gotString[0]);
	}
}
