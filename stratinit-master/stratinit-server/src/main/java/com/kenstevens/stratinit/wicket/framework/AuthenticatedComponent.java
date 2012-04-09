package com.kenstevens.stratinit.wicket.framework;

public interface AuthenticatedComponent {

	boolean isSignedIn();

	boolean isAdmin();
	
	String getUsername();

}