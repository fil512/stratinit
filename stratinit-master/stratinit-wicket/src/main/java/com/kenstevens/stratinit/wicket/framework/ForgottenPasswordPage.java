package com.kenstevens.stratinit.wicket.framework;

import com.kenstevens.stratinit.wicket.base.BasePage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class ForgottenPasswordPage extends BasePage {
	private static final long serialVersionUID = 1L;

	static final String LOGIN_MESSAGE = "Please log in.";

	public ForgottenPasswordPage() {
		this(null);
	}

	public ForgottenPasswordPage(final PageParameters parameters) {
		super(parameters);
		add(new ForgottenPasswordForm("forgottenPasswordForm"));
	}
}
