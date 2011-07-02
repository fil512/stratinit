package com.kenstevens.stratinit.event;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.HandlerManager;
import com.kenstevens.stratinit.StratInitClientTest;

// FIXME remove hibernate-core from jnlp (maybe generate new jnlp?)
public class EventTest extends StratInitClientTest {
	public static final String TEST_STRING = "test";

	@Autowired
	HandlerManager handlerManager;

	static class FooEvent extends GwtEvent<FooEventHandler> {

		private final String name;

		public FooEvent(String name) {
			this.name = name;
		}

		@Override
		public Type<FooEventHandler> getAssociatedType() {
			return FooEventHandler.TYPE;
		}

		@Override
		protected void dispatch(FooEventHandler handler) {
			handler.doFooStuff(name);
		}

		public String getName() {
			return name;
		}
	}

	static abstract class FooEventHandler implements EventHandler {
		public static final Type<FooEventHandler> TYPE = new Type<FooEventHandler>();

		abstract public void doFooStuff(String instring);
	}

	@Test
	public void testDispatch() {
		FooEvent fooEvent = new FooEvent(TEST_STRING);
		final int[] callCount = {0};
		final String[] gotString = {""};

		FooEventHandler testHandler = new FooEventHandler() {

			public void doFooStuff(String instring) {
				++callCount[0];
				gotString[0] = instring;
			}
		};
		assertEquals(0, callCount[0]);
		assertEquals("", gotString[0]);
		handlerManager.addHandler(FooEventHandler.TYPE, testHandler);
		handlerManager.fireEvent(fooEvent);
		assertEquals(1, callCount[0]);
		assertEquals(TEST_STRING, gotString[0]);
	}
}
