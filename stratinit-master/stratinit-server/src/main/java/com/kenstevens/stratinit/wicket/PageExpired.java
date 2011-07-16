package com.kenstevens.stratinit.wicket;

import com.kenstevens.stratinit.wicket.components.HomeLink;

public class PageExpired extends BasePage {
	private static final long serialVersionUID = 1L;

	public PageExpired() {
		add(new HomeLink("homeLink"));
	}
}
