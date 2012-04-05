package com.kenstevens.stratinit.wicket.base;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.kenstevens.stratinit.type.Constants;

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
}
