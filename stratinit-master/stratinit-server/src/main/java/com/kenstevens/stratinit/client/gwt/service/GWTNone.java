package com.kenstevens.stratinit.client.gwt.service;

import java.io.Serializable;


// Because Void doesn't implement Serializable.  Like how dumb is that?
public final class GWTNone implements Serializable {
	private static final long serialVersionUID = 1L;

	GWTNone() {}
}
