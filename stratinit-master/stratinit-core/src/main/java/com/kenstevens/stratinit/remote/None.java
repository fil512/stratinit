package com.kenstevens.stratinit.remote;

import java.io.Serializable;


public final class None implements Serializable {
	
	private None() {}

	private static final long serialVersionUID = -6950474827761742121L;
	// Because Void doesn't implement Serializable.  Like how dumb is that?

	public static None getInstance() {
		return new None();
	}
}
