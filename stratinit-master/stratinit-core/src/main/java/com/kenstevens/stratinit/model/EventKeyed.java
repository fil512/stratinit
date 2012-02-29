package com.kenstevens.stratinit.model;

public interface EventKeyed {
	public Object getKey();
	public boolean isKeyUnique();
}
