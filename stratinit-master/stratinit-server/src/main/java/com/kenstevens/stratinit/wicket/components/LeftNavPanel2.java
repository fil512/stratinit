package com.kenstevens.stratinit.wicket.components;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

import com.kenstevens.stratinit.wicket.RegistrationPage;
import com.kenstevens.stratinit.wicket.docs.AboutPage;
import com.kenstevens.stratinit.wicket.docs.ContactPage;
import com.kenstevens.stratinit.wicket.docs.FAQPage;
import com.kenstevens.stratinit.wicket.docs.GuidePage;
import com.kenstevens.stratinit.wicket.docs.RulesPage;
import com.kenstevens.stratinit.wicket.games.GamesPage;
import com.kenstevens.stratinit.wicket.games.PlayerRankPage;
import com.kenstevens.stratinit.wicket.games.TeamRankPage;
import com.kenstevens.stratinit.wicket.games.UnitsBuiltPage;
import com.kenstevens.stratinit.wicket.security.AuthenticatedPanel;

public class LeftNavPanel2 extends AuthenticatedPanel {
	private static final long serialVersionUID = 4303067574741765294L;

	public LeftNavPanel2(String id) {
		super(id);
		add(new BookmarkablePageLink<Page>("ContactPage", ContactPage.class));
		add(new BookmarkablePageLink<Page>("AboutPage", AboutPage.class));
		add(new BookmarkablePageLink<Page>("RegistrationPage", RegistrationPage.class));
		add(new BookmarkablePageLink<Page>("FAQPage", FAQPage.class));
		add(new BookmarkablePageLink<Page>("GuidePage", GuidePage.class));

		add(new BookmarkablePageLink<Page>("RulesPage", RulesPage.class));
		add(new BookmarkablePageLink<Page>("PlayerRankPage", PlayerRankPage.class));
		add(new BookmarkablePageLink<Page>("TeamRankPage", TeamRankPage.class));
		add(new BookmarkablePageLink<Page>("UnitsBuiltPage", UnitsBuiltPage.class));
		BookmarkablePageLink<Page> gamesPageLink = new BookmarkablePageLink<Page>("GamesPage", GamesPage.class);
		gamesPageLink.getPageParameters().add("mode", "active");
		add(gamesPageLink);

	}
}
