package com.kenstevens.stratinit.wicket;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

public class HomePage extends BasePage {
	private static final long serialVersionUID = 1L;

    public HomePage() {
    	add(new Label("username", getAuthenticatedSession().getUsername()));

		// FIXME Really??
		add(new BookmarkablePageLink<Page>("RegistrationPage2", RegistrationPage.class));
    }

}
