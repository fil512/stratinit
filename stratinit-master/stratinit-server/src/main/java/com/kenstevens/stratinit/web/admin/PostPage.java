package com.kenstevens.stratinit.web.admin;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

public class PostPage extends WebPage {
	private static final long serialVersionUID = 1L;

	public PostPage(final PageParameters parameters) {
		add(new PostForm("postForm"));
		add(new FeedbackPanel("feedback"));
	}
}
