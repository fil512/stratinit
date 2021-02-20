package com.kenstevens.stratinit.site;


public interface ActionQueue {

    void put(Action action);

    void start();

    void shutdown();

    boolean isEmpty();

    void pause();

    void resume();

	void stop();
}