package com.kenstevens.stratinit.wicket.framework;

import org.apache.wicket.markup.html.panel.Panel;


public abstract class AuthenticatedPanel extends Panel implements AuthenticatedComponent {

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
