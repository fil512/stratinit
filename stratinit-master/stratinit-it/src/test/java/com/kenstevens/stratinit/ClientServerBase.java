package com.kenstevens.stratinit;

import com.kenstevens.stratinit.client.api.IAudioPlayer;
import com.kenstevens.stratinit.client.api.ICommandList;
import com.kenstevens.stratinit.client.api.IEventSelector;
import com.kenstevens.stratinit.client.api.IProgressBar;
import com.kenstevens.stratinit.client.event.IEventExecutor;
import com.kenstevens.stratinit.config.TestConfig;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class, classes = {DaoConfig.class, DaoTestConfig.class, TestConfig.class})
public abstract class ClientServerBase {
    @MockBean
    IEventExecutor eventExecutor;
    @MockBean
    IProgressBar progressBar;
    @MockBean
    ICommandList commandList;
    @MockBean
    IAudioPlayer audioPlayer;
    @MockBean
    IEventSelector eventSelector;
}
