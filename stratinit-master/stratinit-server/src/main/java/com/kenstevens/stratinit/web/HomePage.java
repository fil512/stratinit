package com.kenstevens.stratinit.web;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;

import com.kenstevens.stratinit.type.Constants;

public class HomePage extends WebPage {
	private static final long serialVersionUID = 1L;

    public HomePage(final PageParameters parameters) {
		add(new Label("version", Constants.SERVER_VERSION));
        // TODO Add your page's components here
    }
}
