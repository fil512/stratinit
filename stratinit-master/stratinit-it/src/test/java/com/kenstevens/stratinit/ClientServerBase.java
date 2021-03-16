package com.kenstevens.stratinit;

import com.kenstevens.stratinit.client.api.IAudioPlayer;
import com.kenstevens.stratinit.client.api.ICommandList;
import com.kenstevens.stratinit.client.api.IProgressBar;
import com.kenstevens.stratinit.client.api.IStatusReporter;
import com.kenstevens.stratinit.client.event.IEventExecutor;
import com.kenstevens.stratinit.client.server.rest.StratInitDaoBase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public abstract class ClientServerBase extends StratInitDaoBase {
    @MockBean
    IEventExecutor eventExecutor;
    @MockBean
    IProgressBar progressBar;
    @MockBean
    IStatusReporter statusReporter;
    @MockBean
    ICommandList commandList;
    @MockBean
    IAudioPlayer audioPlayer;
}
