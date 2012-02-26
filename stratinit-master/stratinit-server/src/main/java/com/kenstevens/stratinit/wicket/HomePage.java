package com.kenstevens.stratinit.wicket;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

import com.kenstevens.stratinit.wicket.docs.PlayPage;

public class HomePage extends BasePage {
	private static final long serialVersionUID = 1L;

    public HomePage() {
    	AuthenticatedSession authenticatedSession = getAuthenticatedSession();
    	if (authenticatedSession.isSignedIn()) {
    		add(new Label("username", authenticatedSession.getUsername()));
    	} else {
    		add(new Label("username", ""));
    	}

		// TODO Really??
		add(new BookmarkablePageLink<Page>("RegistrationPage2", RegistrationPage.class));
		add(new BookmarkablePageLink<Page>("PlayPage2", PlayPage.class));
    }

}
