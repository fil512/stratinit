package com.kenstevens.stratinit;

import com.kenstevens.stratinit.client.event.ArrivedDataEventAccumulator;
import com.kenstevens.stratinit.client.rest.StratInitServerClient;
import org.springframework.boot.test.mock.mockito.MockBean;

public abstract class BaseMockStratInitClientTest extends BaseStratInitClientTest {
    @MockBean
    protected StratInitServerClient stratInitServer;
    @MockBean
    protected ArrivedDataEventAccumulator arrivedDataEventAccumulator;
}
