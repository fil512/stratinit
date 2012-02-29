package com.kenstevens.stratinit.wicket;

import com.kenstevens.stratinit.wicket.security.AuthenticatedComponent;
import com.kenstevens.stratinit.wicket.security.Authenticator;

public class AuthenticatedPage extends BasePage implements AuthenticatedComponent {

	private static final long serialVersionUID = -8358571925296406622L;
	
	private static final Authenticator authenticator = new Authenticator();

	@Override
	public boolean isSignedIn() {
		return authenticator.isSignedIn();
	}

	@Override
	public boolean isAdmin() {
		return authenticator.isAdmin();
	}

	@Override
	public String getUsername() {
		return authenticator.getUsername();
	}

}
