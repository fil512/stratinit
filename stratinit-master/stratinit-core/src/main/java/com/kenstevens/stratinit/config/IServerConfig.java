package com.kenstevens.stratinit.config;

public interface IServerConfig {

    long getMappedToStartedMillis();

    long getScheduledToStartedMillis();

    long getFlushCacheMillis();

    int getMinPlayersToSchedule();

    RunModeEnum getRunMode();
}
