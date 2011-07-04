package com.kenstevens.stratinit.web.admin;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.WebPage;

import com.kenstevens.stratinit.type.Constants;

public class AdminPage extends WebPage {
	private static final long serialVersionUID = 1L;

    public AdminPage(final PageParameters parameters) {
		add(new Label("version", Constants.SERVER_VERSION));
        // TODO Add your page's components here
    }
}
