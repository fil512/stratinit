package com.kenstevens.stratinit.wicket;

import org.apache.wicket.authroles.authentication.panel.SignInPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class LoginPage extends BasePage {
	private static final long serialVersionUID = 1L;

	static final String LOGIN_MESSAGE = "Please log in.";

	public LoginPage() {
		this(null);
	}

	public LoginPage(final PageParameters parameters) {
		super(parameters);
		add(new SignInPanel("signInPanel"));
	}
}