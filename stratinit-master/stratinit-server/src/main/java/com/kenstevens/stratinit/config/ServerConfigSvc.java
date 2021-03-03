package com.kenstevens.stratinit.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ServerConfigSvc implements IServerConfig {
    private final ServerConfig serverConfig;

    @Autowired
    public ServerConfigSvc(@Value("${stratinit.mode}") RunModeEnum runModeEnum) {
        serverConfig = new ServerConfig(runModeEnum);
    }

    @Override
    public long getMappedToStartedMillis() {
        return serverConfig.getMappedToStartedMillis();
    }

    @Override
    public long getScheduledToStartedMillis() {
        return serverConfig.getScheduledToStartedMillis();
    }

    @Override
    public long getFlushCacheMillis() {
        return serverConfig.getFlushCacheMillis();
    }

    @Override
    public int getMinPlayersToSchedule() {
        return serverConfig.getMinPlayersToSchedule();
    }

    @Override
    public RunModeEnum getRunMode() {
        return serverConfig.getRunMode();
    }
}
