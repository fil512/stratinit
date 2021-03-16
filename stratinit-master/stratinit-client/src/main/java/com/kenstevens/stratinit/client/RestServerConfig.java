package com.kenstevens.stratinit.client;

import com.kenstevens.stratinit.client.rest.RestStratInitClient;
import com.kenstevens.stratinit.config.IServerConfig;
import com.kenstevens.stratinit.config.RunModeEnum;
import com.kenstevens.stratinit.config.ServerConfig;
import com.kenstevens.stratinit.remote.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Properties;

public class RestServerConfig implements IServerConfig {
    @Autowired
    RestStratInitClient stratInitServer;
    @Value("${server.port}")
    private int serverPort;

    IServerConfig serverConfig;

    @Override
    public long getMappedToStartedMillis() {
        init();
        return serverConfig.getMappedToStartedMillis();
    }

    @Override
    public long getScheduledToStartedMillis() {
        init();
        return serverConfig.getScheduledToStartedMillis();
    }

    @Override
    public long getFlushCacheMillis() {
        init();
        return serverConfig.getFlushCacheMillis();
    }

    @Override
    public int getMinPlayersToSchedule() {
        init();
        return serverConfig.getMinPlayersToSchedule();
    }

    @Override
    public RunModeEnum getRunMode() {
        init();
        return serverConfig.getRunMode();
    }

    private void init() {
        if (serverConfig == null) {
            Result<Properties> result = stratInitServer.getServerConfig();
            if (!result.isSuccess()) {
                throw new RuntimeException("Failed to retrieve config from server:" + result.toString(), result.getException());
            }
            Properties properties = result.getValue();
            RunModeEnum runModeEnum = RunModeEnum.valueOf(properties.getProperty(RunModeEnum.class.getSimpleName()));
            this.serverConfig = new ServerConfig(runModeEnum);
        }
    }
}
