package com.kenstevens.stratinit;

import com.kenstevens.stratinit.client.api.ICommandList;
import com.kenstevens.stratinit.client.api.IProgressBar;
import com.kenstevens.stratinit.client.audio.WavPlayer;
import com.kenstevens.stratinit.client.event.IEventExecutor;
import com.kenstevens.stratinit.client.server.rest.StratInitDaoBase;
import com.kenstevens.stratinit.shell.StatusReporter;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public abstract class ClientServerBase extends StratInitDaoBase {
    @MockBean
    IEventExecutor eventExecutor;
    @MockBean
    IProgressBar progressBar;
    @MockBean
    StatusReporter statusReporter;
    @MockBean
    ICommandList commandList;
    @MockBean
    WavPlayer wavPlayer;
}
