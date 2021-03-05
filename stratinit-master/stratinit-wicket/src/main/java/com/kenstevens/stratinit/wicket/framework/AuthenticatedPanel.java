package com.kenstevens.stratinit.wicket.framework;

import com.kenstevens.stratinit.client.model.PlayerRole;
import org.apache.wicket.markup.html.panel.Panel;


public abstract class AuthenticatedPanel extends Panel implements AuthenticatedComponent {

	private static final long serialVersionUID = 1L;

	public AuthenticatedPanel(String id) {
		super(id);
	}


	@Override
	public boolean isSignedIn() {
		return AuthHelper.isSignedIn();
	}

	@Override
	public boolean isAdmin() {
		return AuthHelper.getRoles().contains(PlayerRole.ROLE_ADMIN);
	}

	@Override
	public String getUsername() {
		return AuthHelper.getUsername();
	}
}
