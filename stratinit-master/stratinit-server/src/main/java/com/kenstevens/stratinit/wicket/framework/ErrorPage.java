package com.kenstevens.stratinit.wicket.framework;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

import com.kenstevens.stratinit.wicket.base.BasePage;


public class ErrorPage extends BasePage {
	private static final long serialVersionUID = 1L;

	public ErrorPage() {
		add(new BookmarkablePageLink<Page>("HomePage", HomePage.class));
	}
}
