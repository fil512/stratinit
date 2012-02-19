package com.kenstevens.stratinit.model;

public class EventKey {
	private final Class<? extends EventKeyed> clazz;
	private final Object key;
	
	public EventKey(EventKeyed keyed) {
		this.clazz = keyed.getClass();
		this.key = keyed.getKey();
	}
	
	public String toString() {
		return clazz.getName()+" "+key;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
		result = prime * result + ((getKey() == null) ? 0 : getKey().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventKey other = (EventKey) obj;
		if (clazz == null) {
			if (other.clazz != null)
				return false;
		} else if (!clazz.equals(other.clazz))
			return false;
		if (getKey() == null) {
			if (other.getKey() != null)
				return false;
		} else if (!getKey().equals(other.getKey()))
			return false;
		return true;
	}

	public Object getKey() {
		return key;
	}
	
	
}
