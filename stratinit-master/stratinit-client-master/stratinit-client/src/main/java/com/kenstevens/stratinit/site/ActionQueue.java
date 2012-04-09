package com.kenstevens.stratinit.site;


public interface ActionQueue {

	void put(Action action) throws InterruptedException;

	void start();

	void shutdown();

	boolean isEmpty();

	void pause();

	void resume();

	void stop();
}