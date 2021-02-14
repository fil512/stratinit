package com.kenstevens.stratinit.wicket.framework;

import com.kenstevens.stratinit.model.PlayerRole;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.markup.html.panel.Panel;


public abstract class AuthenticatedPanel extends Panel implements AuthenticatedComponent {

	private static final long serialVersionUID = 1L;

	public AuthenticatedPanel(String id) {
		super(id);
	}


	@Override
	public boolean isSignedIn() {
		return getAuth().isSignedIn();
	}

	@Override
	public boolean isAdmin() {
		return getAuth().getRoles().contains(PlayerRole.ROLE_ADMIN);
	}

	@Override
	public String getUsername() {
		return (String) getAuth().getAttribute("username");
	}

	private AbstractAuthenticatedWebSession getAuth() {
		return ((AbstractAuthenticatedWebSession) getSession());
	}
}
