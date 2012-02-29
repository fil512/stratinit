package com.kenstevens.stratinit.wicket;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

import com.kenstevens.stratinit.wicket.docs.PlayPage;

public class HomePage extends AuthenticatedPage {
	private static final long serialVersionUID = 1L;

    public HomePage() {
    	if (isSignedIn()) {
    		add(new Label("username", getUsername()));
    	} else {
    		add(new Label("username", ""));
    	}

		// TODO Really??
		add(new BookmarkablePageLink<Page>("RegistrationPage2", RegistrationPage.class));
		add(new BookmarkablePageLink<Page>("PlayPage2", PlayPage.class));
    }

}
