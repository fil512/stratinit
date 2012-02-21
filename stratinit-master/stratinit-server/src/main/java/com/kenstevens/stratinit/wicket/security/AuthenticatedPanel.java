package com.kenstevens.stratinit.wicket.security;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.panel.Panel;

import com.kenstevens.stratinit.model.PlayerRole;
import com.kenstevens.stratinit.wicket.AuthenticatedSession;

public class AuthenticatedPanel extends Panel {

	private static final long serialVersionUID = 1L;

	public AuthenticatedPanel(String id) {
		super(id);
	}

	public boolean isSignedIn() {
		return getAuthenticatedSession().isSignedIn();
	}

	public boolean isAdmin() {
		return getAuthenticatedSession().getRoles().contains(PlayerRole.ROLE_ADMIN);
	}
	
	private AuthenticatedWebSession getAuthenticatedSession() {
		return AuthenticatedSession.get();
	}
}
