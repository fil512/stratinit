package com.kenstevens.stratinit.wicket.admin;

import com.kenstevens.stratinit.wicket.base.BasePage;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

// FIXME TEST that I need to login as admin
public class AdminPage extends BasePage {
	private static final long serialVersionUID = 1L;

	public AdminPage() {
		super();
		add(new BookmarkablePageLink<Page>("PostPage", PostPage.class));
		add(new BookmarkablePageLink<Page>("PlayerListPage", PlayerListPage.class));
		add(new ShutdownForm("shutdownForm"));
	}
	
	
}
