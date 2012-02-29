package com.kenstevens.stratinit.wicket;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

public class PageExpired extends BasePage {
	private static final long serialVersionUID = 1L;

	public PageExpired() {
		add(new BookmarkablePageLink<Page>("HomePage", HomePage.class));
	}
}
