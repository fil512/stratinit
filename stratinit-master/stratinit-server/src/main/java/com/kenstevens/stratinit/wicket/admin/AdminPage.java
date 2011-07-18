package com.kenstevens.stratinit.wicket.admin;

import com.kenstevens.stratinit.wicket.BasePage;

// FIXME test I need to login as admin
public class AdminPage extends BasePage {
	private static final long serialVersionUID = 1L;

	public AdminPage() {
		super();

		add(new ShutdownForm("shutdownForm"));
	}
}
