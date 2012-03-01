package com.kenstevens.stratinit.wicket;

import com.kenstevens.stratinit.wicket.security.AuthenticatedComponent;

public class AuthenticatedPage extends BasePage implements AuthenticatedComponent {

	private static final long serialVersionUID = -8358571925296406622L;
	
	private static final AuthenticatedSession session = (AuthenticatedSession) AuthenticatedSession.get();

	@Override
	public boolean isSignedIn() {
		return session.isSignedIn();
	}

	@Override
	public boolean isAdmin() {
		return session.isAdmin();
	}

	@Override
	public String getUsername() {
		return session.getUsername();
	}

}
