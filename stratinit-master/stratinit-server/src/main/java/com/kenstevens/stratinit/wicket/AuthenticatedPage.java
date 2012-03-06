package com.kenstevens.stratinit.wicket;

import com.kenstevens.stratinit.wicket.security.AuthenticatedComponent;

public class AuthenticatedPage extends BasePage implements AuthenticatedComponent {

	private static final long serialVersionUID = -8358571925296406622L;
	
	@Override
	public boolean isSignedIn() {
		return AuthenticatedSession.get().isSignedIn();
	}

	@Override
	public boolean isAdmin() {
		return AuthenticatedSession.getSession().isAdmin();
	}

	@Override
	public String getUsername() {
		return AuthenticatedSession.getSession().getUsername();
	}

}