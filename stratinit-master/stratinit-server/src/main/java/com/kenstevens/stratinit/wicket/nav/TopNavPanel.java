package com.kenstevens.stratinit.wicket.nav;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

import com.kenstevens.stratinit.wicket.admin.AdminPanel;
import com.kenstevens.stratinit.wicket.docs.PlayPage;
import com.kenstevens.stratinit.wicket.framework.AccountPanel;
import com.kenstevens.stratinit.wicket.framework.HomePage;

public class TopNavPanel extends Panel {
	private static final long serialVersionUID = 4303067574741765294L;

	public TopNavPanel(String id) {
		super(id);
		add(new BookmarkablePageLink<Page>("HomePage", HomePage.class));
		add(new AdminPanel("adminPanel"));
		add(new BookmarkablePageLink<Page>("PlayPage", PlayPage.class));
		add(new AccountPanel("accountPanel"));
		add(new LoginLogoutPanel("loginLogoutPanel"));
	}
}
