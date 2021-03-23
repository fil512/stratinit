package com.kenstevens.stratinit.client.event;

public interface IEventExecutor {
    void asyncExec(Runnable runnable);
}
