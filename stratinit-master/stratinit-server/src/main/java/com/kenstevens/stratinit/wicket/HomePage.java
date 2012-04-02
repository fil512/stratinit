package com.kenstevens.stratinit.wicket;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

import com.kenstevens.stratinit.wicket.docs.PlayPage;
import com.kenstevens.stratinit.wicket.docs.VideoPage;

public class HomePage extends AuthenticatedPage {
	private static final long serialVersionUID = 1L;

    public HomePage() {
    	if (isSignedIn()) {
    		add(new Label("username", getUsername()));
    	} else {
    		add(new Label("username", ""));
    	}

		add(new BookmarkablePageLink<Page>("RegistrationPage", RegistrationPage.class));
		add(new BookmarkablePageLink<Page>("VideoPage", VideoPage.class));
		add(new BookmarkablePageLink<Page>("PlayPage", PlayPage.class));
    }

}
