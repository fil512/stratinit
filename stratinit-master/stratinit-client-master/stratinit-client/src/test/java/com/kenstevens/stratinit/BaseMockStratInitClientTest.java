package com.kenstevens.stratinit;

import com.kenstevens.stratinit.event.ArrivedDataEventAccumulator;
import com.kenstevens.stratinit.rest.IStratInitServer;
import org.springframework.boot.test.mock.mockito.MockBean;

public abstract class BaseMockStratInitClientTest extends BaseStratInitClientTest {
    @MockBean
    protected IStratInitServer stratInitServer;
    @MockBean
    protected ArrivedDataEventAccumulator arrivedDataEventAccumulator;
}
