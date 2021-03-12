package com.kenstevens.stratinit.server.rest.event;

import com.kenstevens.stratinit.client.server.rest.StratInitDaoBase;
import com.kenstevens.stratinit.server.event.svc.EventTimer;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class EventTimerMockedBase extends StratInitDaoBase {
    @MockBean
    protected EventTimer eventTimer;
}
