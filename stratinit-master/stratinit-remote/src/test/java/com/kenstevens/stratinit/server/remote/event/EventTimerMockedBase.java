package com.kenstevens.stratinit.server.remote.event;

import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import com.kenstevens.stratinit.server.event.EventQueue;
import com.kenstevens.stratinit.server.event.EventTimer;
import com.kenstevens.stratinit.server.remote.StratInitDaoBase;

@Ignore
public class EventTimerMockedBase extends StratInitDaoBase {
	protected Mockery context = new Mockery();

	protected EventTimer eventTimer;
	@Autowired
	private EventTimer origEventTimer;

	@Autowired
	protected EventQueue eventQueue;

	@Before
	public void setupMocks() {

		eventTimer = context.mock(EventTimer.class);
		ReflectionTestUtils.setField(eventQueue, "eventTimer", eventTimer);
	}

	@After
	public void undoMocks() {
		ReflectionTestUtils.setField(eventQueue, "eventTimer", origEventTimer);
	}


}
