package com.kenstevens.stratinit.wicket.security;

import org.apache.wicket.markup.html.panel.Panel;

import com.kenstevens.stratinit.wicket.AuthenticatedSession;

public class AuthenticatedPanel extends Panel implements AuthenticatedComponent {

	private static final long serialVersionUID = 1L;

	public AuthenticatedPanel(String id) {
		super(id);
	}


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
