package com.kenstevens.stratinit.model;

public interface EventKeyed {
	Object getKey();
	boolean isKeyUnique();
}
