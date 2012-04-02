package com.kenstevens.stratinit.wicket.components;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

import com.kenstevens.stratinit.wicket.docs.AboutPage;
import com.kenstevens.stratinit.wicket.docs.ContactPage;
import com.kenstevens.stratinit.wicket.docs.FAQPage;
import com.kenstevens.stratinit.wicket.docs.GuidePage;
import com.kenstevens.stratinit.wicket.docs.RulesPage;
import com.kenstevens.stratinit.wicket.game.GamesPage;
import com.kenstevens.stratinit.wicket.login.RegistrationPage;
import com.kenstevens.stratinit.wicket.rank.PlayerRankPage;
import com.kenstevens.stratinit.wicket.rank.TeamRankPage;
import com.kenstevens.stratinit.wicket.unit.UnitsBuiltPage;

public class LeftNavPanel extends Panel {
	private static final long serialVersionUID = 4303067574741765294L;

	public LeftNavPanel(String id) {
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
