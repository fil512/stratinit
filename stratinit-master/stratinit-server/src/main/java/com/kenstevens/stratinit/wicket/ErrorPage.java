package com.kenstevens.stratinit.wicket;

import com.kenstevens.stratinit.wicket.components.HomeLink;


public class ErrorPage extends BasePage {
	private static final long serialVersionUID = 1L;

	public ErrorPage() {
		add(new HomeLink("homeLink"));
	}
}
