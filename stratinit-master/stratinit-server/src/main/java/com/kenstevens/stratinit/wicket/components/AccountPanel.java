package com.kenstevens.stratinit.wicket.components;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

import com.kenstevens.stratinit.wicket.RegistrationPage;
import com.kenstevens.stratinit.wicket.security.AuthenticatedPanel;

public class AccountPanel extends AuthenticatedPanel {
	private static final long serialVersionUID = 4303067574741765294L;

	public AccountPanel(String id) {
		super(id);	
		BookmarkablePageLink<Page> accountLink = new BookmarkablePageLink<Page>("AccountPage", RegistrationPage.class);
		add(accountLink);
		if (!isSignedIn()) {
			accountLink.setVisible(false);		}
	}
}
