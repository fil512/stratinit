package com.kenstevens.stratinit.client.model;

public record EventKey(Class<? extends EventKeyed> clazz, Object key) {
	public EventKey(EventKeyed keyed) {
		this(keyed.getClass(), keyed.getKey());
	}

	@Override
	public String toString() {
		return clazz.getName() + " " + key;
	}
}
