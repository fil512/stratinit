package com.kenstevens.stratinit.wicket;

import com.kenstevens.stratinit.wicket.components.HomeLink;

public class PageExpired extends BasePage {
	public PageExpired() {
		add(new HomeLink("homeLink"));
	}
}
