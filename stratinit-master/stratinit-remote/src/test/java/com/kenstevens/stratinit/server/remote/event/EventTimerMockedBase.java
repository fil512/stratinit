package com.kenstevens.stratinit.server.remote.event;

import com.kenstevens.stratinit.server.event.svc.EventQueue;
import com.kenstevens.stratinit.server.event.svc.EventTimer;
import com.kenstevens.stratinit.server.remote.StratInitDaoBase;
import org.jmock.Mockery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

@Disabled
public class EventTimerMockedBase extends StratInitDaoBase {
	protected Mockery context = new Mockery();

	protected EventTimer eventTimer;
	@Autowired
	private EventTimer origEventTimer;

	@Autowired
	protected EventQueue eventQueue;

	@BeforeEach
	public void setupMocks() {

		eventTimer = context.mock(EventTimer.class);
		ReflectionTestUtils.setField(eventQueue, "eventTimer", eventTimer);
	}

	@AfterEach
	public void undoMocks() {
		ReflectionTestUtils.setField(eventQueue, "eventTimer", origEventTimer);
	}


}
