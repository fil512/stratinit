package com.kenstevens.stratinit.wicket.admin;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

import com.kenstevens.stratinit.wicket.framework.AuthenticatedPanel;

public class AdminPanel extends AuthenticatedPanel {
	private static final long serialVersionUID = 4303067574741765294L;

	public AdminPanel(String id) {
		super(id);	
		BookmarkablePageLink<Page> adminLink = new BookmarkablePageLink<Page>("AdminPage", AdminPage.class);
		add(adminLink);
		adminLink.setVisible(isAdmin());
	}
}
