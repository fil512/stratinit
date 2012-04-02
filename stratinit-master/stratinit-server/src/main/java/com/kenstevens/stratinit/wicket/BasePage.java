package com.kenstevens.stratinit.wicket;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.kenstevens.stratinit.type.Constants;
import com.kenstevens.stratinit.wicket.components.GoogleAnalyticsPanel;
import com.kenstevens.stratinit.wicket.components.LeftNavPanel;
import com.kenstevens.stratinit.wicket.components.TopNavPanel;

public class BasePage extends WebPage {
	private static final long serialVersionUID = 1L;

	public BasePage() {
		this(null);
	}

	public BasePage(final PageParameters parameters) {
		super(parameters);
		add(new TopNavPanel("topNavPanel"));
		add(new LeftNavPanel("leftNavPanel"));
		add(new FeedbackPanel("feedback"));
		add(new GoogleAnalyticsPanel("googleAnalyticsPanel"));
		add(new Label("version", Constants.SERVER_VERSION));
	}
}
