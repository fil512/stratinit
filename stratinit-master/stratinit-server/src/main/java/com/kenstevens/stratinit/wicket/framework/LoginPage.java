package com.kenstevens.stratinit.wicket.framework;

import com.kenstevens.stratinit.wicket.base.BasePage;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
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

	private class LoginForm extends Form<LoginForm> {

		private String username;

		private String password;

		public LoginForm(String id) {
			super(id);
			setModel(new CompoundPropertyModel<>(this));
			add(new FeedbackPanel("feedback"));
			add(new RequiredTextField<String>("username"));
			add(new PasswordTextField("password"));
		}

		@Override
		protected void onSubmit() {
			AuthenticatedWebSession session = AuthenticatedWebSession.get();
			if (session.signIn(username, password)) {
				setResponsePage(HomePage.class);
			} else {
				error("Login failed");
			}
		}
	}
}
