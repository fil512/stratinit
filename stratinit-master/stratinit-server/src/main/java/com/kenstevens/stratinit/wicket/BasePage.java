package com.kenstevens.stratinit.wicket;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.wicket.components.SignOutPanel;

public class BasePage extends WebPage {
	private static final long serialVersionUID = 1L;

	public BasePage() {
		this(null);
	}
	
    public BasePage(final PageParameters parameters) {
    	add(new SignOutPanel("signOutPanel"));
		add(new Label("version", Constants.SERVER_VERSION));
    }
}
