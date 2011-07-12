package com.kenstevens.stratinit.wicket.admin;

import com.kenstevens.stratinit.wicket.BasePage;

public class PostPage extends BasePage {
	private static final long serialVersionUID = 1L;

	public PostPage() {
		super();
		add(new PostForm("postForm"));
	}
}
