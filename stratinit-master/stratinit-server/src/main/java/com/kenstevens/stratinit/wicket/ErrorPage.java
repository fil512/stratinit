package com.kenstevens.stratinit.wicket;

import com.kenstevens.stratinit.wicket.components.HomeLink;


public class ErrorPage extends BasePage {
	public ErrorPage() {
		add(new HomeLink("homeLink"));
	}
}
