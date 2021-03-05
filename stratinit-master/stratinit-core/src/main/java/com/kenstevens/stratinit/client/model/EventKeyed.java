package com.kenstevens.stratinit.client.model;

public interface EventKeyed {
	Object getKey();
	boolean isKeyUnique();
}
