package com.kenstevens.stratinit.wicket.security;

import org.apache.wicket.markup.html.panel.Panel;

import com.kenstevens.stratinit.wicket.AuthenticatedSession;

public class AuthenticatedPanel extends Panel implements AuthenticatedComponent {

	private static final long serialVersionUID = 1L;
	private static final AuthenticatedSession session = (AuthenticatedSession) AuthenticatedSession.get();

	public AuthenticatedPanel(String id) {
		super(id);
	}


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
