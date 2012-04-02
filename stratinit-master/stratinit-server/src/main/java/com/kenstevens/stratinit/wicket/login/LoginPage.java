package com.kenstevens.stratinit.wicket.login;

import org.apache.wicket.Page;
import org.apache.wicket.authroles.authentication.panel.SignInPanel;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.kenstevens.stratinit.wicket.BasePage;

public class LoginPage extends BasePage {
	private static final long serialVersionUID = 1L;

	static final String LOGIN_MESSAGE = "Please log in.";

	public LoginPage() {
		this(null);
	}

	public LoginPage(final PageParameters parameters) {
		super(parameters);
		add(new SignInPanel("signInPanel"));
		add (new BookmarkablePageLink<Page>("ForgottenPasswordPage", ForgottenPasswordPage.class));
	}
}
