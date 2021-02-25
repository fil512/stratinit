package com.kenstevens.stratinit.wicket.framework;

import org.apache.wicket.Application;
import org.apache.wicket.markup.html.link.Link;

public class HomeLink extends Link<String> {
	private static final long serialVersionUID = -7170985461867721263L;
	public HomeLink(String id) {
		super(id);
	}

	@Override
	public void onClick() {
		setResponsePage(Application.get().getHomePage());
	}

}