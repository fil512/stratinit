package com.kenstevens.stratinit.wicket.base;

import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.wicket.nav.NavProvider;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

public abstract class BasePage extends WebPage {
	private static final long serialVersionUID = 1L;
	@SpringBean
	NavProvider navProvider;

	public BasePage() {
		this(null);
	}

	public BasePage(final PageParameters parameters) {
		super(parameters);
		add(navProvider.getTopNavPanel());
		add(navProvider.getLeftNavPanel());
		add(navProvider.getAnalyticsPanel());
		add(new FeedbackPanel("feedback"));
		add(new Label("version", Constants.SERVER_VERSION));
	}

	protected AbstractAuthenticatedWebSession getAuth() {
		return ((AbstractAuthenticatedWebSession) getSession());
	}
}
