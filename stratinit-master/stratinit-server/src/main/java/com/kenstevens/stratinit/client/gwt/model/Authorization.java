package com.kenstevens.stratinit.client.gwt.model;

import java.io.Serializable;


public class Authorization implements Serializable {
	private static final long serialVersionUID = -5213973779489516553L;

	private boolean isAdmin = false;
	private String username;
	public boolean isAdmin() {
		return isAdmin;
	}
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

}
