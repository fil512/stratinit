package com.kenstevens.stratinit.wicket;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.wicket.components.AdminPanel;
import com.kenstevens.stratinit.wicket.components.LoginLogoutPanel;
import com.kenstevens.stratinit.wicket.docs.AboutPage;
import com.kenstevens.stratinit.wicket.docs.ContactPage;
import com.kenstevens.stratinit.wicket.docs.FAQPage;
import com.kenstevens.stratinit.wicket.docs.GuidePage;
import com.kenstevens.stratinit.wicket.docs.PlayPage;
import com.kenstevens.stratinit.wicket.docs.RulesPage;
import com.kenstevens.stratinit.wicket.games.GamesPage;
import com.kenstevens.stratinit.wicket.games.PlayerRankPage;
import com.kenstevens.stratinit.wicket.games.TeamRankPage;
import com.kenstevens.stratinit.wicket.games.UnitsBuiltPage;

public class BasePage extends WebPage {
	private static final long serialVersionUID = 1L;

	public BasePage() {
		this(null);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
	}

	public BasePage(final PageParameters parameters) {
		super(parameters);
		add(new LoginLogoutPanel("loginLogoutPanel"));
		add(new AdminPanel("adminPanel"));
		add(new Label("version", Constants.SERVER_VERSION));
		add(new FeedbackPanel("feedback"));
		add(new BookmarkablePageLink<Page>("HomePage", HomePage.class));
		add(new BookmarkablePageLink<Page>("ContactPage", ContactPage.class));
		add(new BookmarkablePageLink<Page>("AboutPage", AboutPage.class));
		add(new BookmarkablePageLink<Page>("RegistrationPage", RegistrationPage.class));
		add(new BookmarkablePageLink<Page>("FAQPage", FAQPage.class));
		add(new BookmarkablePageLink<Page>("GuidePage", GuidePage.class));
		add(new BookmarkablePageLink<Page>("PlayPage", PlayPage.class));
		add(new BookmarkablePageLink<Page>("RulesPage", RulesPage.class));
		add(new BookmarkablePageLink<Page>("PlayerRankPage", PlayerRankPage.class));
		add(new BookmarkablePageLink<Page>("TeamRankPage", TeamRankPage.class));
		add(new BookmarkablePageLink<Page>("UnitsBuiltPage", UnitsBuiltPage.class));
		BookmarkablePageLink<Page> gamesPageLink = new BookmarkablePageLink<Page>("GamesPage", GamesPage.class);
		gamesPageLink.getPageParameters().add("mode", "active");
		add(gamesPageLink);
	}
	
    
	public AuthenticatedSession getAuthenticatedSession() {
		return (AuthenticatedSession) AuthenticatedSession.get();
	}
}
