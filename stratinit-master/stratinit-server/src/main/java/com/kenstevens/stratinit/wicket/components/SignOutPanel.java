package com.kenstevens.stratinit.wicket.components;

import org.apache.wicket.markup.html.panel.Panel;

import com.kenstevens.stratinit.wicket.AuthenticatedSession;

public class SignOutPanel extends Panel {
	private static final long serialVersionUID = 4303067574741765294L;

	public SignOutPanel(String id) {
		super(id);		
		add(new HomeLink("signOut") {
			private static final long serialVersionUID = -6317024681157531298L;

			@Override
			public void onClick() {
				getSession().invalidate();
				super.onClick();
			}
		});
		setVisible(canShow());		
	}

	public boolean canShow() {
		return AuthenticatedSession.get().isSignedIn();
	}

}
