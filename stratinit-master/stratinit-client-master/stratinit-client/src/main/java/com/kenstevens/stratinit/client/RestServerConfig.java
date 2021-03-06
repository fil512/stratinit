package com.kenstevens.stratinit.client;

import com.kenstevens.stratinit.client.rest.IStratInitServer;
import com.kenstevens.stratinit.config.IServerConfig;
import com.kenstevens.stratinit.config.RunModeEnum;
import com.kenstevens.stratinit.config.ServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class RestServerConfig implements IServerConfig {
    @Autowired
    IStratInitServer stratInitServer;

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
            Properties properties = stratInitServer.getServerConfig().getValue();
            RunModeEnum runModeEnum = RunModeEnum.valueOf(properties.getProperty(RunModeEnum.class.getSimpleName()));
            serverConfig = new ServerConfig(runModeEnum);
        }
    }
}
