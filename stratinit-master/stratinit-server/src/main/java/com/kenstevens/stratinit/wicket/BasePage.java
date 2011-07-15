package com.kenstevens.stratinit.wicket;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.wicket.components.SignOutPanel;
import com.kenstevens.stratinit.wicket.docs.AboutPage;
import com.kenstevens.stratinit.wicket.docs.ContactPage;

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
		add(new SignOutPanel("signOutPanel"));
		add(new Label("version", Constants.SERVER_VERSION));
		add(new FeedbackPanel("feedback"));
		add(new BookmarkablePageLink<Page>("HomePage", HomePage.class));
		add(new BookmarkablePageLink<Page>("ContactPage", ContactPage.class));
		add(new BookmarkablePageLink<Page>("AboutPage", AboutPage.class));
		add(new BookmarkablePageLink<Page>("RegistrationPage", RegistrationPage.class));
		add(new BookmarkablePageLink<Page>("LoginPage", LoginPage.class));
	}
}
