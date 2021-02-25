package com.kenstevens.stratinit.wicket.nav;

import com.kenstevens.stratinit.wicket.framework.AuthenticatedPanel;
import com.kenstevens.stratinit.wicket.framework.HomeLink;
import com.kenstevens.stratinit.wicket.framework.LoginPage;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

public class LoginLogoutPanel extends AuthenticatedPanel {
	private static final long serialVersionUID = 4303067574741765294L;

	public LoginLogoutPanel(String id) {
		super(id);	
		BookmarkablePageLink<Page> loginLink = new BookmarkablePageLink<Page>("LoginPage", LoginPage.class);
		add(loginLink);
		loginLink.setVisible(!isSignedIn());	
		HomeLink homeLink = new HomeLink("signOut") {
			private static final long serialVersionUID = -6317024681157531298L;

			@Override
			public void onClick() {
				getSession().invalidate();
				super.onClick();
			}
		};
		add(homeLink);
		homeLink.setVisible(isSignedIn());
	}
}
