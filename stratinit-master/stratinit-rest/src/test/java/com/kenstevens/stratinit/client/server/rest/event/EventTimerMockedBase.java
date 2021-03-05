package com.kenstevens.stratinit.client.server.rest.event;

import com.kenstevens.stratinit.client.server.event.svc.EventTimer;
import com.kenstevens.stratinit.client.server.rest.StratInitDaoBase;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class EventTimerMockedBase extends StratInitDaoBase {
    @MockBean
    protected EventTimer eventTimer;
}
