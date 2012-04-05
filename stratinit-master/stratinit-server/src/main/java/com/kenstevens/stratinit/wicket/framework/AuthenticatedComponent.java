package com.kenstevens.stratinit.wicket.framework;

public interface AuthenticatedComponent {

	public abstract boolean isSignedIn();

	public abstract boolean isAdmin();
	
	public abstract String getUsername();

}