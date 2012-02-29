package com.kenstevens.stratinit.wicket.security;

import org.apache.wicket.markup.html.panel.Panel;

public class AuthenticatedPanel extends Panel implements AuthenticatedComponent {

	private static final long serialVersionUID = 1L;
	private static final Authenticator authenticator = new Authenticator();

	public AuthenticatedPanel(String id) {
		super(id);
	}

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
