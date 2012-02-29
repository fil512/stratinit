package com.kenstevens.stratinit.wicket.security;

import com.kenstevens.stratinit.model.PlayerRole;
import com.kenstevens.stratinit.wicket.AuthenticatedSession;

public class Authenticator implements AuthenticatedComponent {
	@Override
	public boolean isSignedIn() {
		return getAuthenticatedSession().isSignedIn();
	}

	@Override
	public boolean isAdmin() {
		return getAuthenticatedSession().getRoles().contains(PlayerRole.ROLE_ADMIN);
	}
	
	private AuthenticatedSession getAuthenticatedSession() {
		return (AuthenticatedSession) AuthenticatedSession.get();
	}

	@Override
	public String getUsername() {
		return getAuthenticatedSession().getUsername();
	}
}
