package com.kenstevens.stratinit.wicket;

import org.apache.wicket.markup.html.basic.Label;

public class HomePage extends BasePage {
	private static final long serialVersionUID = 1L;

    public HomePage() {
    	add(new Label("username", getAuthenticatedSession().getUsername()));
    }
    
	public AuthenticatedSession getAuthenticatedSession() {
		return (AuthenticatedSession) AuthenticatedSession.get();
	}
}
