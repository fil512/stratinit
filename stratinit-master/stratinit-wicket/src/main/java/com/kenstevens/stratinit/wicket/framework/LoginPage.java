package com.kenstevens.stratinit.wicket.framework;

import com.kenstevens.stratinit.wicket.base.BasePage;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class LoginPage extends BasePage {
	private static final long serialVersionUID = 1L;

	static final String LOGIN_MESSAGE = "Please log in.";

	public LoginPage() {
		this(null);
	}

	public LoginPage(final PageParameters parameters) {
		super(parameters);
		if (getAuth().isSignedIn()) {
			continueToOriginalDestination();
		}
		add(new LoginForm("loginForm"));
		add(new BookmarkablePageLink<Page>("ForgottenPasswordPage", ForgottenPasswordPage.class));
	}

}
