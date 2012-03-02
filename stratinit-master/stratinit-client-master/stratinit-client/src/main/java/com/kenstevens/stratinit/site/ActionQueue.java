package com.kenstevens.stratinit.site;


public interface ActionQueue {

	public abstract void put(Action action) throws InterruptedException;

	public abstract void start();

	public abstract void shutdown();

	public abstract boolean isEmpty();

	public abstract void pause();

	public abstract void resume();

	public abstract void stop();
}